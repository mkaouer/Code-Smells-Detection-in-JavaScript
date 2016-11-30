/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/logger.h>
#include <osquery/tables.h>

#include <IOKit/IOMessage.h>

#include "osquery/core/conversions.h"
#include "osquery/events/darwin/iokit.h"
#include "osquery/tables/system/darwin/iokit_utils.h"

namespace osquery {

REGISTER(IOKitEventPublisher, "event_publisher", "iokit");

struct DeviceTracker : private boost::noncopyable {
 public:
  IOKitEventPublisher* publisher{nullptr};
  io_object_t notification{0};

  explicit DeviceTracker(IOKitEventPublisher* p) : publisher(p) {}
};

void IOKitEventPublisher::restart() {
  if (run_loop_ == nullptr) {
    // There is no run loop to restart.
    return;
  }

  // Remove any existing stream.
  stop();

  port_ = IONotificationPortCreate(kIOMasterPortDefault);
  // Get a run loop source from the created IOKit notification port.
  auto run_loop_source = IONotificationPortGetRunLoopSource(port_);
  CFRunLoopAddSource(run_loop_, run_loop_source, kCFRunLoopDefaultMode);

  std::vector<const std::string*> device_classes = {
      &tables::kIOUSBDeviceClassName_,
      &tables::kIOPCIDeviceClassName_,
      &tables::kIOPlatformExpertDeviceClassName_,
      &tables::kIOACPIPlatformDeviceClassName_,
      &tables::kIOPlatformDeviceClassname_,
  };

  publisher_started_ = false;
  for (const auto& class_name : device_classes) {
    // Service matching is USB for now, must find a way to get more!
    // Can provide a "IOPCIDevice" here too.
    auto matches = IOServiceMatching(class_name->c_str());

    // Register attach/detaches (could use kIOPublishNotification).
    // Notification types are defined in IOKitKeys.
    IOReturn result = IOServiceAddMatchingNotification(
        port_,
        kIOFirstMatchNotification,
        matches,
        (IOServiceMatchingCallback)deviceAttach,
        this,
        &iterator_);
    if (result == kIOReturnSuccess) {
      deviceAttach(this, iterator_);
    }
  }
  publisher_started_ = true;
}

void IOKitEventPublisher::newEvent(const io_service_t& device,
                                   IOKitEventContext::Action action) {
  auto ec = createEventContext();
  ec->action = action;

  {
    // The IORegistry name is not needed.
    io_name_t class_name = {0};
    if (IOObjectGetClass(device, class_name) != kIOReturnSuccess) {
      return;
    }
    ec->type = std::string(class_name);
  }

  // Get the device details
  CFMutableDictionaryRef details;
  IORegistryEntryCreateCFProperties(
      device, &details, kCFAllocatorDefault, kNilOptions);
  if (ec->type == tables::kIOUSBDeviceClassName_) {
    ec->path = tables::getIOKitProperty(details, "USB Address") + ":";
    ec->path += tables::getIOKitProperty(details, "PortNum");
    ec->model = tables::getIOKitProperty(details, "USB Product Name");
    ec->model_id = tables::getIOKitProperty(details, "idProduct");
    ec->vendor = tables::getIOKitProperty(details, "USB Vendor Name");
    ec->vendor_id = tables::getIOKitProperty(details, "idVendor");
    tables::idToHex(ec->vendor_id);
    tables::idToHex(ec->model_id);
    ec->serial = tables::getIOKitProperty(details, "USB Serial Number");
    if (ec->serial.size() == 0) {
      ec->serial = tables::getIOKitProperty(details, "iSerialNumber");
    }
    ec->version = "";
    ec->driver = tables::getIOKitProperty(details, "IOUserClientClass");
  } else if (ec->type == tables::kIOPCIDeviceClassName_) {
    auto compatible = tables::getIOKitProperty(details, "compatible");
    auto properties = tables::IOKitPCIProperties(compatible);
    ec->model_id = properties.model_id;
    ec->vendor_id = properties.vendor_id;
    ec->driver = properties.driver;
    if (ec->driver.empty()) {
      ec->driver = tables::getIOKitProperty(details, "IOName");
    }

    ec->path = tables::getIOKitProperty(details, "pcidebug");
    ec->version = tables::getIOKitProperty(details, "revision-id");
    ec->model = tables::getIOKitProperty(details, "model");
  } else {
    // Get the name as the model.
    io_name_t name = {0};
    IORegistryEntryGetName(device, name);
    if (name[0] != 0) {
      ec->model = std::string(name);
    }
  }

  CFRelease(details);
  fire(ec);
}

void IOKitEventPublisher::deviceAttach(void* refcon, io_iterator_t iterator) {
  auto self = (IOKitEventPublisher*)refcon;
  io_service_t device;
  // The iterator may also have become invalid due to a change in the registry.
  // It is possible to reiterate devices, but that will cause duplicate events.
  while ((device = IOIteratorNext(iterator))) {
    // Create a notification tracker.
    {
      std::lock_guard<std::mutex> lock(self->notification_mutex_);
      auto tracker = std::make_shared<struct DeviceTracker>(self);
      self->devices_.push_back(tracker);
      IOServiceAddInterestNotification(self->port_,
                                       device,
                                       kIOGeneralInterest,
                                       (IOServiceInterestCallback)deviceDetach,
                                       tracker.get(),
                                       &(tracker->notification));
    }
    if (self->publisher_started_) {
      self->newEvent(device, IOKitEventContext::Action::DEVICE_ATTACH);
    }
    IOObjectRelease(device);
  }
}

void IOKitEventPublisher::deviceDetach(void* refcon,
                                       io_service_t device,
                                       natural_t message_type,
                                       void*) {
  if (message_type != kIOMessageServiceIsTerminated) {
    // This is an unexpected notification.
    return;
  }

  auto* tracker = (struct DeviceTracker*)refcon;
  auto* self = tracker->publisher;
  // The device tracker allows us to emit using the publisher and release the
  // notification created for this device.
  self->newEvent(device, IOKitEventContext::Action::DEVICE_DETACH);
  IOObjectRelease(device);

  {
    std::lock_guard<std::mutex> lock(self->notification_mutex_);
    // Remove the device tracker.
    IOObjectRelease(tracker->notification);
    auto it = self->devices_.begin();
    while (it != self->devices_.end()) {
      if ((*it)->notification == tracker->notification) {
        self->devices_.erase(it);
        return;
      }
      it++;
    }
  }
}

Status IOKitEventPublisher::run() {
  // The run entrypoint executes in a dedicated thread.
  if (run_loop_ == nullptr) {
    run_loop_ = CFRunLoopGetCurrent();
    // Restart the stream creation.
    restart();
  }

  // Start the run loop, it may be removed with a tearDown.
  CFRunLoopRun();

  // Add artificial latency to run loop.
  osquery::publisherSleep(1000);
  return Status(0, "OK");
}

bool IOKitEventPublisher::shouldFire(const IOKitSubscriptionContextRef& sc,
                                     const IOKitEventContextRef& ec) const {
  if (!sc->type.empty() && sc->type != ec->type) {
    return false;
  } else if (!sc->model_id.empty() && sc->model_id != ec->model_id) {
    return false;
  } else if (!sc->vendor_id.empty() && sc->vendor_id != ec->vendor_id) {
    return false;
  }

  return true;
}

void IOKitEventPublisher::stop() {
  // Stop the run loop.
  if (run_loop_ != nullptr) {
    CFRunLoopStop(run_loop_);
  }
}

void IOKitEventPublisher::tearDown() {
  stop();

  // Do not keep a reference to the run loop.
  run_loop_ = nullptr;
}
}

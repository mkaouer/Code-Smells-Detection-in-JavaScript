/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant 
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <stdio.h>

#include <boost/filesystem/operations.hpp>
#include <boost/filesystem/path.hpp>
#include <boost/thread.hpp>

#include <gtest/gtest.h>

#include <osquery/events.h>
#include <osquery/filesystem.h>
#include <osquery/tables.h>

#include "osquery/events/linux/inotify.h"

namespace osquery {

const std::string kRealTestPath = "/tmp/osquery-inotify-trigger";
const std::string kRealTestDir = "/tmp/osquery-inotify-triggers";
const std::string kRealTestDirPath = "/tmp/osquery-inotify-triggers/1";
const std::string kRealTestSubDir = "/tmp/osquery-inotify-triggers/2";
const std::string kRealTestSubDirPath = "/tmp/osquery-inotify-triggers/2/1";

int kMaxEventLatency = 3000;

class INotifyTests : public testing::Test {
 protected:
  void TearDown() {
    EventFactory::deregisterEventPublishers();
    boost::filesystem::remove_all(kRealTestPath);
    boost::filesystem::remove_all(kRealTestDir);
  }

  void StartEventLoop() {
    event_pub_ = std::make_shared<INotifyEventPublisher>();
    EventFactory::registerEventPublisher(event_pub_);
    FILE* fd = fopen(kRealTestPath.c_str(), "w");
    fclose(fd);

    temp_thread_ = boost::thread(EventFactory::run, "INotifyEventPublisher");
  }

  void SubscriptionAction(const std::string& path,
                          uint32_t mask = 0,
                          EventCallback ec = 0) {
    auto mc = std::make_shared<INotifySubscriptionContext>();
    mc->path = path;
    mc->mask = mask;

    EventFactory::addSubscription("INotifyEventPublisher", mc, ec);
  }

  bool WaitForEvents(int max, int num_events = 0) {
    int delay = 0;
    while (delay <= max * 1000) {
      if (num_events > 0 && event_pub_->numEvents() >= num_events) {
        return true;
      } else if (num_events == 0 && event_pub_->numEvents() > 0) {
        return true;
      }
      delay += 50;
      ::usleep(50);
    }
    return false;
  }

  void TriggerEvent(const std::string& path) {
    FILE* fd = fopen(path.c_str(), "w");
    fputs("inotify", fd);
    fclose(fd);
  }

  void EndEventLoop() {
    EventFactory::end();
    event_pub_->tearDown();
    temp_thread_.join();
    EventFactory::end(false);
  }

  std::shared_ptr<INotifyEventPublisher> event_pub_;
  boost::thread temp_thread_;
};

TEST_F(INotifyTests, test_register_event_pub) {
  auto status = EventFactory::registerEventPublisher<INotifyEventPublisher>();
  EXPECT_TRUE(status.ok());

  // Make sure only one event type exists
  EXPECT_EQ(EventFactory::numEventPublishers(), 1);
}

TEST_F(INotifyTests, test_inotify_init) {
  // Handle should not be initialized during ctor.
  auto event_pub = std::make_shared<INotifyEventPublisher>();
  EXPECT_FALSE(event_pub->isHandleOpen());

  // Registering the event type initializes inotify.
  EventFactory::registerEventPublisher(event_pub);
  EXPECT_TRUE(event_pub->isHandleOpen());

  // Similarly deregistering closes the handle.
  EventFactory::deregisterEventPublishers();
  EXPECT_FALSE(event_pub->isHandleOpen());
}

TEST_F(INotifyTests, test_inotify_add_subscription_missing_path) {
  EventFactory::registerEventPublisher<INotifyEventPublisher>();

  // This subscription path is fake, and will succeed.
  auto mc = std::make_shared<INotifySubscriptionContext>();
  mc->path = "/this/path/is/fake";

  auto subscription = Subscription::create(mc);
  auto status =
      EventFactory::addSubscription("INotifyEventPublisher", subscription);
  EXPECT_TRUE(status.ok());
}

TEST_F(INotifyTests, test_inotify_add_subscription_success) {
  EventFactory::registerEventPublisher<INotifyEventPublisher>();

  // This subscription path *should* be real.
  auto mc = std::make_shared<INotifySubscriptionContext>();
  mc->path = "/";

  auto subscription = Subscription::create(mc);
  auto status =
      EventFactory::addSubscription("INotifyEventPublisher", subscription);
  EXPECT_TRUE(status.ok());
}

TEST_F(INotifyTests, test_inotify_run) {
  // Assume event type is registered.
  event_pub_ = std::make_shared<INotifyEventPublisher>();
  EventFactory::registerEventPublisher(event_pub_);

  // Create a temporary file to watch, open writeable
  FILE* fd = fopen(kRealTestPath.c_str(), "w");

  // Create a subscriptioning context
  auto mc = std::make_shared<INotifySubscriptionContext>();
  mc->path = kRealTestPath;
  EventFactory::addSubscription("INotifyEventPublisher",
                                Subscription::create(mc));

  // Create an event loop thread (similar to main)
  boost::thread temp_thread(EventFactory::run, "INotifyEventPublisher");
  EXPECT_TRUE(event_pub_->numEvents() == 0);

  // Cause an inotify event by writing to the watched path.
  fputs("inotify", fd);
  fclose(fd);

  // Wait for the thread's run loop to select.
  WaitForEvents(kMaxEventLatency);
  EXPECT_TRUE(event_pub_->numEvents() > 0);

  // Cause the thread to tear down.
  EventFactory::end();
  temp_thread.join();
  // Reset the event factory state.
  EventFactory::end(false);
}

class TestINotifyEventSubscriber
    : public EventSubscriber<INotifyEventPublisher> {
  DECLARE_SUBSCRIBER("TestINotifyEventSubscriber");

 public:
  void init() { callback_count_ = 0; }
  Status SimpleCallback(const INotifyEventContextRef& ec) {
    callback_count_ += 1;
    return Status(0, "OK");
  }

  Status Callback(const INotifyEventContextRef& ec) {
    // The following comments are an example Callback routine.
    // Row r;
    // r["action"] = ec->action;
    // r["path"] = ec->path;

    // Normally would call Add here.
    actions_.push_back(ec->action);
    callback_count_ += 1;
    return Status(0, "OK");
  }

  SCRef GetSubscription(const std::string& path, uint32_t mask = 0) {
    auto mc = createSubscriptionContext();
    mc->path = path;
    mc->mask = mask;
    return mc;
  }

  void WaitForEvents(int max, int num_events = 1) {
    int delay = 0;
    while (delay < max * 1000) {
      if (callback_count_ >= num_events) {
        return;
      }
      ::usleep(50);
      delay += 50;
    }
  }

  std::vector<std::string> actions() { return actions_; }

  int count() { return callback_count_; }

 public:
  int callback_count_;
  std::vector<std::string> actions_;
};

TEST_F(INotifyTests, test_inotify_fire_event) {
  // Assume event type is registered.
  StartEventLoop();
  auto sub = std::make_shared<TestINotifyEventSubscriber>();
  sub->init();

  // Create a subscriptioning context, note the added Event to the symbol
  auto sc = sub->GetSubscription(kRealTestPath, 0);
  sub->subscribe(&TestINotifyEventSubscriber::SimpleCallback, sc);

  TriggerEvent(kRealTestPath);
  sub->WaitForEvents(kMaxEventLatency);

  // Make sure our expected event fired (aka subscription callback was called).
  EXPECT_TRUE(sub->count() > 0);

  // Cause the thread to tear down.
  EndEventLoop();
}

TEST_F(INotifyTests, test_inotify_event_action) {
  // Assume event type is registered.
  StartEventLoop();
  auto sub = std::make_shared<TestINotifyEventSubscriber>();
  sub->init();

  auto sc = sub->GetSubscription(kRealTestPath, 0);
  sub->subscribe(&TestINotifyEventSubscriber::Callback, sc);

  TriggerEvent(kRealTestPath);
  sub->WaitForEvents(kMaxEventLatency, 4);

  // Make sure the inotify action was expected.
  EXPECT_EQ(sub->actions().size(), 4);
  EXPECT_EQ(sub->actions()[0], "UPDATED");
  EXPECT_EQ(sub->actions()[1], "OPENED");
  EXPECT_EQ(sub->actions()[2], "UPDATED");
  EXPECT_EQ(sub->actions()[3], "UPDATED");

  // Cause the thread to tear down.
  EndEventLoop();
}

TEST_F(INotifyTests, test_inotify_optimization) {
  // Assume event type is registered.
  StartEventLoop();

  boost::filesystem::create_directory(kRealTestDir);

  // Adding a descriptor to a directory will monitor files within.
  SubscriptionAction(kRealTestDir);
  EXPECT_TRUE(event_pub_->isPathMonitored(kRealTestDirPath));

  // Adding a subscription to a file within a monitored directory is fine
  // but this will NOT cause an additional INotify watch.
  SubscriptionAction(kRealTestDirPath);
  EXPECT_EQ(event_pub_->numDescriptors(), 1);

  // Cause the thread to tear down.
  EndEventLoop();
}

TEST_F(INotifyTests, test_inotify_recursion) {
  StartEventLoop();

  auto sub = std::make_shared<TestINotifyEventSubscriber>();
  sub->init();

  boost::filesystem::create_directory(kRealTestDir);
  boost::filesystem::create_directory(kRealTestSubDir);

  // Subscribe to the directory inode
  auto mc = sub->createSubscriptionContext();
  mc->path = kRealTestDir;
  mc->recursive = true;
  sub->subscribe(&TestINotifyEventSubscriber::Callback, mc);

  // Trigger on a subdirectory's file.
  TriggerEvent(kRealTestSubDirPath);

  sub->WaitForEvents(kMaxEventLatency, 1);
  EXPECT_TRUE(sub->count() > 0);

  EndEventLoop();
}
}

int main(int argc, char* argv[]) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}

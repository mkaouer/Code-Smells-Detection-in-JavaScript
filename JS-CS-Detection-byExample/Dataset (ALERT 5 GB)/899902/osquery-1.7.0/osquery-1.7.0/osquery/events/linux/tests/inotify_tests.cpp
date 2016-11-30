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
#include "osquery/core/test_util.h"

namespace fs = boost::filesystem;

namespace osquery {

const int kMaxEventLatency = 3000;

class INotifyTests : public testing::Test {
 protected:
  void SetUp() override {
    real_test_path = kTestWorkingDirectory + "inotify-trigger";
    real_test_dir = kTestWorkingDirectory + "inotify-triggers";
    real_test_dir_path = real_test_dir + "/1";
    real_test_sub_dir = real_test_dir + "/2";
    real_test_sub_dir_path = real_test_sub_dir + "/1";
  }

  void TearDown() override {
    // End the event loops, and join on the threads.
    fs::remove_all(real_test_path);
    fs::remove_all(real_test_dir);
  }

  void StartEventLoop() {
    event_pub_ = std::make_shared<INotifyEventPublisher>();
    auto status = EventFactory::registerEventPublisher(event_pub_);
    FILE* fd = fopen(real_test_path.c_str(), "w");
    fclose(fd);
    temp_thread_ = boost::thread(EventFactory::run, "inotify");
  }

  void StopEventLoop() {
    while (!event_pub_->hasStarted()) {
      ::usleep(20);
    }

    EventFactory::end(true);
    temp_thread_.join();
  }

  void SubscriptionAction(const std::string& path,
                          uint32_t mask = IN_ALL_EVENTS,
                          EventCallback ec = nullptr) {
    auto sc = std::make_shared<INotifySubscriptionContext>();
    sc->path = path;
    sc->mask = mask;

    EventFactory::addSubscription("inotify", "TestSubscriber", sc, ec);
    event_pub_->configure();
  }

  bool WaitForEvents(size_t max, size_t num_events = 0) {
    size_t delay = 0;
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

  void RemoveAll(std::shared_ptr<INotifyEventPublisher>& pub) {
    pub->subscriptions_.clear();
    // Reset monitors.
    std::vector<std::string> monitors;
    for (const auto& path : pub->path_descriptors_) {
      monitors.push_back(path.first);
    }
    for (const auto& path : monitors) {
      pub->removeMonitor(path, true);
    }
  }

 protected:
  // Internal state managers.
  std::shared_ptr<INotifyEventPublisher> event_pub_{nullptr};
  boost::thread temp_thread_;

  // Transient paths.
  std::string real_test_path;
  std::string real_test_dir;
  std::string real_test_dir_path;
  std::string real_test_sub_dir;
  std::string real_test_sub_dir_path;
};

TEST_F(INotifyTests, test_register_event_pub) {
  auto pub = std::make_shared<INotifyEventPublisher>();
  auto status = EventFactory::registerEventPublisher(pub);
  EXPECT_TRUE(status.ok());

  // Make sure only one event type exists
  EXPECT_EQ(EventFactory::numEventPublishers(), 1U);
  // And deregister
  status = EventFactory::deregisterEventPublisher("inotify");
  EXPECT_TRUE(status.ok());
}

TEST_F(INotifyTests, test_inotify_init) {
  // Handle should not be initialized during ctor.
  auto event_pub = std::make_shared<INotifyEventPublisher>();
  EXPECT_FALSE(event_pub->isHandleOpen());

  // Registering the event type initializes inotify.
  auto status = EventFactory::registerEventPublisher(event_pub);
  EXPECT_TRUE(status.ok());
  EXPECT_TRUE(event_pub->isHandleOpen());

  // Similarly deregistering closes the handle.
  EventFactory::deregisterEventPublisher("inotify");
  EXPECT_FALSE(event_pub->isHandleOpen());
}

TEST_F(INotifyTests, test_inotify_add_subscription_missing_path) {
  auto pub = std::make_shared<INotifyEventPublisher>();
  EventFactory::registerEventPublisher(pub);

  // This subscription path is fake, and will succeed.
  auto mc = std::make_shared<INotifySubscriptionContext>();
  mc->path = "/this/path/is/fake";

  auto subscription = Subscription::create("TestSubscriber", mc);
  auto status = EventFactory::addSubscription("inotify", subscription);
  EXPECT_TRUE(status.ok());
  EventFactory::deregisterEventPublisher("inotify");
}

TEST_F(INotifyTests, test_inotify_add_subscription_success) {
  auto pub = std::make_shared<INotifyEventPublisher>();
  EventFactory::registerEventPublisher(pub);

  // This subscription path *should* be real.
  auto mc = std::make_shared<INotifySubscriptionContext>();
  mc->path = "/";
  mc->mask = IN_ALL_EVENTS;

  auto subscription = Subscription::create("TestSubscriber", mc);
  auto status = EventFactory::addSubscription("inotify", subscription);
  EXPECT_TRUE(status.ok());
  EventFactory::deregisterEventPublisher("inotify");
}

TEST_F(INotifyTests, test_inotify_match_subscription) {
  auto pub = std::make_shared<INotifyEventPublisher>();
  pub->addMonitor("/etc", IN_ALL_EVENTS, false, false);
  EXPECT_EQ(pub->path_descriptors_.count("/etc"), 1U);
  // This will fail because there is no trailing "/" at the end.
  // The configure component should take care of these paths.
  EXPECT_FALSE(pub->isPathMonitored("/etc/passwd"));
  pub->path_descriptors_.clear();

  // Calling addMonitor the correct way.
  pub->addMonitor("/etc/", IN_ALL_EVENTS, false, false);
  EXPECT_TRUE(pub->isPathMonitored("/etc/passwd"));
  pub->path_descriptors_.clear();

  // Test the matching capability.
  {
    auto sc = pub->createSubscriptionContext();
    sc->path = "/etc";
    pub->monitorSubscription(sc, false);
    EXPECT_EQ(sc->path, "/etc/");
    EXPECT_TRUE(pub->isPathMonitored("/etc/"));
    EXPECT_TRUE(pub->isPathMonitored("/etc/passwd"));
  }

  std::vector<std::string> valid_dirs = {"/etc", "/etc/", "/etc/*"};
  for (const auto& dir : valid_dirs) {
    pub->path_descriptors_.clear();
    auto sc = pub->createSubscriptionContext();
    sc->path = dir;
    pub->monitorSubscription(sc, false);
    auto ec = pub->createEventContext();
    ec->path = "/etc/";
    EXPECT_TRUE(pub->shouldFire(sc, ec));
    ec->path = "/etc/passwd";
    EXPECT_TRUE(pub->shouldFire(sc, ec));
  }
}

class TestINotifyEventSubscriber
    : public EventSubscriber<INotifyEventPublisher> {
 public:
  TestINotifyEventSubscriber() { setName("TestINotifyEventSubscriber"); }

  Status init() override {
    callback_count_ = 0;
    return Status(0, "OK");
  }

  Status SimpleCallback(const ECRef& ec, const SCRef& sc) {
    callback_count_ += 1;
    return Status(0, "OK");
  }

  Status Callback(const ECRef& ec, const SCRef& sc) {
    // The following comments are an example Callback routine.
    // Row r;
    // r["action"] = ec->action;
    // r["path"] = ec->path;

    // Normally would call Add here.
    actions_.push_back(ec->action);
    callback_count_ += 1;
    return Status(0, "OK");
  }

  SCRef GetSubscription(const std::string& path,
                        uint32_t mask = IN_ALL_EVENTS) {
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
  int callback_count_{0};
  std::vector<std::string> actions_;

 private:
  FRIEND_TEST(INotifyTests, test_inotify_fire_event);
  FRIEND_TEST(INotifyTests, test_inotify_event_action);
  FRIEND_TEST(INotifyTests, test_inotify_optimization);
  FRIEND_TEST(INotifyTests, test_inotify_directory_watch);
  FRIEND_TEST(INotifyTests, test_inotify_recursion);
};

TEST_F(INotifyTests, test_inotify_run) {
  // Assume event type is registered.
  event_pub_ = std::make_shared<INotifyEventPublisher>();
  auto status = EventFactory::registerEventPublisher(event_pub_);
  EXPECT_TRUE(status.ok());

  // Create a temporary file to watch, open writeable
  FILE* fd = fopen(real_test_path.c_str(), "w");

  // Create a subscriber.
  auto sub = std::make_shared<TestINotifyEventSubscriber>();
  EventFactory::registerEventSubscriber(sub);

  // Create a subscriptioning context
  auto mc = std::make_shared<INotifySubscriptionContext>();
  mc->path = real_test_path;
  mc->mask = IN_ALL_EVENTS;
  status = EventFactory::addSubscription(
      "inotify", Subscription::create("TestINotifyEventSubscriber", mc));
  EXPECT_TRUE(status.ok());
  event_pub_->configure();

  // Create an event loop thread (similar to main)
  boost::thread temp_thread(EventFactory::run, "inotify");
  EXPECT_TRUE(event_pub_->numEvents() == 0);

  // Cause an inotify event by writing to the watched path.
  fputs("inotify", fd);
  fclose(fd);

  // Wait for the thread's run loop to select.
  WaitForEvents(kMaxEventLatency);
  EXPECT_TRUE(event_pub_->numEvents() > 0);
  EventFactory::end();
  temp_thread.join();
}

TEST_F(INotifyTests, test_inotify_fire_event) {
  // Assume event type is registered.
  StartEventLoop();
  auto sub = std::make_shared<TestINotifyEventSubscriber>();
  EventFactory::registerEventSubscriber(sub);

  // Create a subscriptioning context, note the added Event to the symbol
  auto sc = sub->GetSubscription(real_test_path, 0);
  sub->subscribe(&TestINotifyEventSubscriber::SimpleCallback, sc);
  event_pub_->configure();

  TriggerEvent(real_test_path);
  sub->WaitForEvents(kMaxEventLatency);

  // Make sure our expected event fired (aka subscription callback was called).
  EXPECT_TRUE(sub->count() > 0);
  StopEventLoop();
}

TEST_F(INotifyTests, test_inotify_event_action) {
  // Assume event type is registered.
  StartEventLoop();
  auto sub = std::make_shared<TestINotifyEventSubscriber>();
  EventFactory::registerEventSubscriber(sub);

  auto sc = sub->GetSubscription(real_test_path, IN_ALL_EVENTS);
  sub->subscribe(&TestINotifyEventSubscriber::Callback, sc);
  event_pub_->configure();

  TriggerEvent(real_test_path);
  sub->WaitForEvents(kMaxEventLatency, 2);

  // Make sure the inotify action was expected.
  EXPECT_GT(sub->actions().size(), 0U);
  if (sub->actions().size() >= 2) {
    EXPECT_EQ(sub->actions()[0], "UPDATED");
  }

  StopEventLoop();
}

TEST_F(INotifyTests, test_inotify_optimization) {
  // Assume event type is registered.
  StartEventLoop();
  fs::create_directory(real_test_dir);

  // Adding a descriptor to a directory will monitor files within.
  SubscriptionAction(real_test_dir);
  EXPECT_TRUE(event_pub_->isPathMonitored(real_test_dir_path));

  // Adding a subscription to a file within a monitored directory is fine
  // but this will NOT cause an additional INotify watch.
  SubscriptionAction(real_test_dir_path);
  EXPECT_EQ(event_pub_->numDescriptors(), 1U);
  StopEventLoop();
}

TEST_F(INotifyTests, test_inotify_directory_watch) {
  StartEventLoop();

  auto sub = std::make_shared<TestINotifyEventSubscriber>();
  EventFactory::registerEventSubscriber(sub);

  fs::create_directory(real_test_dir);
  fs::create_directory(real_test_sub_dir);

  // Subscribe to the directory inode
  auto mc = sub->createSubscriptionContext();
  mc->path = real_test_dir;
  mc->recursive = true;
  sub->subscribe(&TestINotifyEventSubscriber::Callback, mc);
  event_pub_->configure();

  // Trigger on a subdirectory's file.
  TriggerEvent(real_test_sub_dir_path);

  sub->WaitForEvents(kMaxEventLatency, 1);
  EXPECT_TRUE(sub->count() > 0);
  StopEventLoop();
}

TEST_F(INotifyTests, test_inotify_recursion) {
  // Create a non-registered publisher and subscriber.
  auto pub = std::make_shared<INotifyEventPublisher>();
  EventFactory::registerEventPublisher(pub);
  auto sub = std::make_shared<TestINotifyEventSubscriber>();

  // Create a mock directory structure.
  createMockFileStructure();

  // Create and test several subscriptions.
  auto sc = sub->createSubscriptionContext();

  sc->path = kFakeDirectory + "/*";
  sub->subscribe(&TestINotifyEventSubscriber::Callback, sc);
  // Trigger a configure step manually.
  pub->configure();

  // Expect a single monitor on the root of the fake tree.
  EXPECT_EQ(pub->path_descriptors_.size(), 1U);
  EXPECT_EQ(pub->path_descriptors_.count(kFakeDirectory + "/"), 1U);
  RemoveAll(pub);

  // Make sure monitors are empty.
  EXPECT_EQ(pub->numDescriptors(), 0U);

  auto sc2 = sub->createSubscriptionContext();
  sc2->path = kFakeDirectory + "/**";
  sub->subscribe(&TestINotifyEventSubscriber::Callback, sc2);
  pub->configure();

  // Expect only the directories to be monitored.
  EXPECT_EQ(pub->path_descriptors_.size(), 6U);
  RemoveAll(pub);

  // Use a directory structure that includes a loop.
  boost::system::error_code ec;
  fs::create_symlink(kFakeDirectory, kFakeDirectory + "/link", ec);

  auto sc3 = sub->createSubscriptionContext();
  sc3->path = kFakeDirectory + "/**";
  sub->subscribe(&TestINotifyEventSubscriber::Callback, sc3);
  pub->configure();

  // Also expect canonicalized resolution (to prevent loops).
  EXPECT_EQ(pub->path_descriptors_.size(), 6U);
  RemoveAll(pub);

  // Remove mock directory structure.
  tearDownMockFileStructure();
  EventFactory::deregisterEventPublisher("inotify");
}
}

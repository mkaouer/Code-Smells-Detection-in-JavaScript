/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.internal.operators;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import rx.*;
import rx.Observable.OnSubscribe;
import rx.functions.Action0;
import rx.internal.util.RxThreadFactory;
import rx.observers.*;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class OperatorUnsubscribeOnTest {

    @Test(timeout = 1000)
    public void testUnsubscribeWhenSubscribeOnAndUnsubscribeOnAreOnSameThread() throws InterruptedException {
        UIEventLoopScheduler UI_EVENT_LOOP = new UIEventLoopScheduler();
        try {
            final ThreadSubscription subscription = new ThreadSubscription();
            final AtomicReference<Thread> subscribeThread = new AtomicReference<Thread>();
            Observable<Integer> w = Observable.create(new OnSubscribe<Integer>() {

                @Override
                public void call(Subscriber<? super Integer> t1) {
                    subscribeThread.set(Thread.currentThread());
                    t1.add(subscription);
                    t1.onNext(1);
                    t1.onNext(2);
                    t1.onCompleted();
                }
            });

            TestSubscriber<Integer> observer = new TestSubscriber<Integer>();
            w
            .subscribeOn(UI_EVENT_LOOP)
            .observeOn(Schedulers.computation())
            .unsubscribeOn(UI_EVENT_LOOP)
            .subscribe(observer);

            Thread unsubscribeThread = subscription.getThread();

            assertNotNull(unsubscribeThread);
            assertNotSame(Thread.currentThread(), unsubscribeThread);

            assertNotNull(subscribeThread.get());
            assertNotSame(Thread.currentThread(), subscribeThread.get());
            // True for Schedulers.newThread()

            System.out.println("unsubscribeThread: " + unsubscribeThread);
            System.out.println("subscribeThread.get(): " + subscribeThread.get());
            assertTrue(unsubscribeThread == UI_EVENT_LOOP.getThread());

            observer.assertReceivedOnNext(Arrays.asList(1, 2));
            observer.assertTerminalEvent();
        } finally {
            UI_EVENT_LOOP.shutdown();
        }
    }

    @Test(timeout = 1000)
    public void testUnsubscribeWhenSubscribeOnAndUnsubscribeOnAreOnDifferentThreads() throws InterruptedException {
        UIEventLoopScheduler UI_EVENT_LOOP = new UIEventLoopScheduler();
        try {
            final ThreadSubscription subscription = new ThreadSubscription();
            final AtomicReference<Thread> subscribeThread = new AtomicReference<Thread>();
            Observable<Integer> w = Observable.create(new OnSubscribe<Integer>() {

                @Override
                public void call(Subscriber<? super Integer> t1) {
                    subscribeThread.set(Thread.currentThread());
                    t1.add(subscription);
                    t1.onNext(1);
                    t1.onNext(2);
                    t1.onCompleted();
                }
            });

            TestSubscriber<Integer> observer = new TestSubscriber<Integer>();
            w
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.computation())
            .unsubscribeOn(UI_EVENT_LOOP)
            .subscribe(observer);

            Thread unsubscribeThread = subscription.getThread();

            assertNotNull(unsubscribeThread);
            assertNotSame(Thread.currentThread(), unsubscribeThread);

            assertNotNull(subscribeThread.get());
            assertNotSame(Thread.currentThread(), subscribeThread.get());
            // True for Schedulers.newThread()

            System.out.println("unsubscribeThread: " + unsubscribeThread);
            System.out.println("subscribeThread.get(): " + subscribeThread.get());
            Thread uiThread = UI_EVENT_LOOP.getThread();
            System.out.println("UI_EVENT_LOOP: " + uiThread);

            assertTrue(unsubscribeThread == uiThread);

            observer.assertReceivedOnNext(Arrays.asList(1, 2));
            observer.assertTerminalEvent();
        } finally {
            UI_EVENT_LOOP.shutdown();
        }
    }

    private static class ThreadSubscription implements Subscription {
        private volatile Thread thread;

        private final CountDownLatch latch = new CountDownLatch(1);

        private final Subscription s = Subscriptions.create(new Action0() {

            @Override
            public void call() {
                System.out.println("unsubscribe invoked: " + Thread.currentThread());
                thread = Thread.currentThread();
                latch.countDown();
            }

        });

        @Override
        public void unsubscribe() {
            s.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return s.isUnsubscribed();
        }

        public Thread getThread() throws InterruptedException {
            latch.await();
            return thread;
        }
    }

    public static class UIEventLoopScheduler extends Scheduler {

        private final ExecutorService eventLoop;
        final Scheduler single;
        private volatile Thread t;

        public UIEventLoopScheduler() {

            eventLoop = Executors.newSingleThreadExecutor(new RxThreadFactory("Test-EventLoop"));

            single = Schedulers.from(eventLoop);

            /*
             * DON'T DO THIS IN PRODUCTION CODE
             */
            final CountDownLatch latch = new CountDownLatch(1);
            eventLoop.submit(new Runnable() {

                @Override
                public void run() {
                    t = Thread.currentThread();
                    latch.countDown();
                }

            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("failed to initialize and get inner thread");
            }
        }

        @Override
        public Worker createWorker() {
            return single.createWorker();
        }

        public void shutdown() {
            eventLoop.shutdownNow();
        }

        public Thread getThread() {
            return t;
        }

    }
}
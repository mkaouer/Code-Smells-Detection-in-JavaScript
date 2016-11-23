/**
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.operators;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class SynchronizedObserverTest {

    @Mock
    Observer<String> aObserver;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSingleThreadedBasic() {
        Subscription s = mock(Subscription.class);
        TestSingleThreadedObservable onSubscribe = new TestSingleThreadedObservable(s, "one", "two", "three");
        Observable<String> w = Observable.create(onSubscribe);

        SafeObservableSubscription as = new SafeObservableSubscription(s);
        SynchronizedObserver<String> aw = new SynchronizedObserver<String>(aObserver, as);

        w.subscribe(aw);
        onSubscribe.waitToFinish();

        verify(aObserver, times(1)).onNext("one");
        verify(aObserver, times(1)).onNext("two");
        verify(aObserver, times(1)).onNext("three");
        verify(aObserver, never()).onError(any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        //            verify(s, times(1)).unsubscribe();
    }

    @Test
    public void testMultiThreadedBasic() {
        Subscription s = mock(Subscription.class);
        TestMultiThreadedObservable onSubscribe = new TestMultiThreadedObservable(s, "one", "two", "three");
        Observable<String> w = Observable.create(onSubscribe);

        SafeObservableSubscription as = new SafeObservableSubscription(s);
        BusyObserver busyObserver = new BusyObserver();
        SynchronizedObserver<String> aw = new SynchronizedObserver<String>(busyObserver, as);

        w.subscribe(aw);
        onSubscribe.waitToFinish();

        assertEquals(3, busyObserver.onNextCount.get());
        assertFalse(busyObserver.onError);
        assertTrue(busyObserver.onCompleted);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        //            verify(s, times(1)).unsubscribe();

        // we can have concurrency ...
        assertTrue(onSubscribe.maxConcurrentThreads.get() > 1);
        // ... but the onNext execution should be single threaded
        assertEquals(1, busyObserver.maxConcurrentThreads.get());
    }

    @Test
    public void testMultiThreadedBasicWithLock() {
        Subscription s = mock(Subscription.class);
        TestMultiThreadedObservable onSubscribe = new TestMultiThreadedObservable(s, "one", "two", "three");
        Observable<String> w = Observable.create(onSubscribe);

        SafeObservableSubscription as = new SafeObservableSubscription(s);
        BusyObserver busyObserver = new BusyObserver();

        Object lock = new Object();
        ExternalBusyThread externalBusyThread = new ExternalBusyThread(busyObserver, lock, 10, 100);

        SynchronizedObserver<String> aw = new SynchronizedObserver<String>(busyObserver, as, lock);

        externalBusyThread.start();

        w.subscribe(aw);
        onSubscribe.waitToFinish();

        try {
            externalBusyThread.join(10000);
            assertFalse(externalBusyThread.isAlive());
            assertFalse(externalBusyThread.fail);
        } catch (InterruptedException e) {
            // ignore
        }

        assertEquals(3, busyObserver.onNextCount.get());
        assertFalse(busyObserver.onError);
        assertTrue(busyObserver.onCompleted);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        //            verify(s, times(1)).unsubscribe();

        // we can have concurrency ...
        assertTrue(onSubscribe.maxConcurrentThreads.get() > 1);
        // ... but the onNext execution should be single threaded
        assertEquals(1, busyObserver.maxConcurrentThreads.get());
    }

    @Test
    public void testMultiThreadedWithNPE() {
        Subscription s = mock(Subscription.class);
        TestMultiThreadedObservable onSubscribe = new TestMultiThreadedObservable(s, "one", "two", "three", null);
        Observable<String> w = Observable.create(onSubscribe);

        SafeObservableSubscription as = new SafeObservableSubscription(s);
        BusyObserver busyObserver = new BusyObserver();
        SynchronizedObserver<String> aw = new SynchronizedObserver<String>(busyObserver, as);

        w.subscribe(aw);
        onSubscribe.waitToFinish();

        System.out.println("maxConcurrentThreads: " + onSubscribe.maxConcurrentThreads.get());

        // we can't know how many onNext calls will occur since they each run on a separate thread
        // that depends on thread scheduling so 0, 1, 2 and 3 are all valid options
        // assertEquals(3, busyObserver.onNextCount.get());
        assertTrue(busyObserver.onNextCount.get() < 4);
        assertTrue(busyObserver.onError);
        // no onCompleted because onError was invoked
        assertFalse(busyObserver.onCompleted);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        //verify(s, times(1)).unsubscribe();

        // we can have concurrency ...
        assertTrue(onSubscribe.maxConcurrentThreads.get() > 1);
        // ... but the onNext execution should be single threaded
        assertEquals(1, busyObserver.maxConcurrentThreads.get());
    }

    @Test
    public void testMultiThreadedWithNPEAndLock() {
        Subscription s = mock(Subscription.class);
        TestMultiThreadedObservable onSubscribe = new TestMultiThreadedObservable(s, "one", "two", "three", null);
        Observable<String> w = Observable.create(onSubscribe);

        SafeObservableSubscription as = new SafeObservableSubscription(s);
        BusyObserver busyObserver = new BusyObserver();

        Object lock = new Object();
        ExternalBusyThread externalBusyThread = new ExternalBusyThread(busyObserver, lock, 10, 100);

        SynchronizedObserver<String> aw = new SynchronizedObserver<String>(busyObserver, as, lock);

        externalBusyThread.start();

        w.subscribe(aw);
        onSubscribe.waitToFinish();

        try {
            externalBusyThread.join(10000);
            assertFalse(externalBusyThread.isAlive());
            assertFalse(externalBusyThread.fail);
        } catch (InterruptedException e) {
            // ignore
        }

        System.out.println("maxConcurrentThreads: " + onSubscribe.maxConcurrentThreads.get());

        // we can't know how many onNext calls will occur since they each run on a separate thread
        // that depends on thread scheduling so 0, 1, 2 and 3 are all valid options
        // assertEquals(3, busyObserver.onNextCount.get());
        assertTrue(busyObserver.onNextCount.get() < 4);
        assertTrue(busyObserver.onError);
        // no onCompleted because onError was invoked
        assertFalse(busyObserver.onCompleted);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        //verify(s, times(1)).unsubscribe();

        // we can have concurrency ...
        assertTrue(onSubscribe.maxConcurrentThreads.get() > 1);
        // ... but the onNext execution should be single threaded
        assertEquals(1, busyObserver.maxConcurrentThreads.get());
    }

    @Test
    public void testMultiThreadedWithNPEinMiddle() {
        Subscription s = mock(Subscription.class);
        TestMultiThreadedObservable onSubscribe = new TestMultiThreadedObservable(s, "one", "two", "three", null, "four", "five", "six", "seven", "eight", "nine");
        Observable<String> w = Observable.create(onSubscribe);

        SafeObservableSubscription as = new SafeObservableSubscription(s);
        BusyObserver busyObserver = new BusyObserver();
        SynchronizedObserver<String> aw = new SynchronizedObserver<String>(busyObserver, as);

        w.subscribe(aw);
        onSubscribe.waitToFinish();

        System.out.println("maxConcurrentThreads: " + onSubscribe.maxConcurrentThreads.get());
        // this should not be the full number of items since the error should stop it before it completes all 9
        System.out.println("onNext count: " + busyObserver.onNextCount.get());
        assertTrue(busyObserver.onNextCount.get() < 9);
        assertTrue(busyObserver.onError);
        // no onCompleted because onError was invoked
        assertFalse(busyObserver.onCompleted);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        // verify(s, times(1)).unsubscribe();

        // we can have concurrency ...
        assertTrue(onSubscribe.maxConcurrentThreads.get() > 1);
        // ... but the onNext execution should be single threaded
        assertEquals(1, busyObserver.maxConcurrentThreads.get());
    }

    @Test
    public void testMultiThreadedWithNPEinMiddleAndLock() {
        Subscription s = mock(Subscription.class);
        TestMultiThreadedObservable onSubscribe = new TestMultiThreadedObservable(s, "one", "two", "three", null, "four", "five", "six", "seven", "eight", "nine");
        Observable<String> w = Observable.create(onSubscribe);

        SafeObservableSubscription as = new SafeObservableSubscription(s);
        BusyObserver busyObserver = new BusyObserver();

        Object lock = new Object();
        ExternalBusyThread externalBusyThread = new ExternalBusyThread(busyObserver, lock, 10, 100);

        SynchronizedObserver<String> aw = new SynchronizedObserver<String>(busyObserver, as, lock);

        externalBusyThread.start();

        w.subscribe(aw);
        onSubscribe.waitToFinish();

        try {
            externalBusyThread.join(10000);
            assertFalse(externalBusyThread.isAlive());
            assertFalse(externalBusyThread.fail);
        } catch (InterruptedException e) {
            // ignore
        }

        System.out.println("maxConcurrentThreads: " + onSubscribe.maxConcurrentThreads.get());
        // this should not be the full number of items since the error should stop it before it completes all 9
        System.out.println("onNext count: " + busyObserver.onNextCount.get());
        assertTrue(busyObserver.onNextCount.get() < 9);
        assertTrue(busyObserver.onError);
        // no onCompleted because onError was invoked
        assertFalse(busyObserver.onCompleted);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        // verify(s, times(1)).unsubscribe();

        // we can have concurrency ...
        assertTrue(onSubscribe.maxConcurrentThreads.get() > 1);
        // ... but the onNext execution should be single threaded
        assertEquals(1, busyObserver.maxConcurrentThreads.get());
    }

    /**
     * A non-realistic use case that tries to expose thread-safety issues by throwing lots of out-of-order
     * events on many threads.
     * 
     * @param w
     * @param tw
     */
    @Test
    public void runConcurrencyTest() {
        ExecutorService tp = Executors.newFixedThreadPool(20);
        try {
            TestConcurrencyObserver tw = new TestConcurrencyObserver();
            SafeObservableSubscription s = new SafeObservableSubscription();
            SynchronizedObserver<String> w = new SynchronizedObserver<String>(tw, s);

            Future<?> f1 = tp.submit(new OnNextThread(w, 12000));
            Future<?> f2 = tp.submit(new OnNextThread(w, 5000));
            Future<?> f3 = tp.submit(new OnNextThread(w, 75000));
            Future<?> f4 = tp.submit(new OnNextThread(w, 13500));
            Future<?> f5 = tp.submit(new OnNextThread(w, 22000));
            Future<?> f6 = tp.submit(new OnNextThread(w, 15000));
            Future<?> f7 = tp.submit(new OnNextThread(w, 7500));
            Future<?> f8 = tp.submit(new OnNextThread(w, 23500));

            Future<?> f10 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onCompleted, f1, f2, f3, f4));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
            Future<?> f11 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onCompleted, f4, f6, f7));
            Future<?> f12 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onCompleted, f4, f6, f7));
            Future<?> f13 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onCompleted, f4, f6, f7));
            Future<?> f14 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onCompleted, f4, f6, f7));
            // // the next 4 onError events should wait on same as f10
            Future<?> f15 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onError, f1, f2, f3, f4));
            Future<?> f16 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onError, f1, f2, f3, f4));
            Future<?> f17 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onError, f1, f2, f3, f4));
            Future<?> f18 = tp.submit(new CompletionThread(w, TestConcurrencyObserverEvent.onError, f1, f2, f3, f4));

            waitOnThreads(f1, f2, f3, f4, f5, f6, f7, f8, f10, f11, f12, f13, f14, f15, f16, f17, f18);
            @SuppressWarnings("unused")
            int numNextEvents = tw.assertEvents(null); // no check of type since we don't want to test barging results here, just interleaving behavior
            // System.out.println("Number of events executed: " + numNextEvents);
        } catch (Throwable e) {
            fail("Concurrency test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            tp.shutdown();
            try {
                tp.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void waitOnThreads(Future<?>... futures) {
        for (Future<?> f : futures) {
            try {
                f.get(10, TimeUnit.SECONDS);
            } catch (Throwable e) {
                System.err.println("Failed while waiting on future.");
                e.printStackTrace();
            }
        }
    }

    /**
     * A thread that will pass data to onNext
     */
    public static class OnNextThread implements Runnable {

        private final Observer<String> Observer;
        private final int numStringsToSend;

        OnNextThread(Observer<String> Observer, int numStringsToSend) {
            this.Observer = Observer;
            this.numStringsToSend = numStringsToSend;
        }

        @Override
        public void run() {
            for (int i = 0; i < numStringsToSend; i++) {
                Observer.onNext("aString");
            }
        }
    }

    /**
     * A thread that will call onError or onNext
     */
    public static class CompletionThread implements Runnable {

        private final Observer<String> Observer;
        private final TestConcurrencyObserverEvent event;
        private final Future<?>[] waitOnThese;

        CompletionThread(Observer<String> Observer, TestConcurrencyObserverEvent event, Future<?>... waitOnThese) {
            this.Observer = Observer;
            this.event = event;
            this.waitOnThese = waitOnThese;
        }

        @Override
        public void run() {
            /* if we have 'waitOnThese' futures, we'll wait on them before proceeding */
            if (waitOnThese != null) {
                for (Future<?> f : waitOnThese) {
                    try {
                        f.get();
                    } catch (Throwable e) {
                        System.err.println("Error while waiting on future in CompletionThread");
                    }
                }
            }

            /* send the event */
            if (event == TestConcurrencyObserverEvent.onError) {
                Observer.onError(new RuntimeException("mocked exception"));
            } else if (event == TestConcurrencyObserverEvent.onCompleted) {
                Observer.onCompleted();

            } else {
                throw new IllegalArgumentException("Expecting either onError or onCompleted");
            }
        }
    }

    private static enum TestConcurrencyObserverEvent {
        onCompleted, onError, onNext
    }

    private static class TestConcurrencyObserver implements Observer<String> {

        /**
         * used to store the order and number of events received
         */
        private final LinkedBlockingQueue<TestConcurrencyObserverEvent> events = new LinkedBlockingQueue<TestConcurrencyObserverEvent>();
        private final int waitTime;

        @SuppressWarnings("unused")
        public TestConcurrencyObserver(int waitTimeInNext) {
            this.waitTime = waitTimeInNext;
        }

        public TestConcurrencyObserver() {
            this.waitTime = 0;
        }

        @Override
        public void onCompleted() {
            events.add(TestConcurrencyObserverEvent.onCompleted);
        }

        @Override
        public void onError(Throwable e) {
            events.add(TestConcurrencyObserverEvent.onError);
        }

        @Override
        public void onNext(String args) {
            events.add(TestConcurrencyObserverEvent.onNext);
            // do some artificial work to make the thread scheduling/timing vary
            int s = 0;
            for (int i = 0; i < 20; i++) {
                s += s * i;
            }

            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }

        /**
         * Assert the order of events is correct and return the number of onNext executions.
         * 
         * @param expectedEndingEvent
         * @return int count of onNext calls
         * @throws IllegalStateException
         *             If order of events was invalid.
         */
        public int assertEvents(TestConcurrencyObserverEvent expectedEndingEvent) throws IllegalStateException {
            int nextCount = 0;
            boolean finished = false;
            for (TestConcurrencyObserverEvent e : events) {
                if (e == TestConcurrencyObserverEvent.onNext) {
                    if (finished) {
                        // already finished, we shouldn't get this again
                        throw new IllegalStateException("Received onNext but we're already finished.");
                    }
                    nextCount++;
                } else if (e == TestConcurrencyObserverEvent.onError) {
                    if (finished) {
                        // already finished, we shouldn't get this again
                        throw new IllegalStateException("Received onError but we're already finished.");
                    }
                    if (expectedEndingEvent != null && TestConcurrencyObserverEvent.onError != expectedEndingEvent) {
                        throw new IllegalStateException("Received onError ending event but expected " + expectedEndingEvent);
                    }
                    finished = true;
                } else if (e == TestConcurrencyObserverEvent.onCompleted) {
                    if (finished) {
                        // already finished, we shouldn't get this again
                        throw new IllegalStateException("Received onCompleted but we're already finished.");
                    }
                    if (expectedEndingEvent != null && TestConcurrencyObserverEvent.onCompleted != expectedEndingEvent) {
                        throw new IllegalStateException("Received onCompleted ending event but expected " + expectedEndingEvent);
                    }
                    finished = true;
                }
            }

            return nextCount;
        }

    }

    /**
     * This spawns a single thread for the subscribe execution
     */
    private static class TestSingleThreadedObservable implements Observable.OnSubscribeFunc<String> {

        final Subscription s;
        final String[] values;
        private Thread t = null;

        public TestSingleThreadedObservable(final Subscription s, final String... values) {
            this.s = s;
            this.values = values;

        }

        public Subscription onSubscribe(final Observer<? super String> observer) {
            System.out.println("TestSingleThreadedObservable subscribed to ...");
            t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        System.out.println("running TestSingleThreadedObservable thread");
                        for (String s : values) {
                            System.out.println("TestSingleThreadedObservable onNext: " + s);
                            observer.onNext(s);
                        }
                        observer.onCompleted();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }

            });
            System.out.println("starting TestSingleThreadedObservable thread");
            t.start();
            System.out.println("done starting TestSingleThreadedObservable thread");
            return s;
        }

        public void waitToFinish() {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * This spawns a thread for the subscription, then a separate thread for each onNext call.
     */
    private static class TestMultiThreadedObservable implements Observable.OnSubscribeFunc<String> {

        final Subscription s;
        final String[] values;
        Thread t = null;
        AtomicInteger threadsRunning = new AtomicInteger();
        AtomicInteger maxConcurrentThreads = new AtomicInteger();
        ExecutorService threadPool;

        public TestMultiThreadedObservable(Subscription s, String... values) {
            this.s = s;
            this.values = values;
            this.threadPool = Executors.newCachedThreadPool();
        }

        @Override
        public Subscription onSubscribe(final Observer<? super String> observer) {
            System.out.println("TestMultiThreadedObservable subscribed to ...");
            t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        System.out.println("running TestMultiThreadedObservable thread");
                        for (final String s : values) {
                            threadPool.execute(new Runnable() {

                                @Override
                                public void run() {
                                    threadsRunning.incrementAndGet();
                                    try {
                                        // perform onNext call
                                        System.out.println("TestMultiThreadedObservable onNext: " + s);
                                        if (s == null) {
                                            // force an error
                                            throw new NullPointerException();
                                        }
                                        observer.onNext(s);
                                        // capture 'maxThreads'
                                        int concurrentThreads = threadsRunning.get();
                                        int maxThreads = maxConcurrentThreads.get();
                                        if (concurrentThreads > maxThreads) {
                                            maxConcurrentThreads.compareAndSet(maxThreads, concurrentThreads);
                                        }
                                    } catch (Throwable e) {
                                        observer.onError(e);
                                    } finally {
                                        threadsRunning.decrementAndGet();
                                    }
                                }
                            });
                        }
                        // we are done spawning threads
                        threadPool.shutdown();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }

                    // wait until all threads are done, then mark it as COMPLETED
                    try {
                        // wait for all the threads to finish
                        threadPool.awaitTermination(2, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    observer.onCompleted();
                }
            });
            System.out.println("starting TestMultiThreadedObservable thread");
            t.start();
            System.out.println("done starting TestMultiThreadedObservable thread");
            return s;
        }

        public void waitToFinish() {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class BusyObserver implements Observer<String> {
        volatile boolean onCompleted = false;
        volatile boolean onError = false;
        AtomicInteger onNextCount = new AtomicInteger();
        AtomicInteger threadsRunning = new AtomicInteger();
        AtomicInteger maxConcurrentThreads = new AtomicInteger();

        @Override
        public void onCompleted() {
            threadsRunning.incrementAndGet();

            System.out.println(">>> BusyObserver received onCompleted");
            onCompleted = true;

            int concurrentThreads = threadsRunning.get();
            int maxThreads = maxConcurrentThreads.get();
            if (concurrentThreads > maxThreads) {
                maxConcurrentThreads.compareAndSet(maxThreads, concurrentThreads);
            }
            threadsRunning.decrementAndGet();
        }

        @Override
        public void onError(Throwable e) {
            threadsRunning.incrementAndGet();

            System.out.println(">>> BusyObserver received onError: " + e.getMessage());
            onError = true;

            int concurrentThreads = threadsRunning.get();
            int maxThreads = maxConcurrentThreads.get();
            if (concurrentThreads > maxThreads) {
                maxConcurrentThreads.compareAndSet(maxThreads, concurrentThreads);
            }
            threadsRunning.decrementAndGet();
        }

        @Override
        public void onNext(String args) {
            threadsRunning.incrementAndGet();
            try {
                onNextCount.incrementAndGet();
                System.out.println(">>> BusyObserver received onNext: " + args);
                try {
                    // simulate doing something computational
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                // capture 'maxThreads'
                int concurrentThreads = threadsRunning.get();
                int maxThreads = maxConcurrentThreads.get();
                if (concurrentThreads > maxThreads) {
                    maxConcurrentThreads.compareAndSet(maxThreads, concurrentThreads);
                }
                threadsRunning.decrementAndGet();
            }
        }

    }

    private static class ExternalBusyThread extends Thread {

        private BusyObserver observer;
        private Object lock;
        private int lockTimes;
        private int waitTime;
        public volatile boolean fail;

        public ExternalBusyThread(BusyObserver observer, Object lock, int lockTimes, int waitTime) {
            this.observer = observer;
            this.lock = lock;
            this.lockTimes = lockTimes;
            this.waitTime = waitTime;
            this.fail = false;
        }

        @Override
        public void run() {
            Random r = new Random();
            for (int i = 0; i < lockTimes; i++) {
                synchronized (lock) {
                    int oldOnNextCount = observer.onNextCount.get();
                    boolean oldOnCompleted = observer.onCompleted;
                    boolean oldOnError = observer.onError;
                    try {
                        Thread.sleep(r.nextInt(waitTime));
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    // Since we own the lock, onNextCount, onCompleted and
                    // onError must not be changed.
                    int newOnNextCount = observer.onNextCount.get();
                    boolean newOnCompleted = observer.onCompleted;
                    boolean newOnError = observer.onError;
                    if (oldOnNextCount != newOnNextCount) {
                        System.out.println(">>> ExternalBusyThread received different onNextCount: "
                                + oldOnNextCount
                                + " -> "
                                + newOnNextCount);
                        fail = true;
                        break;
                    }
                    if (oldOnCompleted != newOnCompleted) {
                        System.out.println(">>> ExternalBusyThread received different onCompleted: "
                                + oldOnCompleted
                                + " -> "
                                + newOnCompleted);
                        fail = true;
                        break;
                    }
                    if (oldOnError != newOnError) {
                        System.out.println(">>> ExternalBusyThread received different onError: "
                                + oldOnError
                                + " -> "
                                + newOnError);
                        fail = true;
                        break;
                    }
                }
            }
        }

    }
}

/**
 * Copyright 2013 Netflix, Inc.
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
package rx;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable.OnSubscribeFunc;
import rx.observables.ConnectableObservable;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action0;
import rx.util.functions.Action1;
import rx.util.functions.Func1;
import rx.util.functions.Func2;

public class ObservableTests {

    @Mock
    Observer<Integer> w;

    private static final Func1<Integer, Boolean> IS_EVEN = new Func1<Integer, Boolean>() {
        @Override
        public Boolean call(Integer value) {
            return value % 2 == 0;
        }
    };

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fromArray() {
        String[] items = new String[] { "one", "two", "three" };
        assertEquals(new Integer(3), Observable.from(items).count().toBlockingObservable().single());
        assertEquals("two", Observable.from(items).skip(1).take(1).toBlockingObservable().single());
        assertEquals("three", Observable.from(items).takeLast(1).toBlockingObservable().single());
    }

    @Test
    public void fromIterable() {
        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");

        assertEquals(new Integer(3), Observable.from(items).count().toBlockingObservable().single());
        assertEquals("two", Observable.from(items).skip(1).take(1).toBlockingObservable().single());
        assertEquals("three", Observable.from(items).takeLast(1).toBlockingObservable().single());
    }

    @Test
    public void fromArityArgs3() {
        Observable<String> items = Observable.from("one", "two", "three");

        assertEquals(new Integer(3), items.count().toBlockingObservable().single());
        assertEquals("two", items.skip(1).take(1).toBlockingObservable().single());
        assertEquals("three", items.takeLast(1).toBlockingObservable().single());
    }

    @Test
    public void fromArityArgs1() {
        Observable<String> items = Observable.from("one");

        assertEquals(new Integer(1), items.count().toBlockingObservable().single());
        assertEquals("one", items.takeLast(1).toBlockingObservable().single());
    }

    @Test
    public void testCreate() {

        Observable<String> observable = Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(Observer<? super String> Observer) {
                Observer.onNext("one");
                Observer.onNext("two");
                Observer.onNext("three");
                Observer.onCompleted();
                return Subscriptions.empty();
            }

        });

        @SuppressWarnings("unchecked")
        Observer<String> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, times(1)).onNext("one");
        verify(aObserver, times(1)).onNext("two");
        verify(aObserver, times(1)).onNext("three");
        verify(aObserver, never()).onError(any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testCountAFewItems() {
        Observable<String> observable = Observable.from("a", "b", "c", "d");
        observable.count().subscribe(w);
        // we should be called only once
        verify(w, times(1)).onNext(anyInt());
        verify(w).onNext(4);
        verify(w, never()).onError(any(Throwable.class));
        verify(w, times(1)).onCompleted();
    }

    @Test
    public void testCountZeroItems() {
        Observable<String> observable = Observable.empty();
        observable.count().subscribe(w);
        // we should be called only once
        verify(w, times(1)).onNext(anyInt());
        verify(w).onNext(0);
        verify(w, never()).onError(any(Throwable.class));
        verify(w, times(1)).onCompleted();
    }

    @Test
    public void testCountError() {
        Observable<String> o = Observable.create(new OnSubscribeFunc<String>() {
            @Override
            public Subscription onSubscribe(Observer<? super String> obsv) {
                obsv.onError(new RuntimeException());
                return Subscriptions.empty();
            }
        });
        o.count().subscribe(w);
        verify(w, never()).onNext(anyInt());
        verify(w, never()).onCompleted();
        verify(w, times(1)).onError(any(RuntimeException.class));
    }

    public void testFirstWithPredicateOfSome() {
        Observable<Integer> observable = Observable.from(1, 3, 5, 4, 6, 3);
        observable.first(IS_EVEN).subscribe(w);
        verify(w, times(1)).onNext(anyInt());
        verify(w).onNext(4);
        verify(w, times(1)).onCompleted();
        verify(w, never()).onError(any(Throwable.class));
    }

    @Test
    public void testFirstWithPredicateOfNoneMatchingThePredicate() {
        Observable<Integer> observable = Observable.from(1, 3, 5, 7, 9, 7, 5, 3, 1);
        observable.first(IS_EVEN).subscribe(w);
        verify(w, never()).onNext(anyInt());
        verify(w, times(1)).onCompleted();
        verify(w, never()).onError(any(Throwable.class));
    }

    @Test
    public void testFirstOfSome() {
        Observable<Integer> observable = Observable.from(1, 2, 3);
        observable.first().subscribe(w);
        verify(w, times(1)).onNext(anyInt());
        verify(w).onNext(1);
        verify(w, times(1)).onCompleted();
        verify(w, never()).onError(any(Throwable.class));
    }

    @Test
    public void testFirstOfNone() {
        Observable<Integer> observable = Observable.empty();
        observable.first().subscribe(w);
        verify(w, never()).onNext(anyInt());
        verify(w, times(1)).onCompleted();
        verify(w, never()).onError(any(Throwable.class));
    }

    @Test
    public void testReduce() {
        Observable<Integer> observable = Observable.from(1, 2, 3, 4);
        observable.reduce(new Func2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer t1, Integer t2) {
                return t1 + t2;
            }

        }).subscribe(w);
        // we should be called only once
        verify(w, times(1)).onNext(anyInt());
        verify(w).onNext(10);
    }

    
    /**
     * A reduce should fail with an IllegalArgumentException if done on an empty Observable.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReduceWithEmptyObservable() {
        Observable<Integer> observable = Observable.range(1, 0);
        observable.reduce(new Func2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer t1, Integer t2) {
                return t1 + t2;
            }

        }).toBlockingObservable().forEach(new Action1<Integer>() {

            @Override
            public void call(Integer t1) {
                // do nothing ... we expect an exception instead
            }
        });

        fail("Expected an exception to be thrown");
    }
    
    /**
     * A reduce on an empty Observable and a seed should just pass the seed through.
     * 
     * This is confirmed at https://github.com/Netflix/RxJava/issues/423#issuecomment-27642456
     */
    @Test
    public void testReduceWithEmptyObservableAndSeed() {
        Observable<Integer> observable = Observable.range(1, 0);
        int value = observable.reduce(1, new Func2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer t1, Integer t2) {
                return t1 + t2;
            }

        }).toBlockingObservable().last();

        assertEquals(1, value);
    }
    
    @Test
    public void testReduceWithInitialValue() {
        Observable<Integer> observable = Observable.from(1, 2, 3, 4);
        observable.reduce(50, new Func2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer t1, Integer t2) {
                return t1 + t2;
            }

        }).subscribe(w);
        // we should be called only once
        verify(w, times(1)).onNext(anyInt());
        verify(w).onNext(60);
    }

    @Test
    public void testSequenceEqual() {
        Observable<Integer> first = Observable.from(1, 2, 3);
        Observable<Integer> second = Observable.from(1, 2, 4);
        @SuppressWarnings("unchecked")
        Observer<Boolean> result = mock(Observer.class);
        Observable.sequenceEqual(first, second).subscribe(result);
        verify(result, times(2)).onNext(true);
        verify(result, times(1)).onNext(false);
    }

    @Test
    public void testOnSubscribeFails() {
        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        final RuntimeException re = new RuntimeException("bad impl");
        Observable<String> o = Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(Observer<? super String> t1) {
                throw re;
            }

        });
        o.subscribe(observer);
        verify(observer, times(0)).onNext(anyString());
        verify(observer, times(0)).onCompleted();
        verify(observer, times(1)).onError(re);
    }

    @Test
    public void testMaterializeDematerializeChaining() {
        Observable<Integer> obs = Observable.just(1);
        Observable<Integer> chained = obs.materialize().dematerialize();

        @SuppressWarnings("unchecked")
        Observer<Integer> observer = mock(Observer.class);
        chained.subscribe(observer);

        verify(observer, times(1)).onNext(1);
        verify(observer, times(1)).onCompleted();
        verify(observer, times(0)).onError(any(Throwable.class));
    }

    /**
     * The error from the user provided Observer is not handled by the subscribe method try/catch.
     * 
     * It is handled by the AtomicObserver that wraps the provided Observer.
     * 
     * Result: Passes (if AtomicObserver functionality exists)
     */
    @Test
    public void testCustomObservableWithErrorInObserverAsynchronous() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(final Observer<? super String> observer) {
                final BooleanSubscription s = new BooleanSubscription();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (!s.isUnsubscribed()) {
                                observer.onNext("1");
                                observer.onNext("2");
                                observer.onNext("three");
                                observer.onNext("4");
                                observer.onCompleted();
                            }
                        } finally {
                            latch.countDown();
                        }
                    }
                }).start();
                return s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                int num = Integer.parseInt(v);
                System.out.println(num);
                // doSomething(num);
                count.incrementAndGet();
            }

        });

        // wait for async sequence to complete
        latch.await();

        assertEquals(2, count.get());
        assertNotNull(error.get());
        if (!(error.get() instanceof NumberFormatException)) {
            fail("It should be a NumberFormatException");
        }
    }

    /**
     * The error from the user provided Observer is handled by the subscribe try/catch because this is synchronous
     * 
     * Result: Passes
     */
    @Test
    public void testCustomObservableWithErrorInObserverSynchronous() {
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(Observer<? super String> observer) {
                observer.onNext("1");
                observer.onNext("2");
                observer.onNext("three");
                observer.onNext("4");
                observer.onCompleted();
                return Subscriptions.empty();
            }
        }).subscribe(new Observer<String>() {

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                int num = Integer.parseInt(v);
                System.out.println(num);
                // doSomething(num);
                count.incrementAndGet();
            }

        });
        assertEquals(2, count.get());
        assertNotNull(error.get());
        if (!(error.get() instanceof NumberFormatException)) {
            fail("It should be a NumberFormatException");
        }
    }

    /**
     * The error from the user provided Observable is handled by the subscribe try/catch because this is synchronous
     * 
     * 
     * Result: Passes
     */
    @Test
    public void testCustomObservableWithErrorInObservableSynchronous() {
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(Observer<? super String> observer) {
                observer.onNext("1");
                observer.onNext("2");
                throw new NumberFormatException();
            }
        }).subscribe(new Observer<String>() {

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                System.out.println(v);
                count.incrementAndGet();
            }

        });
        assertEquals(2, count.get());
        assertNotNull(error.get());
        if (!(error.get() instanceof NumberFormatException)) {
            fail("It should be a NumberFormatException");
        }
    }

    @Test
    public void testPublish() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        ConnectableObservable<String> o = Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(final Observer<? super String> observer) {
                final BooleanSubscription subscription = new BooleanSubscription();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        observer.onNext("one");
                        observer.onCompleted();
                    }
                }).start();
                return subscription;
            }
        }).publish();

        final CountDownLatch latch = new CountDownLatch(2);

        // subscribe once
        o.subscribe(new Action1<String>() {

            @Override
            public void call(String v) {
                assertEquals("one", v);
                latch.countDown();
            }
        });

        // subscribe again
        o.subscribe(new Action1<String>() {

            @Override
            public void call(String v) {
                assertEquals("one", v);
                latch.countDown();
            }
        });

        Subscription s = o.connect();
        try {
            if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                fail("subscriptions did not receive values");
            }
            assertEquals(1, counter.get());
        } finally {
            s.unsubscribe();
        }
    }

    @Test
    public void testPublishLast() throws InterruptedException {
        final AtomicInteger count = new AtomicInteger();
        ConnectableObservable<String> connectable = Observable.create(new OnSubscribeFunc<String>() {
            @Override
            public Subscription onSubscribe(final Observer<? super String> observer) {
                count.incrementAndGet();
                final BooleanSubscription subscription = new BooleanSubscription();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        observer.onNext("first");
                        observer.onNext("last");
                        observer.onCompleted();
                    }
                }).start();
                return subscription;
            }
        }).publishLast();

        // subscribe once
        final CountDownLatch latch = new CountDownLatch(1);
        connectable.subscribe(new Action1<String>() {
            @Override
            public void call(String value) {
                assertEquals("last", value);
                latch.countDown();
            }
        });

        // subscribe twice
        connectable.subscribe(new Action1<String>() {
            @Override
            public void call(String _) {
            }
        });

        Subscription subscription = connectable.connect();
        assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        assertEquals(1, count.get());
        subscription.unsubscribe();
    }

    @Test
    public void testReplay() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        ConnectableObservable<String> o = Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(final Observer<? super String> observer) {
                final BooleanSubscription subscription = new BooleanSubscription();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        observer.onNext("one");
                        observer.onCompleted();
                    }
                }).start();
                return subscription;
            }
        }).replay();

        // we connect immediately and it will emit the value
        Subscription s = o.connect();
        try {

            // we then expect the following 2 subscriptions to get that same value
            final CountDownLatch latch = new CountDownLatch(2);

            // subscribe once
            o.subscribe(new Action1<String>() {

                @Override
                public void call(String v) {
                    assertEquals("one", v);
                    latch.countDown();
                }
            });

            // subscribe again
            o.subscribe(new Action1<String>() {

                @Override
                public void call(String v) {
                    assertEquals("one", v);
                    latch.countDown();
                }
            });

            if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                fail("subscriptions did not receive values");
            }
            assertEquals(1, counter.get());
        } finally {
            s.unsubscribe();
        }
    }

    @Test
    public void testCache() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        Observable<String> o = Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(final Observer<? super String> observer) {
                final BooleanSubscription subscription = new BooleanSubscription();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        observer.onNext("one");
                        observer.onCompleted();
                    }
                }).start();
                return subscription;
            }
        }).cache();

        // we then expect the following 2 subscriptions to get that same value
        final CountDownLatch latch = new CountDownLatch(2);

        // subscribe once
        o.subscribe(new Action1<String>() {

            @Override
            public void call(String v) {
                assertEquals("one", v);
                latch.countDown();
            }
        });

        // subscribe again
        o.subscribe(new Action1<String>() {

            @Override
            public void call(String v) {
                assertEquals("one", v);
                latch.countDown();
            }
        });

        if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
            fail("subscriptions did not receive values");
        }
        assertEquals(1, counter.get());
    }

    /**
     * https://github.com/Netflix/RxJava/issues/198
     * 
     * Rx Design Guidelines 5.2
     * 
     * "when calling the Subscribe method that only has an onNext argument, the OnError behavior will be
     * to rethrow the exception on the thread that the message comes out from the Observable.
     * The OnCompleted behavior in this case is to do nothing."
     */
    @Test
    public void testErrorThrownWithoutErrorHandlerSynchronous() {
        try {
            Observable.error(new RuntimeException("failure")).subscribe(new Action1<Object>() {

                @Override
                public void call(Object t1) {
                    // won't get anything
                }

            });
            fail("expected exception");
        } catch (Throwable e) {
            assertEquals("failure", e.getMessage());
        }
    }

    /**
     * https://github.com/Netflix/RxJava/issues/198
     * 
     * Rx Design Guidelines 5.2
     * 
     * "when calling the Subscribe method that only has an onNext argument, the OnError behavior will be
     * to rethrow the exception on the thread that the message comes out from the Observable.
     * The OnCompleted behavior in this case is to do nothing."
     * 
     * @throws InterruptedException
     */
    @Test
    public void testErrorThrownWithoutErrorHandlerAsynchronous() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
        Observable.create(new OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(final Observer<? super String> observer) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            observer.onError(new Error("failure"));
                        } catch (Throwable e) {
                            // without an onError handler it has to just throw on whatever thread invokes it
                            exception.set(e);
                        }
                        latch.countDown();
                    }
                }).start();
                return Subscriptions.empty();
            }
        }).subscribe(new Action1<String>() {

            @Override
            public void call(String t1) {

            }

        });
        // wait for exception
        latch.await(3000, TimeUnit.MILLISECONDS);
        assertNotNull(exception.get());
        assertEquals("failure", exception.get().getMessage());
    }

    @Test
    public void testTakeWithErrorInObserver() {
        final AtomicInteger count = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        Observable.from("1", "2", "three", "4").take(3).subscribe(new Observer<String>() {

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                System.out.println("error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                int num = Integer.parseInt(v);
                System.out.println(num);
                // doSomething(num);
                count.incrementAndGet();
            }

        });
        assertEquals(2, count.get());
        assertNotNull(error.get());
        if (!(error.get() instanceof NumberFormatException)) {
            fail("It should be a NumberFormatException");
        }
    }

    @Test
    public void testOfType() {
        Observable<String> observable = Observable.from(1, "abc", false, 2L).ofType(String.class);

        @SuppressWarnings("unchecked")
        Observer<Object> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, never()).onNext(1);
        verify(aObserver, times(1)).onNext("abc");
        verify(aObserver, never()).onNext(false);
        verify(aObserver, never()).onNext(2L);
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testOfTypeWithPolymorphism() {
        ArrayList<Integer> l1 = new ArrayList<Integer>();
        l1.add(1);
        LinkedList<Integer> l2 = new LinkedList<Integer>();
        l2.add(2);

        @SuppressWarnings("rawtypes")
        Observable<List> observable = Observable.<Object> from(l1, l2, "123").ofType(List.class);

        @SuppressWarnings("unchecked")
        Observer<Object> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, times(1)).onNext(l1);
        verify(aObserver, times(1)).onNext(l2);
        verify(aObserver, never()).onNext("123");
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testContains() {
        Observable<Boolean> observable = Observable.from("a", "b", null).contains("b");

        @SuppressWarnings("unchecked")
        Observer<Object> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, times(1)).onNext(true);
        verify(aObserver, never()).onNext(false);
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testContainsWithInexistence() {
        Observable<Boolean> observable = Observable.from("a", "b", null).contains("c");

        @SuppressWarnings("unchecked")
        Observer<Object> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, times(1)).onNext(false);
        verify(aObserver, never()).onNext(true);
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testContainsWithNull() {
        Observable<Boolean> observable = Observable.from("a", "b", null).contains(null);

        @SuppressWarnings("unchecked")
        Observer<Object> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, times(1)).onNext(true);
        verify(aObserver, never()).onNext(false);
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testContainsWithEmptyObservable() {
        Observable<Boolean> observable = Observable.<String> empty().contains("a");

        @SuppressWarnings("unchecked")
        Observer<Object> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, times(1)).onNext(false);
        verify(aObserver, never()).onNext(true);
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    public void testIgnoreElements() {
        Observable<Integer> observable = Observable.from(1, 2, 3).ignoreElements();

        @SuppressWarnings("unchecked")
        Observer<Integer> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, never()).onNext(any(Integer.class));
        verify(aObserver, never()).onError(any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }
}
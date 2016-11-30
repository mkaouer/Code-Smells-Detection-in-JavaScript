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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.*;
import org.mockito.InOrder;
import org.mockito.Mock;

import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.exceptions.TestException;
import rx.functions.*;
import rx.internal.util.RxRingBuffer;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

public class OperatorDelayTest {
    @Mock
    private Observer<Long> observer;
    @Mock
    private Observer<Long> observer2;

    private TestScheduler scheduler;

    @Before
    public void before() {
        initMocks(this);
        scheduler = new TestScheduler();
    }

    @Test
    public void testDelay() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Observable<Long> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed.subscribe(observer);

        InOrder inOrder = inOrder(observer);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(0L);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(2400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(1L);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(3400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(2L);
        verify(observer, times(1)).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));
    }

    @Test
    public void testLongDelay() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Observable<Long> delayed = source.delay(5L, TimeUnit.SECONDS, scheduler);
        delayed.subscribe(observer);

        InOrder inOrder = inOrder(observer);

        scheduler.advanceTimeTo(5999L, TimeUnit.MILLISECONDS);
        verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(6000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(0L);
        scheduler.advanceTimeTo(6999L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        scheduler.advanceTimeTo(7000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(1L);
        scheduler.advanceTimeTo(7999L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        scheduler.advanceTimeTo(8000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(2L);
        inOrder.verify(observer, times(1)).onCompleted();
        inOrder.verify(observer, never()).onNext(anyLong());
        inOrder.verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelayWithError() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).map(new Func1<Long, Long>() {
            @Override
            public Long call(Long value) {
                if (value == 1L) {
                    throw new RuntimeException("error!");
                }
                return value;
            }
        });
        Observable<Long> delayed = source.delay(1L, TimeUnit.SECONDS, scheduler);
        delayed.subscribe(observer);

        InOrder inOrder = inOrder(observer);

        scheduler.advanceTimeTo(1999L, TimeUnit.MILLISECONDS);
        verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(2000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onError(any(Throwable.class));
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();

        scheduler.advanceTimeTo(5000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        inOrder.verify(observer, never()).onError(any(Throwable.class));
        verify(observer, never()).onCompleted();
    }

    @Test
    public void testDelayWithMultipleSubscriptions() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Observable<Long> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed.subscribe(observer);
        delayed.subscribe(observer2);

        InOrder inOrder = inOrder(observer);
        InOrder inOrder2 = inOrder(observer2);

        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        verify(observer, never()).onNext(anyLong());
        verify(observer2, never()).onNext(anyLong());

        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(0L);
        inOrder2.verify(observer2, times(1)).onNext(0L);

        scheduler.advanceTimeTo(2499L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        inOrder2.verify(observer2, never()).onNext(anyLong());

        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(1L);
        inOrder2.verify(observer2, times(1)).onNext(1L);

        verify(observer, never()).onCompleted();
        verify(observer2, never()).onCompleted();

        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(2L);
        inOrder2.verify(observer2, times(1)).onNext(2L);
        inOrder.verify(observer, never()).onNext(anyLong());
        inOrder2.verify(observer2, never()).onNext(anyLong());
        inOrder.verify(observer, times(1)).onCompleted();
        inOrder2.verify(observer2, times(1)).onCompleted();

        verify(observer, never()).onError(any(Throwable.class));
        verify(observer2, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelaySubscription() {
        Observable<Integer> result = Observable.just(1, 2, 3).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        result.subscribe(o);

        inOrder.verify(o, never()).onNext(any());
        inOrder.verify(o, never()).onCompleted();

        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);

        inOrder.verify(o, times(1)).onNext(1);
        inOrder.verify(o, times(1)).onNext(2);
        inOrder.verify(o, times(1)).onNext(3);
        inOrder.verify(o, times(1)).onCompleted();

        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelaySubscriptionCancelBeforeTime() {
        Observable<Integer> result = Observable.just(1, 2, 3).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);

        Subscription s = result.subscribe(o);
        s.unsubscribe();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);

        verify(o, never()).onNext(any());
        verify(o, never()).onCompleted();
        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableNormal1() {
        PublishSubject<Integer> source = PublishSubject.create();
        final List<PublishSubject<Integer>> delays = new ArrayList<PublishSubject<Integer>>();
        final int n = 10;
        for (int i = 0; i < n; i++) {
            PublishSubject<Integer> delay = PublishSubject.create();
            delays.add(delay);
        }

        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer t1) {
                return delays.get(t1);
            }
        };

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(delayFunc).subscribe(o);

        for (int i = 0; i < n; i++) {
            source.onNext(i);
            delays.get(i).onNext(i);
            inOrder.verify(o).onNext(i);
        }
        source.onCompleted();

        inOrder.verify(o).onCompleted();
        inOrder.verifyNoMoreInteractions();

        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableSingleSend1() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();

        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return delay;
            }
        };
        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(delayFunc).subscribe(o);

        source.onNext(1);
        delay.onNext(1);
        delay.onNext(2);

        inOrder.verify(o).onNext(1);
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableSourceThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();

        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return delay;
            }
        };
        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(delayFunc).subscribe(o);
        source.onNext(1);
        source.onError(new TestException());
        delay.onNext(1);

        inOrder.verify(o).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onNext(any());
        verify(o, never()).onCompleted();
    }

    @Test
    public void testDelayWithObservableDelayFunctionThrows() {
        PublishSubject<Integer> source = PublishSubject.create();

        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                throw new TestException();
            }
        };
        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(delayFunc).subscribe(o);
        source.onNext(1);

        inOrder.verify(o).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onNext(any());
        verify(o, never()).onCompleted();
    }

    @Test
    public void testDelayWithObservableDelayThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();

        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return delay;
            }
        };
        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(delayFunc).subscribe(o);
        source.onNext(1);
        delay.onError(new TestException());

        inOrder.verify(o).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onNext(any());
        verify(o, never()).onCompleted();
    }

    @Test
    public void testDelayWithObservableSubscriptionNormal() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Func0<Observable<Integer>> subFunc = new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return delay;
            }
        };
        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return delay;
            }
        };

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(subFunc, delayFunc).subscribe(o);

        source.onNext(1);
        delay.onNext(1);

        source.onNext(2);
        delay.onNext(2);

        inOrder.verify(o).onNext(2);
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onError(any(Throwable.class));
        verify(o, never()).onCompleted();
    }

    @Test
    public void testDelayWithObservableSubscriptionFunctionThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Func0<Observable<Integer>> subFunc = new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                throw new TestException();
            }
        };
        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return delay;
            }
        };

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(subFunc, delayFunc).subscribe(o);

        source.onNext(1);
        delay.onNext(1);

        source.onNext(2);

        inOrder.verify(o).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onNext(any());
        verify(o, never()).onCompleted();
    }

    @Test
    public void testDelayWithObservableSubscriptionThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Func0<Observable<Integer>> subFunc = new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return delay;
            }
        };
        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return delay;
            }
        };

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(subFunc, delayFunc).subscribe(o);

        source.onNext(1);
        delay.onError(new TestException());

        source.onNext(2);

        inOrder.verify(o).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onNext(any());
        verify(o, never()).onCompleted();
    }

    @Test
    public void testDelayWithObservableEmptyDelayer() {
        PublishSubject<Integer> source = PublishSubject.create();

        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return Observable.empty();
            }
        };
        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(delayFunc).subscribe(o);

        source.onNext(1);
        source.onCompleted();

        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onCompleted();
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableSubscriptionRunCompletion() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> sdelay = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Func0<Observable<Integer>> subFunc = new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return sdelay;
            }
        };
        Func1<Integer, Observable<Integer>> delayFunc = new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return delay;
            }
        };

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        source.delay(subFunc, delayFunc).subscribe(o);

        source.onNext(1);
        sdelay.onCompleted();

        source.onNext(2);
        delay.onNext(2);

        inOrder.verify(o).onNext(2);
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onError(any(Throwable.class));
        verify(o, never()).onCompleted();
    }

    @Test
    public void testDelayWithObservableAsTimed() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);

        final Observable<Long> delayer = Observable.timer(500L, TimeUnit.MILLISECONDS, scheduler);

        Func1<Long, Observable<Long>> delayFunc = new Func1<Long, Observable<Long>>() {
            @Override
            public Observable<Long> call(Long t1) {
                return delayer;
            }
        };

        Observable<Long> delayed = source.delay(delayFunc);
        delayed.subscribe(observer);

        InOrder inOrder = inOrder(observer);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(0L);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(2400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(1L);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(3400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, never()).onNext(anyLong());
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext(2L);
        verify(observer, times(1)).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableReorder() {
        int n = 3;

        PublishSubject<Integer> source = PublishSubject.create();
        final List<PublishSubject<Integer>> subjects = new ArrayList<PublishSubject<Integer>>();
        for (int i = 0; i < n; i++) {
            subjects.add(PublishSubject.<Integer> create());
        }

        Observable<Integer> result = source.delay(new Func1<Integer, Observable<Integer>>() {

            @Override
            public Observable<Integer> call(Integer t1) {
                return subjects.get(t1);
            }
        });

        @SuppressWarnings("unchecked")
        Observer<Integer> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);

        result.subscribe(o);

        for (int i = 0; i < n; i++) {
            source.onNext(i);
        }
        source.onCompleted();

        inOrder.verify(o, never()).onNext(anyInt());
        inOrder.verify(o, never()).onCompleted();

        for (int i = n - 1; i >= 0; i--) {
            subjects.get(i).onCompleted();
            inOrder.verify(o).onNext(i);
        }

        inOrder.verify(o).onCompleted();

        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testDelayEmitsEverything() {
        Observable<Integer> source = Observable.range(1, 5);
        Observable<Integer> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed = delayed.doOnEach(new Action1<Notification<? super Integer>>() {

            @Override
            public void call(Notification<? super Integer> t1) {
                System.out.println(t1);
            }

        });
        TestSubscriber<Integer> observer = new TestSubscriber<Integer>();
        delayed.subscribe(observer);
        // all will be delivered after 500ms since range does not delay between them
        scheduler.advanceTimeBy(500L, TimeUnit.MILLISECONDS);
        observer.assertReceivedOnNext(Arrays.asList(1, 2, 3, 4, 5));
    }

    @Test
    public void testBackpressureWithTimedDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Observable.range(1, RxRingBuffer.SIZE * 2)
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .map(new Func1<Integer, Integer>() {

                    int c = 0;

                    @Override
                    public Integer call(Integer t) {
                        if (c++ <= 0) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                        return t;
                    }

                }).subscribe(ts);

        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        assertEquals(RxRingBuffer.SIZE * 2, ts.getOnNextEvents().size());
    }

    @Test
    public void testBackpressureWithSubscriptionTimedDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Observable.range(1, RxRingBuffer.SIZE * 2)
                .delaySubscription(100, TimeUnit.MILLISECONDS)
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .map(new Func1<Integer, Integer>() {

                    int c = 0;

                    @Override
                    public Integer call(Integer t) {
                        if (c++ <= 0) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                        return t;
                    }

                }).subscribe(ts);

        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        assertEquals(RxRingBuffer.SIZE * 2, ts.getOnNextEvents().size());
    }

    @Test
    public void testBackpressureWithSelectorDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Observable.range(1, RxRingBuffer.SIZE * 2)
                .delay(new Func1<Integer, Observable<Long>>() {

                    @Override
                    public Observable<Long> call(Integer i) {
                        return Observable.timer(100, TimeUnit.MILLISECONDS);
                    }

                })
                .observeOn(Schedulers.computation())
                .map(new Func1<Integer, Integer>() {

                    int c = 0;

                    @Override
                    public Integer call(Integer t) {
                        if (c++ <= 0) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                        return t;
                    }

                }).subscribe(ts);

        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        assertEquals(RxRingBuffer.SIZE * 2, ts.getOnNextEvents().size());
    }

    @Test
    public void testBackpressureWithSelectorDelayAndSubscriptionDelay() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Observable.range(1, RxRingBuffer.SIZE * 2)
                .delay(new Func0<Observable<Long>>() {

                    @Override
                    public Observable<Long> call() {
                        return Observable.timer(500, TimeUnit.MILLISECONDS);
                    }
                }, new Func1<Integer, Observable<Long>>() {

                    @Override
                    public Observable<Long> call(Integer i) {
                        return Observable.timer(100, TimeUnit.MILLISECONDS);
                    }

                })
                .observeOn(Schedulers.computation())
                .map(new Func1<Integer, Integer>() {

                    int c = 0;

                    @Override
                    public Integer call(Integer t) {
                        if (c++ <= 0) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                        return t;
                    }

                }).subscribe(ts);

        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        assertEquals(RxRingBuffer.SIZE * 2, ts.getOnNextEvents().size());
    }

    @Test
    public void testErrorRunsBeforeOnNext() {
        TestScheduler test = Schedulers.test();

        PublishSubject<Integer> ps = PublishSubject.create();

        TestSubscriber<Integer> ts = TestSubscriber.create();

        ps.delay(1, TimeUnit.SECONDS, test).subscribe(ts);

        ps.onNext(1);

        test.advanceTimeBy(500, TimeUnit.MILLISECONDS);

        ps.onError(new TestException());

        test.advanceTimeBy(1, TimeUnit.SECONDS);

        ts.assertNoValues();
        ts.assertError(TestException.class);
        ts.assertNotCompleted();
    }

    @Test
    public void delaySubscriptionCancelBeforeTime() {
        PublishSubject<Integer> source = PublishSubject.create();

        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();

        source.delaySubscription(100, TimeUnit.MILLISECONDS, scheduler).subscribe(ts);

        Assert.assertFalse("source subscribed?", source.hasObservers());

        ts.unsubscribe();

        Assert.assertFalse("source subscribed?", source.hasObservers());

        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);

        Assert.assertFalse("source subscribed?", source.hasObservers());
    }

    @Test
    public void delayAndTakeUntilNeverSubscribeToSource() {
        PublishSubject<Integer> interrupt = PublishSubject.create();
        final AtomicBoolean subscribed = new AtomicBoolean(false);
        TestScheduler testScheduler = new TestScheduler();

        Observable.just(1)
        .doOnSubscribe(new Action0() {
            @Override
            public void call() {
                subscribed.set(true);
            }
        })
        .delaySubscription(1, TimeUnit.SECONDS, testScheduler)
        .takeUntil(interrupt)
        .subscribe();

        interrupt.onNext(9000);
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        Assert.assertFalse(subscribed.get());
    }

}

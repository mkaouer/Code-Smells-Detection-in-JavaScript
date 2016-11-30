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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.TestException;
import rx.functions.Action1;
import rx.observers.TestSubscriber;

public class OperatorMergeDelayErrorTest {

    @Mock
    Observer<String> stringObserver;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testErrorDelayed1() {
        final Observable<String> o1 = Observable.create(new TestErrorObservable("four", null, "six")); // we expect to lose "six" from the source (and it should never be sent by the source since onError was called
        final Observable<String> o2 = Observable.create(new TestErrorObservable("one", "two", "three"));

        Observable<String> m = Observable.mergeDelayError(o1, o2);
        m.subscribe(stringObserver);

        verify(stringObserver, times(1)).onError(any(NullPointerException.class));
        verify(stringObserver, never()).onCompleted();
        verify(stringObserver, times(1)).onNext("one");
        verify(stringObserver, times(1)).onNext("two");
        verify(stringObserver, times(1)).onNext("three");
        verify(stringObserver, times(1)).onNext("four");
        verify(stringObserver, times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Observable keeps sending after onError
        // inner observable errors are considered terminal for that source
//        verify(stringObserver, times(1)).onNext("six");
        // inner observable errors are considered terminal for that source
    }

    @Test
    public void testErrorDelayed2() {
        final Observable<String> o1 = Observable.create(new TestErrorObservable("one", "two", "three"));
        final Observable<String> o2 = Observable.create(new TestErrorObservable("four", null, "six")); // we expect to lose "six" from the source (and it should never be sent by the source since onError was called
        final Observable<String> o3 = Observable.create(new TestErrorObservable("seven", "eight", null));
        final Observable<String> o4 = Observable.create(new TestErrorObservable("nine"));

        Observable<String> m = Observable.mergeDelayError(o1, o2, o3, o4);
        m.subscribe(stringObserver);

        verify(stringObserver, times(1)).onError(any(NullPointerException.class));
        verify(stringObserver, never()).onCompleted();
        verify(stringObserver, times(1)).onNext("one");
        verify(stringObserver, times(1)).onNext("two");
        verify(stringObserver, times(1)).onNext("three");
        verify(stringObserver, times(1)).onNext("four");
        verify(stringObserver, times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Observable keeps sending after onError
        // inner observable errors are considered terminal for that source
//        verify(stringObserver, times(1)).onNext("six");
        verify(stringObserver, times(1)).onNext("seven");
        verify(stringObserver, times(1)).onNext("eight");
        verify(stringObserver, times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed3() {
        final Observable<String> o1 = Observable.create(new TestErrorObservable("one", "two", "three"));
        final Observable<String> o2 = Observable.create(new TestErrorObservable("four", "five", "six"));
        final Observable<String> o3 = Observable.create(new TestErrorObservable("seven", "eight", null));
        final Observable<String> o4 = Observable.create(new TestErrorObservable("nine"));

        Observable<String> m = Observable.mergeDelayError(o1, o2, o3, o4);
        m.subscribe(stringObserver);

        verify(stringObserver, times(1)).onError(any(NullPointerException.class));
        verify(stringObserver, never()).onCompleted();
        verify(stringObserver, times(1)).onNext("one");
        verify(stringObserver, times(1)).onNext("two");
        verify(stringObserver, times(1)).onNext("three");
        verify(stringObserver, times(1)).onNext("four");
        verify(stringObserver, times(1)).onNext("five");
        verify(stringObserver, times(1)).onNext("six");
        verify(stringObserver, times(1)).onNext("seven");
        verify(stringObserver, times(1)).onNext("eight");
        verify(stringObserver, times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed4() {
        final Observable<String> o1 = Observable.create(new TestErrorObservable("one", "two", "three"));
        final Observable<String> o2 = Observable.create(new TestErrorObservable("four", "five", "six"));
        final Observable<String> o3 = Observable.create(new TestErrorObservable("seven", "eight"));
        final Observable<String> o4 = Observable.create(new TestErrorObservable("nine", null));

        Observable<String> m = Observable.mergeDelayError(o1, o2, o3, o4);
        m.subscribe(stringObserver);

        verify(stringObserver, times(1)).onError(any(NullPointerException.class));
        verify(stringObserver, never()).onCompleted();
        verify(stringObserver, times(1)).onNext("one");
        verify(stringObserver, times(1)).onNext("two");
        verify(stringObserver, times(1)).onNext("three");
        verify(stringObserver, times(1)).onNext("four");
        verify(stringObserver, times(1)).onNext("five");
        verify(stringObserver, times(1)).onNext("six");
        verify(stringObserver, times(1)).onNext("seven");
        verify(stringObserver, times(1)).onNext("eight");
        verify(stringObserver, times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed4WithThreading() {
        final TestAsyncErrorObservable o1 = new TestAsyncErrorObservable("one", "two", "three");
        final TestAsyncErrorObservable o2 = new TestAsyncErrorObservable("four", "five", "six");
        final TestAsyncErrorObservable o3 = new TestAsyncErrorObservable("seven", "eight");
        // throw the error at the very end so no onComplete will be called after it
        final TestAsyncErrorObservable o4 = new TestAsyncErrorObservable("nine", null);

        Observable<String> m = Observable.mergeDelayError(Observable.create(o1), Observable.create(o2), Observable.create(o3), Observable.create(o4));
        m.subscribe(stringObserver);

        try {
            o1.t.join();
            o2.t.join();
            o3.t.join();
            o4.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(stringObserver, times(1)).onNext("one");
        verify(stringObserver, times(1)).onNext("two");
        verify(stringObserver, times(1)).onNext("three");
        verify(stringObserver, times(1)).onNext("four");
        verify(stringObserver, times(1)).onNext("five");
        verify(stringObserver, times(1)).onNext("six");
        verify(stringObserver, times(1)).onNext("seven");
        verify(stringObserver, times(1)).onNext("eight");
        verify(stringObserver, times(1)).onNext("nine");
        verify(stringObserver, times(1)).onError(any(NullPointerException.class));
        verify(stringObserver, never()).onCompleted();
    }

    @Test
    public void testCompositeErrorDelayed1() {
        final Observable<String> o1 = Observable.create(new TestErrorObservable("four", null, "six")); // we expect to lose "six" from the source (and it should never be sent by the source since onError was called
        final Observable<String> o2 = Observable.create(new TestErrorObservable("one", "two", null));

        Observable<String> m = Observable.mergeDelayError(o1, o2);
        m.subscribe(stringObserver);

        verify(stringObserver, times(1)).onError(any(CompositeException.class));
        verify(stringObserver, never()).onCompleted();
        verify(stringObserver, times(1)).onNext("one");
        verify(stringObserver, times(1)).onNext("two");
        verify(stringObserver, times(0)).onNext("three");
        verify(stringObserver, times(1)).onNext("four");
        verify(stringObserver, times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Observable keeps sending after onError
        // inner observable errors are considered terminal for that source
//        verify(stringObserver, times(1)).onNext("six");
    }

    @Test
    public void testCompositeErrorDelayed2() {
        final Observable<String> o1 = Observable.create(new TestErrorObservable("four", null, "six")); // we expect to lose "six" from the source (and it should never be sent by the source since onError was called
        final Observable<String> o2 = Observable.create(new TestErrorObservable("one", "two", null));

        Observable<String> m = Observable.mergeDelayError(o1, o2);
        CaptureObserver w = new CaptureObserver();
        m.subscribe(w);

        assertNotNull(w.e);
        if (w.e instanceof CompositeException) {
            assertEquals(2, ((CompositeException) w.e).getExceptions().size());
            w.e.printStackTrace();
        } else {
            fail("Expecting CompositeException");
        }

    }

    /**
     * The unit tests below are from OperationMerge and should ensure the normal merge functionality is correct.
     */

    @Test
    public void testMergeObservableOfObservables() {
        final Observable<String> o1 = Observable.create(new TestSynchronousObservable());
        final Observable<String> o2 = Observable.create(new TestSynchronousObservable());

        Observable<Observable<String>> observableOfObservables = Observable.create(new Observable.OnSubscribe<Observable<String>>() {

            @Override
            public void call(Subscriber<? super Observable<String>> observer) {
                // simulate what would happen in an observable
                observer.onNext(o1);
                observer.onNext(o2);
                observer.onCompleted();
            }

        });
        Observable<String> m = Observable.mergeDelayError(observableOfObservables);
        m.subscribe(stringObserver);

        verify(stringObserver, never()).onError(any(Throwable.class));
        verify(stringObserver, times(1)).onCompleted();
        verify(stringObserver, times(2)).onNext("hello");
    }

    @Test
    public void testMergeArray() {
        final Observable<String> o1 = Observable.create(new TestSynchronousObservable());
        final Observable<String> o2 = Observable.create(new TestSynchronousObservable());

        Observable<String> m = Observable.mergeDelayError(o1, o2);
        m.subscribe(stringObserver);

        verify(stringObserver, never()).onError(any(Throwable.class));
        verify(stringObserver, times(2)).onNext("hello");
        verify(stringObserver, times(1)).onCompleted();
    }

    @Test
    public void testMergeList() {
        final Observable<String> o1 = Observable.create(new TestSynchronousObservable());
        final Observable<String> o2 = Observable.create(new TestSynchronousObservable());
        List<Observable<String>> listOfObservables = new ArrayList<Observable<String>>();
        listOfObservables.add(o1);
        listOfObservables.add(o2);

        Observable<String> m = Observable.mergeDelayError(Observable.from(listOfObservables));
        m.subscribe(stringObserver);

        verify(stringObserver, never()).onError(any(Throwable.class));
        verify(stringObserver, times(1)).onCompleted();
        verify(stringObserver, times(2)).onNext("hello");
    }

    @Test
    public void testMergeArrayWithThreading() {
        final TestASynchronousObservable o1 = new TestASynchronousObservable();
        final TestASynchronousObservable o2 = new TestASynchronousObservable();

        Observable<String> m = Observable.mergeDelayError(Observable.create(o1), Observable.create(o2));
        m.subscribe(stringObserver);

        try {
            o1.t.join();
            o2.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(stringObserver, never()).onError(any(Throwable.class));
        verify(stringObserver, times(2)).onNext("hello");
        verify(stringObserver, times(1)).onCompleted();
    }

    @Test(timeout = 1000L)
    public void testSynchronousError() {
        final Observable<Observable<String>> o1 = Observable.error(new RuntimeException("unit test"));

        final CountDownLatch latch = new CountDownLatch(1);
        Observable.mergeDelayError(o1).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                fail("Expected onError path");
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onNext(String s) {
                fail("Expected onError path");
            }
        });

        try {
            latch.await();
        } catch (InterruptedException ex) {
            fail("interrupted");
        }
    }

    private static class TestSynchronousObservable implements Observable.OnSubscribe<String> {

        @Override
        public void call(Subscriber<? super String> observer) {
            observer.onNext("hello");
            observer.onCompleted();
        }
    }

    private static class TestASynchronousObservable implements Observable.OnSubscribe<String> {
        Thread t;

        @Override
        public void call(final Subscriber<? super String> observer) {
            t = new Thread(new Runnable() {

                @Override
                public void run() {
                    observer.onNext("hello");
                    observer.onCompleted();
                }

            });
            t.start();
        }
    }

    private static class TestErrorObservable implements Observable.OnSubscribe<String> {

        String[] valuesToReturn;

        TestErrorObservable(String... values) {
            valuesToReturn = values;
        }

        @Override
        public void call(Subscriber<? super String> observer) {
            boolean errorThrown = false;
            for (String s : valuesToReturn) {
                if (s == null) {
                    System.out.println("throwing exception");
                    observer.onError(new NullPointerException());
                    errorThrown = true;
                    // purposefully not returning here so it will continue calling onNext
                    // so that we also test that we handle bad sequences like this
                } else {
                    observer.onNext(s);
                }
            }
            if (!errorThrown) {
                observer.onCompleted();
            }
        }
    }

    private static class TestAsyncErrorObservable implements Observable.OnSubscribe<String> {

        String[] valuesToReturn;

        TestAsyncErrorObservable(String... values) {
            valuesToReturn = values;
        }

        Thread t;

        @Override
        public void call(final Subscriber<? super String> observer) {
            t = new Thread(new Runnable() {

                @Override
                public void run() {
                    for (String s : valuesToReturn) {
                        if (s == null) {
                            System.out.println("throwing exception");
                            try {
                                Thread.sleep(100);
                            } catch (Throwable e) {

                            }
                            observer.onError(new NullPointerException());
                            return;
                        } else {
                            observer.onNext(s);
                        }
                    }
                    System.out.println("subscription complete");
                    observer.onCompleted();
                }

            });
            t.start();
        }
    }

    private static class CaptureObserver implements Observer<String> {
        volatile Throwable e;

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            this.e = e;
        }

        @Override
        public void onNext(String args) {

        }

    }
    @Test
    public void testMergeSourceWhichDoesntPropagateExceptionBack() {
        Observable<Integer> source = Observable.create(new OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> t1) {
                try {
                    t1.onNext(0);
                } catch (Throwable swallow) {
                    
                }
                t1.onNext(1);
                t1.onCompleted();
            }
        });
        
        Observable<Integer> result = Observable.mergeDelayError(source, Observable.just(2));
        
        @SuppressWarnings("unchecked")
        final Observer<Integer> o = mock(Observer.class);
        InOrder inOrder = inOrder(o);
        
        result.unsafeSubscribe(new Subscriber<Integer>() {
            int calls;
            @Override
            public void onNext(Integer t) {
                if (calls++ == 0) {
                    throw new TestException();
                }
                o.onNext(t);
            }

            @Override
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override
            public void onCompleted() {
                o.onCompleted();
            }
            
        });
        
        /*
         * If the child onNext throws, why would we keep accepting values from
         * other sources?
         */
        inOrder.verify(o).onNext(2);
        inOrder.verify(o, never()).onNext(0);
        inOrder.verify(o, never()).onNext(1);
        inOrder.verify(o, never()).onNext(anyInt());
        inOrder.verify(o).onError(any(TestException.class));
        verify(o, never()).onCompleted();
    }

    @Test
    public void testErrorInParentObservable() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Observable.mergeDelayError(
                Observable.just(Observable.just(1), Observable.just(2))
                        .startWith(Observable.<Integer> error(new RuntimeException()))
                ).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertTerminalEvent();
        ts.assertReceivedOnNext(Arrays.asList(1, 2));
        assertEquals(1, ts.getOnErrorEvents().size());

    }

    @Test
    public void testErrorInParentObservableDelayed() throws Exception {
        for (int i = 0; i < 50; i++) {
            final TestASynchronous1sDelayedObservable o1 = new TestASynchronous1sDelayedObservable();
            final TestASynchronous1sDelayedObservable o2 = new TestASynchronous1sDelayedObservable();
            Observable<Observable<String>> parentObservable = Observable.create(new Observable.OnSubscribe<Observable<String>>() {
                @Override
                public void call(Subscriber<? super Observable<String>> op) {
                    op.onNext(Observable.create(o1));
                    op.onNext(Observable.create(o2));
                    op.onError(new NullPointerException("throwing exception in parent"));
                }
            });
    
            @SuppressWarnings("unchecked")
            Observer<String> stringObserver = mock(Observer.class);
            
            TestSubscriber<String> ts = new TestSubscriber<String>(stringObserver);
            Observable<String> m = Observable.mergeDelayError(parentObservable);
            m.subscribe(ts);
            System.out.println("testErrorInParentObservableDelayed | " + i);
            ts.awaitTerminalEvent(2000, TimeUnit.MILLISECONDS);
            ts.assertTerminalEvent();
    
            verify(stringObserver, times(2)).onNext("hello");
            verify(stringObserver, times(1)).onError(any(NullPointerException.class));
            verify(stringObserver, never()).onCompleted();
        }
    }

    private static class TestASynchronous1sDelayedObservable implements Observable.OnSubscribe<String> {
        Thread t;

        @Override
        public void call(final Subscriber<? super String> observer) {
            t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        observer.onError(e);
                    }
                    observer.onNext("hello");
                    observer.onCompleted();
                }

            });
            t.start();
        }
    }
    @Test
    public void testDelayErrorMaxConcurrent() {
        final List<Long> requests = new ArrayList<Long>();
        Observable<Integer> source = Observable.mergeDelayError(Observable.just(
                Observable.just(1).asObservable(), 
                Observable.<Integer>error(new TestException())).doOnRequest(new Action1<Long>() {
                    @Override
                    public void call(Long t1) {
                        requests.add(t1);
                    }
                }), 1);
        
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        
        source.subscribe(ts);
        
        ts.assertReceivedOnNext(Arrays.asList(1));
        ts.assertTerminalEvent();
        assertEquals(1, ts.getOnErrorEvents().size());
        assertTrue(ts.getOnErrorEvents().get(0) instanceof TestException);
        assertEquals(Arrays.asList(1L, 1L, 1L), requests);
    }
}
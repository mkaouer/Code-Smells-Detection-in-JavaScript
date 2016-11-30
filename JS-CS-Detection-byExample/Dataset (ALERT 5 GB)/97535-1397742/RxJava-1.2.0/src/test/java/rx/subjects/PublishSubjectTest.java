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
package rx.subjects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.mockito.InOrder;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.exceptions.*;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

public class PublishSubjectTest {

    @Test
    public void testCompleted() {
        PublishSubject<String> subject = PublishSubject.create();

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        subject.subscribe(observer);

        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");
        subject.onCompleted();

        @SuppressWarnings("unchecked")
        Observer<String> anotherObserver = mock(Observer.class);
        subject.subscribe(anotherObserver);

        subject.onNext("four");
        subject.onCompleted();
        subject.onError(new Throwable());

        assertCompletedObserver(observer);
        assertNeverObserver(anotherObserver);
    }

    @Test
    public void testCompletedStopsEmittingData() {
        PublishSubject<Object> channel = PublishSubject.create();
        @SuppressWarnings("unchecked")
        Observer<Object> observerA = mock(Observer.class);
        @SuppressWarnings("unchecked")
        Observer<Object> observerB = mock(Observer.class);
        @SuppressWarnings("unchecked")
        Observer<Object> observerC = mock(Observer.class);

        Subscription a = channel.subscribe(observerA);
        channel.subscribe(observerB);

        InOrder inOrderA = inOrder(observerA);
        InOrder inOrderB = inOrder(observerB);
        InOrder inOrderC = inOrder(observerC);

        channel.onNext(42);

        inOrderA.verify(observerA).onNext(42);
        inOrderB.verify(observerB).onNext(42);

        a.unsubscribe();
        inOrderA.verifyNoMoreInteractions();

        channel.onNext(4711);

        inOrderB.verify(observerB).onNext(4711);

        channel.onCompleted();

        inOrderB.verify(observerB).onCompleted();

        channel.subscribe(observerC);

        inOrderC.verify(observerC).onCompleted();

        channel.onNext(13);

        inOrderB.verifyNoMoreInteractions();
        inOrderC.verifyNoMoreInteractions();
    }

    private void assertCompletedObserver(Observer<String> observer) {
        verify(observer, times(1)).onNext("one");
        verify(observer, times(1)).onNext("two");
        verify(observer, times(1)).onNext("three");
        verify(observer, never()).onNext("four");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onCompleted();
        verifyNoMoreInteractions(observer);
    }

    private void assertNeverObserver(Observer<String> observer) {
        verify(observer, never()).onNext("one");
        verify(observer, never()).onNext("two");
        verify(observer, never()).onNext("three");
        verify(observer, never()).onNext("four");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onCompleted();
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void testError() {
        PublishSubject<String> subject = PublishSubject.create();

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        subject.subscribe(observer);

        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");
        subject.onError(testException);

        @SuppressWarnings("unchecked")
        Observer<String> anotherObserver = mock(Observer.class);
        subject.subscribe(anotherObserver);

        subject.onNext("four");
        subject.onError(new Throwable());
        subject.onCompleted();

        assertErrorObserver(observer);
        assertNeverErrorObserver(anotherObserver);
    }

    private void assertErrorObserver(Observer<String> observer) {
        verify(observer, times(1)).onNext("one");
        verify(observer, times(1)).onNext("two");
        verify(observer, times(1)).onNext("three");
        verify(observer, times(1)).onError(testException);
        verify(observer, never()).onCompleted();
        verifyNoMoreInteractions(observer);
    }

    private void assertNeverErrorObserver(Observer<String> observer) {
        verify(observer, never()).onNext("one");
        verify(observer, never()).onNext("two");
        verify(observer, never()).onNext("three");
        verify(observer, never()).onNext("four");
        verify(observer, times(1)).onError(any(Throwable.class));
        verify(observer, never()).onCompleted();
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void testSubscribeMidSequence() {
        PublishSubject<String> subject = PublishSubject.create();

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        subject.subscribe(observer);

        subject.onNext("one");
        subject.onNext("two");

        assertObservedUntilTwo(observer);

        @SuppressWarnings("unchecked")
        Observer<String> anotherObserver = mock(Observer.class);
        subject.subscribe(anotherObserver);

        subject.onNext("three");
        subject.onCompleted();

        assertCompletedObserver(observer);
        assertCompletedStartingWithThreeObserver(anotherObserver);
    }

    private void assertCompletedStartingWithThreeObserver(Observer<String> observer) {
        verify(observer, never()).onNext("one");
        verify(observer, never()).onNext("two");
        verify(observer, times(1)).onNext("three");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onCompleted();
    }

    @Test
    public void testUnsubscribeFirstObserver() {
        PublishSubject<String> subject = PublishSubject.create();

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        Subscription subscription = subject.subscribe(observer);

        subject.onNext("one");
        subject.onNext("two");

        subscription.unsubscribe();
        assertObservedUntilTwo(observer);

        @SuppressWarnings("unchecked")
        Observer<String> anotherObserver = mock(Observer.class);
        subject.subscribe(anotherObserver);

        subject.onNext("three");
        subject.onCompleted();

        assertObservedUntilTwo(observer);
        assertCompletedStartingWithThreeObserver(anotherObserver);
    }

    private void assertObservedUntilTwo(Observer<String> observer) {
        verify(observer, times(1)).onNext("one");
        verify(observer, times(1)).onNext("two");
        verify(observer, never()).onNext("three");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, never()).onCompleted();
    }

    @Test
    public void testNestedSubscribe() {
        final PublishSubject<Integer> s = PublishSubject.create();

        final AtomicInteger countParent = new AtomicInteger();
        final AtomicInteger countChildren = new AtomicInteger();
        final AtomicInteger countTotal = new AtomicInteger();

        final ArrayList<String> list = new ArrayList<String>();

        s.flatMap(new Func1<Integer, Observable<String>>() {

            @Override
            public Observable<String> call(final Integer v) {
                countParent.incrementAndGet();

                // then subscribe to subject again (it will not receive the previous value)
                return s.map(new Func1<Integer, String>() {

                    @Override
                    public String call(Integer v2) {
                        countChildren.incrementAndGet();
                        return "Parent: " + v + " Child: " + v2;
                    }

                });
            }

        }).subscribe(new Action1<String>() {

            @Override
            public void call(String v) {
                countTotal.incrementAndGet();
                list.add(v);
            }

        });

        for (int i = 0; i < 10; i++) {
            s.onNext(i);
        }
        s.onCompleted();

        //            System.out.println("countParent: " + countParent.get());
        //            System.out.println("countChildren: " + countChildren.get());
        //            System.out.println("countTotal: " + countTotal.get());

        // 9+8+7+6+5+4+3+2+1+0 == 45
        assertEquals(45, list.size());
    }

    /**
     * Should be able to unsubscribe all Observers, have it stop emitting, then subscribe new ones and it start emitting again.
     */
    @Test
    public void testReSubscribe() {
        final PublishSubject<Integer> ps = PublishSubject.create();

        @SuppressWarnings("unchecked")
        Observer<Integer> o1 = mock(Observer.class);
        Subscription s1 = ps.subscribe(o1);

        // emit
        ps.onNext(1);

        // validate we got it
        InOrder inOrder1 = inOrder(o1);
        inOrder1.verify(o1, times(1)).onNext(1);
        inOrder1.verifyNoMoreInteractions();

        // unsubscribe
        s1.unsubscribe();

        // emit again but nothing will be there to receive it
        ps.onNext(2);

        @SuppressWarnings("unchecked")
        Observer<Integer> o2 = mock(Observer.class);
        Subscription s2 = ps.subscribe(o2);

        // emit
        ps.onNext(3);

        // validate we got it
        InOrder inOrder2 = inOrder(o2);
        inOrder2.verify(o2, times(1)).onNext(3);
        inOrder2.verifyNoMoreInteractions();

        s2.unsubscribe();
    }

    private final Throwable testException = new Throwable();

    @Test(timeout = 5000)
    public void testUnsubscriptionCase() {
        PublishSubject<String> src = PublishSubject.create();

        for (int i = 0; i < 10; i++) {
            @SuppressWarnings("unchecked")
            final Observer<Object> o = mock(Observer.class);
            InOrder inOrder = inOrder(o);
            String v = "" + i;
            System.out.printf("Turn: %d%n", i);
            src.first()
                .flatMap(new Func1<String, Observable<String>>() {

                    @Override
                    public Observable<String> call(String t1) {
                        return Observable.just(t1 + ", " + t1);
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String t) {
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
            src.onNext(v);

            inOrder.verify(o).onNext(v + ", " + v);
            inOrder.verify(o).onCompleted();
            verify(o, never()).onError(any(Throwable.class));
        }
    }


    @Test
    public void testOnErrorThrowsDoesntPreventDelivery() {
        PublishSubject<String> ps = PublishSubject.create();

        ps.subscribe();
        TestSubscriber<String> ts = new TestSubscriber<String>();
        ps.subscribe(ts);

        try {
            ps.onError(new RuntimeException("an exception"));
            fail("expect OnErrorNotImplementedException");
        } catch (OnErrorNotImplementedException e) {
            // ignore
        }
        // even though the onError above throws we should still receive it on the other subscriber
        assertEquals(1, ts.getOnErrorEvents().size());
    }

    /**
     * This one has multiple failures so should get a CompositeException
     */
    @Test
    public void testOnErrorThrowsDoesntPreventDelivery2() {
        PublishSubject<String> ps = PublishSubject.create();

        ps.subscribe();
        ps.subscribe();
        TestSubscriber<String> ts = new TestSubscriber<String>();
        ps.subscribe(ts);
        ps.subscribe();
        ps.subscribe();
        ps.subscribe();

        try {
            ps.onError(new RuntimeException("an exception"));
            fail("expect OnErrorNotImplementedException");
        } catch (CompositeException e) {
            // we should have 5 of them
            assertEquals(5, e.getExceptions().size());
        }
        // even though the onError above throws we should still receive it on the other subscriber
        assertEquals(1, ts.getOnErrorEvents().size());
    }
    @Test
    public void testCurrentStateMethodsNormal() {
        PublishSubject<Object> as = PublishSubject.create();

        assertFalse(as.hasThrowable());
        assertFalse(as.hasCompleted());
        assertNull(as.getThrowable());

        as.onNext(1);

        assertFalse(as.hasThrowable());
        assertFalse(as.hasCompleted());
        assertNull(as.getThrowable());

        as.onCompleted();

        assertFalse(as.hasThrowable());
        assertTrue(as.hasCompleted());
        assertNull(as.getThrowable());
    }

    @Test
    public void testCurrentStateMethodsEmpty() {
        PublishSubject<Object> as = PublishSubject.create();

        assertFalse(as.hasThrowable());
        assertFalse(as.hasCompleted());
        assertNull(as.getThrowable());

        as.onCompleted();

        assertFalse(as.hasThrowable());
        assertTrue(as.hasCompleted());
        assertNull(as.getThrowable());
    }
    @Test
    public void testCurrentStateMethodsError() {
        PublishSubject<Object> as = PublishSubject.create();

        assertFalse(as.hasThrowable());
        assertFalse(as.hasCompleted());
        assertNull(as.getThrowable());

        as.onError(new TestException());

        assertTrue(as.hasThrowable());
        assertFalse(as.hasCompleted());
        assertTrue(as.getThrowable() instanceof TestException);
    }

    @Test
    public void testPublishSubjectValueRelay() {
        PublishSubject<Integer> async = PublishSubject.create();
        async.onNext(1);
        async.onCompleted();

        assertFalse(async.hasObservers());
        assertTrue(async.hasCompleted());
        assertFalse(async.hasThrowable());
        assertNull(async.getThrowable());
    }

    @Test
    public void testPublishSubjectValueEmpty() {
        PublishSubject<Integer> async = PublishSubject.create();
        async.onCompleted();

        assertFalse(async.hasObservers());
        assertTrue(async.hasCompleted());
        assertFalse(async.hasThrowable());
        assertNull(async.getThrowable());
    }
    @Test
    public void testPublishSubjectValueError() {
        PublishSubject<Integer> async = PublishSubject.create();
        TestException te = new TestException();
        async.onError(te);

        assertFalse(async.hasObservers());
        assertFalse(async.hasCompleted());
        assertTrue(async.hasThrowable());
        assertSame(te, async.getThrowable());
    }

    @Test
    public void backpressureFailFast() {
        PublishSubject<Integer> ps = PublishSubject.create();

        TestSubscriber<Integer> ts = TestSubscriber.create(1);

        ps.subscribe(ts);

        ps.onNext(1);

        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertNotCompleted();

        ps.onNext(2);

        ts.assertValue(1);
        ts.assertError(MissingBackpressureException.class);
        ts.assertNotCompleted();

        assertEquals("PublishSubject: could not emit value due to lack of requests", ts.getOnErrorEvents().get(0).getMessage());
    }

    @Test
    public void crossUnsubscribe() {
        PublishSubject<Integer> ps = PublishSubject.create();

        final TestSubscriber<Integer> ts0 = TestSubscriber.create();

        TestSubscriber<Integer> ts1 = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 2) {
                    ts0.unsubscribe();
                }
            }
        };

        ps.subscribe(ts1);
        ps.subscribe(ts0);

        ps.onNext(1);

        ts0.assertValue(1);
        ts1.assertValue(1);

        ps.onNext(2);
        ps.onCompleted();

        ts0.assertValue(1);
        ts0.assertNoErrors();
        ts0.assertNotCompleted();
        ts0.assertUnsubscribed();

        ts1.assertValues(1, 2);
        ts1.assertNoErrors();
        ts1.assertCompleted();
    }
}

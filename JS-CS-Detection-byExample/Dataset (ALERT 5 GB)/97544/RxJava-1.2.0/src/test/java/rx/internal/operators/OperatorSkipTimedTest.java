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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.mockito.InOrder;

import rx.Observable;
import rx.Observer;
import rx.exceptions.TestException;
import rx.functions.Action0;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

public class OperatorSkipTimedTest {

    @Test
    public void testSkipTimed() {
        TestScheduler scheduler = new TestScheduler();

        PublishSubject<Integer> source = PublishSubject.create();

        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);

        result.subscribe(o);

        source.onNext(1);
        source.onNext(2);
        source.onNext(3);

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        source.onNext(4);
        source.onNext(5);
        source.onNext(6);

        source.onCompleted();

        InOrder inOrder = inOrder(o);

        inOrder.verify(o, never()).onNext(1);
        inOrder.verify(o, never()).onNext(2);
        inOrder.verify(o, never()).onNext(3);
        inOrder.verify(o).onNext(4);
        inOrder.verify(o).onNext(5);
        inOrder.verify(o).onNext(6);
        inOrder.verify(o).onCompleted();
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testSkipTimedFinishBeforeTime() {
        TestScheduler scheduler = new TestScheduler();

        PublishSubject<Integer> source = PublishSubject.create();

        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);

        result.subscribe(o);

        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onCompleted();

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        InOrder inOrder = inOrder(o);

        inOrder.verify(o).onCompleted();
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onNext(any());
        verify(o, never()).onError(any(Throwable.class));
    }

    @Test
    public void testSkipTimedErrorBeforeTime() {
        TestScheduler scheduler = new TestScheduler();

        PublishSubject<Integer> source = PublishSubject.create();

        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);

        result.subscribe(o);

        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onError(new TestException());

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        InOrder inOrder = inOrder(o);

        inOrder.verify(o).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onNext(any());
        verify(o, never()).onCompleted();
    }

    @Test
    public void testSkipTimedErrorAfterTime() {
        TestScheduler scheduler = new TestScheduler();

        PublishSubject<Integer> source = PublishSubject.create();

        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);

        @SuppressWarnings("unchecked")
        Observer<Object> o = mock(Observer.class);

        result.subscribe(o);

        source.onNext(1);
        source.onNext(2);
        source.onNext(3);

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        source.onNext(4);
        source.onNext(5);
        source.onNext(6);

        source.onError(new TestException());

        InOrder inOrder = inOrder(o);

        inOrder.verify(o, never()).onNext(1);
        inOrder.verify(o, never()).onNext(2);
        inOrder.verify(o, never()).onNext(3);
        inOrder.verify(o).onNext(4);
        inOrder.verify(o).onNext(5);
        inOrder.verify(o).onNext(6);
        inOrder.verify(o).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        verify(o, never()).onCompleted();

    }

    @Test
    public void testSkipTimedUnsubscribePropagatesToUpstream() {
        TestScheduler scheduler = new TestScheduler();

        PublishSubject<Integer> source = PublishSubject.create();

        final AtomicBoolean unsub = new AtomicBoolean();
        Observable<Integer> result = source.doOnUnsubscribe(new Action0() {

            @Override
            public void call() {
                unsub.set(true);
            }
        }).skip(1, TimeUnit.SECONDS, scheduler);

        TestSubscriber<Object> ts = TestSubscriber.create();

        result.subscribe(ts);
        source.onNext(1);
        ts.unsubscribe();
        assertTrue(unsub.get());
    }
}

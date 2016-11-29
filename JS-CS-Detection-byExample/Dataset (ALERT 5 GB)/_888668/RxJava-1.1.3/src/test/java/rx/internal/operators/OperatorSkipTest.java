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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.observers.TestSubscriber;

public class OperatorSkipTest {

    @Test
    public void testSkipNegativeElements() {
        try {
            Observable.just("one", "two", "three").skip(-99);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("n >= 0 required but it was -99", e.getMessage());
        }
    }

    @Test
    public void testSkipZeroElements() {

        Observable<String> skip = Observable.just("one", "two", "three").lift(new OperatorSkip<String>(0));

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        skip.subscribe(observer);
        verify(observer, times(1)).onNext("one");
        verify(observer, times(1)).onNext("two");
        verify(observer, times(1)).onNext("three");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onCompleted();
    }

    @Test
    public void testSkipOneElement() {

        Observable<String> skip = Observable.just("one", "two", "three").lift(new OperatorSkip<String>(1));

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        skip.subscribe(observer);
        verify(observer, never()).onNext("one");
        verify(observer, times(1)).onNext("two");
        verify(observer, times(1)).onNext("three");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onCompleted();
    }

    @Test
    public void testSkipTwoElements() {

        Observable<String> skip = Observable.just("one", "two", "three").lift(new OperatorSkip<String>(2));

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        skip.subscribe(observer);
        verify(observer, never()).onNext("one");
        verify(observer, never()).onNext("two");
        verify(observer, times(1)).onNext("three");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onCompleted();
    }

    @Test
    public void testSkipEmptyStream() {

        Observable<String> w = Observable.empty();
        Observable<String> skip = w.lift(new OperatorSkip<String>(1));

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        skip.subscribe(observer);
        verify(observer, never()).onNext(any(String.class));
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onCompleted();
    }

    @Test
    public void testSkipMultipleObservers() {

        Observable<String> skip = Observable.just("one", "two", "three").lift(new OperatorSkip<String>(2));

        @SuppressWarnings("unchecked")
        Observer<String> observer1 = mock(Observer.class);
        skip.subscribe(observer1);

        @SuppressWarnings("unchecked")
        Observer<String> observer2 = mock(Observer.class);
        skip.subscribe(observer2);

        verify(observer1, times(1)).onNext(any(String.class));
        verify(observer1, never()).onError(any(Throwable.class));
        verify(observer1, times(1)).onCompleted();

        verify(observer2, times(1)).onNext(any(String.class));
        verify(observer2, never()).onError(any(Throwable.class));
        verify(observer2, times(1)).onCompleted();
    }

    @Test
    public void testSkipError() {

        Exception e = new Exception();

        Observable<String> ok = Observable.just("one");
        Observable<String> error = Observable.error(e);

        Observable<String> skip = Observable.concat(ok, error).lift(new OperatorSkip<String>(100));

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        skip.subscribe(observer);

        verify(observer, never()).onNext(any(String.class));
        verify(observer, times(1)).onError(e);
        verify(observer, never()).onCompleted();

    }
    
    @Test
    public void testBackpressureMultipleSmallAsyncRequests() throws InterruptedException {
        final AtomicLong requests = new AtomicLong(0);
        TestSubscriber<Long> ts = new TestSubscriber<Long>(0);
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .doOnRequest(new Action1<Long>() {
                    @Override
                    public void call(Long n) {
                        requests.addAndGet(n);
                    }
                }).skip(4).subscribe(ts);
        Thread.sleep(100);
        ts.requestMore(1);
        ts.requestMore(1);
        Thread.sleep(100);
        ts.unsubscribe();
        ts.assertUnsubscribed();
        ts.assertNoErrors();
        assertEquals(6, requests.get());
    }
    
    @Test
    public void testRequestOverflowDoesNotOccur() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(Long.MAX_VALUE-1);
        Observable.range(1, 10).skip(5).subscribe(ts);
        ts.assertTerminalEvent();
        ts.assertCompleted();
        ts.assertNoErrors();
        assertEquals(Arrays.asList(6,7,8,9,10), ts.getOnNextEvents());
    }
    
}

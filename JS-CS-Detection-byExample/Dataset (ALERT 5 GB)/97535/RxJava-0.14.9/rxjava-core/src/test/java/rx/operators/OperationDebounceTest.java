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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.TestScheduler;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action0;

public class OperationDebounceTest {

    private TestScheduler scheduler;
    private Observer<String> observer;

    @Before
    @SuppressWarnings("unchecked")
    public void before() {
        scheduler = new TestScheduler();
        observer = mock(Observer.class);
    }

    @Test
    public void testDebounceWithCompleted() {
        Observable<String> source = Observable.create(new Observable.OnSubscribeFunc<String>() {
            @Override
            public Subscription onSubscribe(Observer<? super String> observer) {
                publishNext(observer, 100, "one");    // Should be skipped since "two" will arrive before the timeout expires.
                publishNext(observer, 400, "two");    // Should be published since "three" will arrive after the timeout expires.
                publishNext(observer, 900, "three");   // Should be skipped since onCompleted will arrive before the timeout expires.
                publishCompleted(observer, 1000);     // Should be published as soon as the timeout expires.

                return Subscriptions.empty();
            }
        });

        Observable<String> sampled = Observable.create(OperationDebounce.debounce(source, 400, TimeUnit.MILLISECONDS, scheduler));
        sampled.subscribe(observer);

        scheduler.advanceTimeTo(0, TimeUnit.MILLISECONDS);
        InOrder inOrder = inOrder(observer);
        // must go to 800 since it must be 400 after when two is sent, which is at 400
        scheduler.advanceTimeTo(800, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onNext("two");
        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onCompleted();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDebounceNeverEmits() {
        Observable<String> source = Observable.create(new Observable.OnSubscribeFunc<String>() {
            @Override
            public Subscription onSubscribe(Observer<? super String> observer) {
                // all should be skipped since they are happening faster than the 200ms timeout
                publishNext(observer, 100, "a");    // Should be skipped
                publishNext(observer, 200, "b");    // Should be skipped
                publishNext(observer, 300, "c");    // Should be skipped
                publishNext(observer, 400, "d");    // Should be skipped
                publishNext(observer, 500, "e");    // Should be skipped
                publishNext(observer, 600, "f");    // Should be skipped
                publishNext(observer, 700, "g");    // Should be skipped
                publishNext(observer, 800, "h");    // Should be skipped
                publishCompleted(observer, 900);     // Should be published as soon as the timeout expires.

                return Subscriptions.empty();
            }
        });

        Observable<String> sampled = Observable.create(OperationDebounce.debounce(source, 200, TimeUnit.MILLISECONDS, scheduler));
        sampled.subscribe(observer);

        scheduler.advanceTimeTo(0, TimeUnit.MILLISECONDS);
        InOrder inOrder = inOrder(observer);
        inOrder.verify(observer, times(0)).onNext(anyString());
        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, times(1)).onCompleted();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDebounceWithError() {
        Observable<String> source = Observable.create(new Observable.OnSubscribeFunc<String>() {
            @Override
            public Subscription onSubscribe(Observer<? super String> observer) {
                Exception error = new TestException();
                publishNext(observer, 100, "one");    // Should be published since "two" will arrive after the timeout expires.
                publishNext(observer, 600, "two");    // Should be skipped since onError will arrive before the timeout expires.
                publishError(observer, 700, error);   // Should be published as soon as the timeout expires.

                return Subscriptions.empty();
            }
        });

        Observable<String> sampled = Observable.create(OperationDebounce.debounce(source, 400, TimeUnit.MILLISECONDS, scheduler));
        sampled.subscribe(observer);

        scheduler.advanceTimeTo(0, TimeUnit.MILLISECONDS);
        InOrder inOrder = inOrder(observer);
        // 100 + 400 means it triggers at 500
        scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
        inOrder.verify(observer).onNext("one");
        scheduler.advanceTimeTo(701, TimeUnit.MILLISECONDS);
        inOrder.verify(observer).onError(any(TestException.class));
        inOrder.verifyNoMoreInteractions();
    }

    private <T> void publishCompleted(final Observer<T> observer, long delay) {
        scheduler.schedule(new Action0() {
            @Override
            public void call() {
                observer.onCompleted();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private <T> void publishError(final Observer<T> observer, long delay, final Exception error) {
        scheduler.schedule(new Action0() {
            @Override
            public void call() {
                observer.onError(error);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private <T> void publishNext(final Observer<T> observer, final long delay, final T value) {
        scheduler.schedule(new Action0() {
            @Override
            public void call() {
                observer.onNext(value);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("serial")
    private class TestException extends Exception {
    }
}

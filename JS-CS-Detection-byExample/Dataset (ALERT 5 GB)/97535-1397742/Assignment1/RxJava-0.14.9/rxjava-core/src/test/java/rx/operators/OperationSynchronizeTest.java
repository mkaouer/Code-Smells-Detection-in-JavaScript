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
import static rx.operators.OperationSynchronize.*;

import org.junit.Test;
import org.mockito.Mockito;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class OperationSynchronizeTest {

    /**
     * Ensure onCompleted can not be called after an Unsubscribe
     */
    @Test
    public void testOnCompletedAfterUnSubscribe() {
        TestObservable t = new TestObservable(null);
        Observable<String> st = Observable.create(synchronize(Observable.create(t)));

        @SuppressWarnings("unchecked")
        Observer<String> w = mock(Observer.class);
        Subscription ws = st.subscribe(w);

        t.sendOnNext("one");
        ws.unsubscribe();
        t.sendOnCompleted();

        verify(w, times(1)).onNext("one");
        verify(w, Mockito.never()).onCompleted();
    }

    /**
     * Ensure onNext can not be called after an Unsubscribe
     */
    @Test
    public void testOnNextAfterUnSubscribe() {
        TestObservable t = new TestObservable(null);
        Observable<String> st = Observable.create(synchronize(Observable.create(t)));

        @SuppressWarnings("unchecked")
        Observer<String> w = mock(Observer.class);
        Subscription ws = st.subscribe(w);

        t.sendOnNext("one");
        ws.unsubscribe();
        t.sendOnNext("two");

        verify(w, times(1)).onNext("one");
        verify(w, Mockito.never()).onNext("two");
    }

    /**
     * Ensure onError can not be called after an Unsubscribe
     */
    @Test
    public void testOnErrorAfterUnSubscribe() {
        TestObservable t = new TestObservable(null);
        Observable<String> st = Observable.create(synchronize(Observable.create(t)));

        @SuppressWarnings("unchecked")
        Observer<String> w = mock(Observer.class);
        Subscription ws = st.subscribe(w);

        t.sendOnNext("one");
        ws.unsubscribe();
        t.sendOnError(new RuntimeException("bad"));

        verify(w, times(1)).onNext("one");
        verify(w, Mockito.never()).onError(any(Throwable.class));
    }

    /**
     * Ensure onNext can not be called after onError
     */
    @Test
    public void testOnNextAfterOnError() {
        TestObservable t = new TestObservable(null);
        Observable<String> st = Observable.create(synchronize(Observable.create(t)));

        @SuppressWarnings("unchecked")
        Observer<String> w = mock(Observer.class);
        @SuppressWarnings("unused")
        Subscription ws = st.subscribe(w);

        t.sendOnNext("one");
        t.sendOnError(new RuntimeException("bad"));
        t.sendOnNext("two");

        verify(w, times(1)).onNext("one");
        verify(w, times(1)).onError(any(Throwable.class));
        verify(w, Mockito.never()).onNext("two");
    }

    /**
     * Ensure onCompleted can not be called after onError
     */
    @Test
    public void testOnCompletedAfterOnError() {
        TestObservable t = new TestObservable(null);
        Observable<String> st = Observable.create(synchronize(Observable.create(t)));

        @SuppressWarnings("unchecked")
        Observer<String> w = mock(Observer.class);
        @SuppressWarnings("unused")
        Subscription ws = st.subscribe(w);

        t.sendOnNext("one");
        t.sendOnError(new RuntimeException("bad"));
        t.sendOnCompleted();

        verify(w, times(1)).onNext("one");
        verify(w, times(1)).onError(any(Throwable.class));
        verify(w, Mockito.never()).onCompleted();
    }

    /**
     * Ensure onNext can not be called after onCompleted
     */
    @Test
    public void testOnNextAfterOnCompleted() {
        TestObservable t = new TestObservable(null);
        Observable<String> st = Observable.create(synchronize(Observable.create(t)));

        @SuppressWarnings("unchecked")
        Observer<String> w = mock(Observer.class);
        @SuppressWarnings("unused")
        Subscription ws = st.subscribe(w);

        t.sendOnNext("one");
        t.sendOnCompleted();
        t.sendOnNext("two");

        verify(w, times(1)).onNext("one");
        verify(w, Mockito.never()).onNext("two");
        verify(w, times(1)).onCompleted();
        verify(w, Mockito.never()).onError(any(Throwable.class));
    }

    /**
     * Ensure onError can not be called after onCompleted
     */
    @Test
    public void testOnErrorAfterOnCompleted() {
        TestObservable t = new TestObservable(null);
        Observable<String> st = Observable.create(synchronize(Observable.create(t)));

        @SuppressWarnings("unchecked")
        Observer<String> w = mock(Observer.class);
        @SuppressWarnings("unused")
        Subscription ws = st.subscribe(w);

        t.sendOnNext("one");
        t.sendOnCompleted();
        t.sendOnError(new RuntimeException("bad"));

        verify(w, times(1)).onNext("one");
        verify(w, times(1)).onCompleted();
        verify(w, Mockito.never()).onError(any(Throwable.class));
    }

    /**
     * A Observable that doesn't do the right thing on UnSubscribe/Error/etc in that it will keep sending events down the pipe regardless of what happens.
     */
    private static class TestObservable implements Observable.OnSubscribeFunc<String> {

        Observer<? super String> observer = null;

        public TestObservable(Subscription s) {
        }

        /* used to simulate subscription */
        public void sendOnCompleted() {
            observer.onCompleted();
        }

        /* used to simulate subscription */
        public void sendOnNext(String value) {
            observer.onNext(value);
        }

        /* used to simulate subscription */
        public void sendOnError(Throwable e) {
            observer.onError(e);
        }

        @Override
        public Subscription onSubscribe(final Observer<? super String> observer) {
            this.observer = observer;
            return new Subscription() {

                @Override
                public void unsubscribe() {
                    // going to do nothing to pretend I'm a bad Observable that keeps allowing events to be sent
                }

            };
        }

    }
}

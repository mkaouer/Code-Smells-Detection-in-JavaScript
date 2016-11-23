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
import static rx.operators.OperationDematerialize.*;

import org.junit.Test;

import rx.Notification;
import rx.Observable;
import rx.Observer;

public class OperationDematerializeTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testDematerialize1() {
        Observable<Notification<Integer>> notifications = Observable.from(1, 2).materialize();
        Observable<Integer> dematerialize = notifications.dematerialize();

        Observer<Integer> aObserver = mock(Observer.class);
        dematerialize.subscribe(aObserver);

        verify(aObserver, times(1)).onNext(1);
        verify(aObserver, times(1)).onNext(2);
        verify(aObserver, times(1)).onCompleted();
        verify(aObserver, never()).onError(any(Throwable.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDematerialize2() {
        Throwable exception = new Throwable("test");
        Observable<Integer> observable = Observable.error(exception);
        Observable<Integer> dematerialize = Observable.create(dematerialize(observable.materialize()));

        Observer<Integer> aObserver = mock(Observer.class);
        dematerialize.subscribe(aObserver);

        verify(aObserver, times(1)).onError(exception);
        verify(aObserver, times(0)).onCompleted();
        verify(aObserver, times(0)).onNext(any(Integer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDematerialize3() {
        Exception exception = new Exception("test");
        Observable<Integer> observable = Observable.error(exception);
        Observable<Integer> dematerialize = Observable.create(dematerialize(observable.materialize()));

        Observer<Integer> aObserver = mock(Observer.class);
        dematerialize.subscribe(aObserver);

        verify(aObserver, times(1)).onError(exception);
        verify(aObserver, times(0)).onCompleted();
        verify(aObserver, times(0)).onNext(any(Integer.class));
    }
}

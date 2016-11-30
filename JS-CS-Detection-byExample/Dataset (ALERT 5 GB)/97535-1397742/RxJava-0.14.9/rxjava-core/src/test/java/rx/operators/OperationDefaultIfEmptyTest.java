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

import static org.mockito.Mockito.*;
import static rx.operators.OperationDefaultIfEmpty.*;

import org.junit.Test;

import rx.Observable;
import rx.Observer;

public class OperationDefaultIfEmptyTest {

    @Test
    public void testDefaultIfEmpty() {
        Observable<Integer> source = Observable.from(1, 2, 3);
        Observable<Integer> observable = Observable.create(defaultIfEmpty(
                source, 10));

        @SuppressWarnings("unchecked")
        Observer<Integer> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, never()).onNext(10);
        verify(aObserver, times(1)).onNext(1);
        verify(aObserver, times(1)).onNext(2);
        verify(aObserver, times(1)).onNext(3);
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testDefaultIfEmptyWithEmpty() {
        Observable<Integer> source = Observable.empty();
        Observable<Integer> observable = Observable.create(defaultIfEmpty(
                source, 10));

        @SuppressWarnings("unchecked")
        Observer<Integer> aObserver = mock(Observer.class);
        observable.subscribe(aObserver);
        verify(aObserver, times(1)).onNext(10);
        verify(aObserver, never()).onError(
                org.mockito.Matchers.any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }
}

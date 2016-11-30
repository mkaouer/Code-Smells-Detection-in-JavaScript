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
import static rx.operators.OperationSkip.*;

import org.junit.Test;

import rx.Observable;
import rx.Observer;

public class OperationSkipTest {

    @Test
    public void testSkip1() {
        Observable<String> w = Observable.from("one", "two", "three");
        Observable<String> skip = Observable.create(skip(w, 2));

        @SuppressWarnings("unchecked")
        Observer<String> aObserver = mock(Observer.class);
        skip.subscribe(aObserver);
        verify(aObserver, never()).onNext("one");
        verify(aObserver, never()).onNext("two");
        verify(aObserver, times(1)).onNext("three");
        verify(aObserver, never()).onError(any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }

    @Test
    public void testSkip2() {
        Observable<String> w = Observable.from("one", "two", "three");
        Observable<String> skip = Observable.create(skip(w, 1));

        @SuppressWarnings("unchecked")
        Observer<String> aObserver = mock(Observer.class);
        skip.subscribe(aObserver);
        verify(aObserver, never()).onNext("one");
        verify(aObserver, times(1)).onNext("two");
        verify(aObserver, times(1)).onNext("three");
        verify(aObserver, never()).onError(any(Throwable.class));
        verify(aObserver, times(1)).onCompleted();
    }
}

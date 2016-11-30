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
import static rx.operators.OperationFinally.*;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.util.functions.Action0;

public class OperationFinallyTest {

    private Action0 aAction0;
    private Observer<String> aObserver;

    @SuppressWarnings("unchecked")
    // mocking has to be unchecked, unfortunately
    @Before
    public void before() {
        aAction0 = mock(Action0.class);
        aObserver = mock(Observer.class);
    }

    private void checkActionCalled(Observable<String> input) {
        Observable.create(finallyDo(input, aAction0)).subscribe(aObserver);
        verify(aAction0, times(1)).call();
    }

    @Test
    public void testFinallyCalledOnComplete() {
        checkActionCalled(Observable.from(new String[] { "1", "2", "3" }));
    }

    @Test
    public void testFinallyCalledOnError() {
        checkActionCalled(Observable.<String> error(new RuntimeException("expected")));
    }
}

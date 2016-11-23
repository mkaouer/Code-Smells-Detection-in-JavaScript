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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import rx.Observer;
import rx.Subscription;
import rx.functions.Func0;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class OperatorMulticastTest {

    @Test
    public void testMulticast() {
        Subject<String, String> source = PublishSubject.create();

        ConnectableObservable<String> multicasted = new OperatorMulticast<String, String>(source, new PublishSubjectFactory());

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        multicasted.subscribe(observer);

        source.onNext("one");
        source.onNext("two");

        multicasted.connect();

        source.onNext("three");
        source.onNext("four");
        source.onCompleted();

        verify(observer, never()).onNext("one");
        verify(observer, never()).onNext("two");
        verify(observer, times(1)).onNext("three");
        verify(observer, times(1)).onNext("four");
        verify(observer, times(1)).onCompleted();

    }

    @Test
    public void testMulticastConnectTwice() {
        Subject<String, String> source = PublishSubject.create();

        ConnectableObservable<String> multicasted = new OperatorMulticast<String, String>(source, new PublishSubjectFactory());

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        multicasted.subscribe(observer);

        source.onNext("one");

        Subscription sub = multicasted.connect();
        Subscription sub2 = multicasted.connect();

        source.onNext("two");
        source.onCompleted();

        verify(observer, never()).onNext("one");
        verify(observer, times(1)).onNext("two");
        verify(observer, times(1)).onCompleted();

        assertEquals(sub, sub2);

    }

    @Test
    public void testMulticastDisconnect() {
        Subject<String, String> source = PublishSubject.create();

        ConnectableObservable<String> multicasted = new OperatorMulticast<String, String>(source, new PublishSubjectFactory());

        @SuppressWarnings("unchecked")
        Observer<String> observer = mock(Observer.class);
        multicasted.subscribe(observer);

        source.onNext("one");

        Subscription connection = multicasted.connect();
        source.onNext("two");

        connection.unsubscribe();
        source.onNext("three");

        // subscribe again
        multicasted.subscribe(observer);
        // reconnect
        multicasted.connect();
        source.onNext("four");
        source.onCompleted();

        verify(observer, never()).onNext("one");
        verify(observer, times(1)).onNext("two");
        verify(observer, never()).onNext("three");
        verify(observer, times(1)).onNext("four");
        verify(observer, times(1)).onCompleted();

    }

    private static final class PublishSubjectFactory implements Func0<Subject<String, String>> {

        @Override
        public Subject<String, String> call() {
            return PublishSubject.<String> create();
        }

    }
}

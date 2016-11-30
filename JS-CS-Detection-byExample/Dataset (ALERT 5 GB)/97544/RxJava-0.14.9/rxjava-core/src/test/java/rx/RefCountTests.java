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
package rx;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import rx.concurrency.TestScheduler;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action0;
import rx.util.functions.Action1;

public class RefCountTests {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onlyFirstShouldSubscribeAndLastUnsubscribe() {
        final AtomicInteger subscriptionCount = new AtomicInteger();
        final AtomicInteger unsubscriptionCount = new AtomicInteger();
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribeFunc<Integer>() {
            @Override
            public Subscription onSubscribe(Observer<? super Integer> observer) {
                subscriptionCount.incrementAndGet();
                return Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        unsubscriptionCount.incrementAndGet();
                    }
                });
            }
        });
        Observable<Integer> refCounted = observable.publish().refCount();
        Observer<Integer> observer = mock(Observer.class);
        Subscription first = refCounted.subscribe(observer);
        assertEquals(1, subscriptionCount.get());
        Subscription second = refCounted.subscribe(observer);
        assertEquals(1, subscriptionCount.get());
        first.unsubscribe();
        assertEquals(0, unsubscriptionCount.get());
        second.unsubscribe();
        assertEquals(1, unsubscriptionCount.get());
    }

    @Test
    public void testRefCount() {
        TestScheduler s = new TestScheduler();
        Observable<Long> interval = Observable.interval(100, TimeUnit.MILLISECONDS, s).publish().refCount();

        // subscribe list1
        final List<Long> list1 = new ArrayList<Long>();
        Subscription s1 = interval.subscribe(new Action1<Long>() {

            @Override
            public void call(Long t1) {
                list1.add(t1);
            }

        });
        s.advanceTimeBy(200, TimeUnit.MILLISECONDS);

        assertEquals(2, list1.size());
        assertEquals(0L, list1.get(0).longValue());
        assertEquals(1L, list1.get(1).longValue());

        // subscribe list2
        final List<Long> list2 = new ArrayList<Long>();
        Subscription s2 = interval.subscribe(new Action1<Long>() {

            @Override
            public void call(Long t1) {
                list2.add(t1);
            }

        });
        s.advanceTimeBy(300, TimeUnit.MILLISECONDS);

        // list 1 should have 5 items
        assertEquals(5, list1.size());
        assertEquals(2L, list1.get(2).longValue());
        assertEquals(3L, list1.get(3).longValue());
        assertEquals(4L, list1.get(4).longValue());

        // list 2 should only have 3 items
        assertEquals(3, list2.size());
        assertEquals(2L, list2.get(0).longValue());
        assertEquals(3L, list2.get(1).longValue());
        assertEquals(4L, list2.get(2).longValue());

        // unsubscribe list1
        s1.unsubscribe();

        // advance further
        s.advanceTimeBy(300, TimeUnit.MILLISECONDS);

        // list 1 should still have 5 items
        assertEquals(5, list1.size());

        // list 2 should have 6 items
        assertEquals(6, list2.size());
        assertEquals(5L, list2.get(3).longValue());
        assertEquals(6L, list2.get(4).longValue());
        assertEquals(7L, list2.get(5).longValue());

        // unsubscribe list2
        s2.unsubscribe();

        // advance further
        s.advanceTimeBy(1000, TimeUnit.MILLISECONDS);

        // subscribing a new one should start over because the source should have been unsubscribed
        // subscribe list3
        final List<Long> list3 = new ArrayList<Long>();
        Subscription s3 = interval.subscribe(new Action1<Long>() {

            @Override
            public void call(Long t1) {
                list3.add(t1);
            }

        });
        s.advanceTimeBy(200, TimeUnit.MILLISECONDS);

        assertEquals(2, list3.size());
        assertEquals(0L, list3.get(0).longValue());
        assertEquals(1L, list3.get(1).longValue());

    }
}

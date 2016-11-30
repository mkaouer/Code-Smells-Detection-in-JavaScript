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
package rx.observables;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

public class BlockingObservableTest {

    @Mock
    Observer<Integer> w;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLast() {
        BlockingObservable<String> obs = BlockingObservable.from(Observable.from("one", "two", "three"));

        assertEquals("three", obs.last());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLastEmptyObservable() {
        BlockingObservable<Object> obs = BlockingObservable.from(Observable.empty());
        obs.last();
    }

    @Test
    public void testLastOrDefault() {
        BlockingObservable<Integer> observable = BlockingObservable.from(Observable.from(1, 0, -1));
        int last = observable.lastOrDefault(-100, new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer args) {
                return args >= 0;
            }
        });
        assertEquals(0, last);
    }

    @Test
    public void testLastOrDefault1() {
        BlockingObservable<String> observable = BlockingObservable.from(Observable.from("one", "two", "three"));
        assertEquals("three", observable.lastOrDefault("default"));
    }

    @Test
    public void testLastOrDefault2() {
        BlockingObservable<Object> observable = BlockingObservable.from(Observable.empty());
        assertEquals("default", observable.lastOrDefault("default"));
    }

    @Test
    public void testLastOrDefaultWithPredicate() {
        BlockingObservable<Integer> observable = BlockingObservable.from(Observable.from(1, 0, -1));
        int last = observable.lastOrDefault(0, new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer args) {
                return args < 0;
            }
        });

        assertEquals(-1, last);
    }

    @Test
    public void testLastOrDefaultWrongPredicate() {
        BlockingObservable<Integer> observable = BlockingObservable.from(Observable.from(-1, -2, -3));
        int last = observable.lastOrDefault(0, new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer args) {
                return args >= 0;
            }
        });
        assertEquals(0, last);
    }

    @Test
    public void testLastWithPredicate() {
        BlockingObservable<String> obs = BlockingObservable.from(Observable.from("one", "two", "three"));

        assertEquals("two", obs.last(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.length() == 3;
            }
        }));
    }

    public void testSingle() {
        BlockingObservable<String> observable = BlockingObservable.from(Observable.from("one"));
        assertEquals("one", observable.single());
    }

    @Test
    public void testSingleDefault() {
        BlockingObservable<Object> observable = BlockingObservable.from(Observable.empty());
        assertEquals("default", observable.singleOrDefault("default"));
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleDefaultPredicateMatchesMoreThanOne() {
        BlockingObservable.from(Observable.from("one", "two")).singleOrDefault("default", new Func1<String, Boolean>() {
            @Override
            public Boolean call(String args) {
                return args.length() == 3;
            }
        });
    }

    @Test
    public void testSingleDefaultPredicateMatchesNothing() {
        BlockingObservable<String> observable = BlockingObservable.from(Observable.from("one", "two"));
        String result = observable.singleOrDefault("default", new Func1<String, Boolean>() {
            @Override
            public Boolean call(String args) {
                return args.length() == 4;
            }
        });
        assertEquals("default", result);
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleDefaultWithMoreThanOne() {
        BlockingObservable<String> observable = BlockingObservable.from(Observable.from("one", "two", "three"));
        observable.singleOrDefault("default");
    }

    @Test
    public void testSingleWithPredicateDefault() {
        BlockingObservable<String> observable = BlockingObservable.from(Observable.from("one", "two", "four"));
        assertEquals("four", observable.single(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.length() == 4;
            }
        }));
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleWrong() {
        BlockingObservable<Integer> observable = BlockingObservable.from(Observable.from(1, 2));
        observable.single();
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleWrongPredicate() {
        BlockingObservable<Integer> observable = BlockingObservable.from(Observable.from(-1));
        observable.single(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer args) {
                return args > 0;
            }
        });
    }

    @Test
    public void testToIterable() {
        BlockingObservable<String> obs = BlockingObservable.from(Observable.from("one", "two", "three"));

        Iterator<String> it = obs.toIterable().iterator();

        assertEquals(true, it.hasNext());
        assertEquals("one", it.next());

        assertEquals(true, it.hasNext());
        assertEquals("two", it.next());

        assertEquals(true, it.hasNext());
        assertEquals("three", it.next());

        assertEquals(false, it.hasNext());

    }

    @Test(expected = TestException.class)
    public void testToIterableWithException() {
        BlockingObservable<String> obs = BlockingObservable.from(Observable.create(new Observable.OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(Observer<? super String> observer) {
                observer.onNext("one");
                observer.onError(new TestException());
                return Subscriptions.empty();
            }
        }));

        Iterator<String> it = obs.toIterable().iterator();

        assertEquals(true, it.hasNext());
        assertEquals("one", it.next());

        assertEquals(true, it.hasNext());
        it.next();

    }

    @Test
    public void testForEachWithError() {
        try {
            BlockingObservable.from(Observable.create(new Observable.OnSubscribeFunc<String>() {

                @Override
                public Subscription onSubscribe(final Observer<? super String> observer) {
                    final BooleanSubscription subscription = new BooleanSubscription();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            observer.onNext("one");
                            observer.onNext("two");
                            observer.onNext("three");
                            observer.onCompleted();
                        }
                    }).start();
                    return subscription;
                }
            })).forEach(new Action1<String>() {

                @Override
                public void call(String t1) {
                    throw new RuntimeException("fail");
                }
            });
            fail("we expect an exception to be thrown");
        } catch (Throwable e) {
            // do nothing as we expect this
        }
    }

    private static class TestException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}

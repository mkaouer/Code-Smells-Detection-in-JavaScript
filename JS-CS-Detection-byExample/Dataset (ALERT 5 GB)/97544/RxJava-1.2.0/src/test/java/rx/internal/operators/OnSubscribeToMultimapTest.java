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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Observable.OnSubscribe;
import rx.exceptions.TestException;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;

public class OnSubscribeToMultimapTest {
    @Mock
    Observer<Object> objectObserver;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    Func1<String, Integer> lengthFunc = new Func1<String, Integer>() {
        @Override
        public Integer call(String t1) {
            return t1.length();
        }
    };
    Func1<String, String> duplicate = new Func1<String, String>() {
        @Override
        public String call(String t1) {
            return t1 + t1;
        }
    };

    @Test
    public void testToMultimap() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd");

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc);

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("a", "b"));
        expected.put(2, Arrays.asList("cc", "dd"));

        mapped.subscribe(objectObserver);

        verify(objectObserver, never()).onError(any(Throwable.class));
        verify(objectObserver, times(1)).onNext(expected);
        verify(objectObserver, times(1)).onCompleted();
    }

    @Test
    public void testToMultimapWithValueSelector() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd");

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, duplicate);

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("aa", "bb"));
        expected.put(2, Arrays.asList("cccc", "dddd"));

        mapped.subscribe(objectObserver);

        verify(objectObserver, never()).onError(any(Throwable.class));
        verify(objectObserver, times(1)).onNext(expected);
        verify(objectObserver, times(1)).onCompleted();
    }

    @Test
    public void testToMultimapWithMapFactory() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd", "eee", "fff");

        Func0<Map<Integer, Collection<String>>> mapFactory = new Func0<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                return new LinkedHashMap<Integer, Collection<String>>() {
                    /** */
                    private static final long serialVersionUID = -2084477070717362859L;

                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Integer, Collection<String>> eldest) {
                        return size() > 2;
                    }
                };
            }
        };

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(
                lengthFunc, UtilityFunctions.<String>identity(),
                mapFactory, OnSubscribeToMultimapTest.<Integer, String>arrayListCollectionFactory());

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Arrays.asList("eee", "fff"));

        mapped.subscribe(objectObserver);

        verify(objectObserver, never()).onError(any(Throwable.class));
        verify(objectObserver, times(1)).onNext(expected);
        verify(objectObserver, times(1)).onCompleted();
    }

    private static final <K,V> Func1<K, Collection<V>> arrayListCollectionFactory() {
        return new Func1<K, Collection<V>>() {

            @Override
            public Collection<V> call(K k) {
                return new ArrayList<V>();
            }};
    }

    private static final <K, V> Func0<Map<K, Collection<V>>> multimapFactory() {
        return new Func0<Map<K, Collection<V>>>() {

            @Override
            public Map<K, Collection<V>> call() {
                return new HashMap<K, Collection<V>>();
            }
        };
    }

    @Test
    public void testToMultimapWithCollectionFactory() {
        Observable<String> source = Observable.just("cc", "dd", "eee", "eee");

        Func1<Integer, Collection<String>> collectionFactory = new Func1<Integer, Collection<String>>() {

            @Override
            public Collection<String> call(Integer t1) {
                if (t1 == 2) {
                    return new ArrayList<String>();
                } else {
                    return new HashSet<String>();
                }
            }
        };

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(
                lengthFunc, UtilityFunctions.<String>identity(),
                OnSubscribeToMultimapTest.<Integer, String>multimapFactory(), collectionFactory);

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, new HashSet<String>(Arrays.asList("eee")));

        mapped.subscribe(objectObserver);

        verify(objectObserver, never()).onError(any(Throwable.class));
        verify(objectObserver, times(1)).onNext(expected);
        verify(objectObserver, times(1)).onCompleted();
    }

    @Test
    public void testToMultimapWithError() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd");

        Func1<String, Integer> lengthFuncErr = new Func1<String, Integer>() {
            @Override
            public Integer call(String t1) {
                if ("b".equals(t1)) {
                    throw new RuntimeException("Forced Failure");
                }
                return t1.length();
            }
        };

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFuncErr);

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("a", "b"));
        expected.put(2, Arrays.asList("cc", "dd"));

        mapped.subscribe(objectObserver);

        verify(objectObserver, times(1)).onError(any(Throwable.class));
        verify(objectObserver, never()).onNext(expected);
        verify(objectObserver, never()).onCompleted();
    }

    @Test
    public void testToMultimapWithErrorInValueSelector() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd");

        Func1<String, String> duplicateErr = new Func1<String, String>() {
            @Override
            public String call(String t1) {
                if ("b".equals(t1)) {
                    throw new RuntimeException("Forced failure");
                }
                return t1 + t1;
            }
        };

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, duplicateErr);

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("aa", "bb"));
        expected.put(2, Arrays.asList("cccc", "dddd"));

        mapped.subscribe(objectObserver);

        verify(objectObserver, times(1)).onError(any(Throwable.class));
        verify(objectObserver, never()).onNext(expected);
        verify(objectObserver, never()).onCompleted();
    }

    @Test
    public void testToMultimapWithMapThrowingFactory() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd", "eee", "fff");

        Func0<Map<Integer, Collection<String>>> mapFactory = new Func0<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                throw new RuntimeException("Forced failure");
            }
        };

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, UtilityFunctions.<String>identity(), mapFactory);

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Arrays.asList("eee", "fff"));

        mapped.subscribe(objectObserver);

        verify(objectObserver, times(1)).onError(any(Throwable.class));
        verify(objectObserver, never()).onNext(expected);
        verify(objectObserver, never()).onCompleted();
    }

    @Test
    public void testToMultimapWithThrowingCollectionFactory() {
        Observable<String> source = Observable.just("cc", "cc", "eee", "eee");

        Func1<Integer, Collection<String>> collectionFactory = new Func1<Integer, Collection<String>>() {

            @Override
            public Collection<String> call(Integer t1) {
                if (t1 == 2) {
                    throw new RuntimeException("Forced failure");
                } else {
                    return new HashSet<String>();
                }
            }
        };

        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, UtilityFunctions.<String>identity(), OnSubscribeToMultimapTest.<Integer, String>multimapFactory(), collectionFactory);

        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Collections.singleton("eee"));

        mapped.subscribe(objectObserver);

        verify(objectObserver, times(1)).onError(any(Throwable.class));
        verify(objectObserver, never()).onNext(expected);
        verify(objectObserver, never()).onCompleted();
    }

    @Test
    public void testKeySelectorThrows() {
        TestSubscriber<Object> ts = TestSubscriber.create();

        Observable.just(1, 2).toMultimap(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer v) {
                throw new TestException();
            }
        }).subscribe(ts);

        ts.assertError(TestException.class);
        ts.assertNoValues();
        ts.assertNotCompleted();
    }

    @Test
    public void testValueSelectorThrows() {
        TestSubscriber<Object> ts = TestSubscriber.create();

        Observable.just(1, 2).toMultimap(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer v) {
                return v;
            }
        }, new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer v) {
                throw new TestException();
            }
        }).subscribe(ts);

        ts.assertError(TestException.class);
        ts.assertNoValues();
        ts.assertNotCompleted();
    }

    @Test
    public void testMapFactoryThrows() {
        TestSubscriber<Object> ts = TestSubscriber.create();

        Observable.just(1, 2).toMultimap(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer v) {
                return v;
            }
        }, new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer v) {
                return v;
            }
        }, new Func0<Map<Integer, Collection<Integer>>>() {
            @Override
            public Map<Integer, Collection<Integer>> call() {
                throw new TestException();
            }
        }).subscribe(ts);

        ts.assertError(TestException.class);
        ts.assertNoValues();
        ts.assertNotCompleted();
    }

    @Test
    public void testCollectionFactoryThrows() {
        TestSubscriber<Object> ts = TestSubscriber.create();

        Observable.just(1, 2).toMultimap(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer v) {
                return v;
            }
        }, new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer v) {
                return v;
            }
        }, new Func0<Map<Integer, Collection<Integer>>>() {
            @Override
            public Map<Integer, Collection<Integer>> call() {
                return new HashMap<Integer, Collection<Integer>>();
            }
        }, new Func1<Integer, Collection<Integer>>() {
            @Override
            public Collection<Integer> call(Integer k) {
                throw new TestException();
            }
        }).subscribe(ts);

        ts.assertError(TestException.class);
        ts.assertNoValues();
        ts.assertNotCompleted();
    }

    @Test
    public void testKeySelectorFailureDoesNotAllowErrorAndCompletedEmissions() {
        TestSubscriber<Map<Integer, Collection<Integer>>> ts = TestSubscriber.create(0);
        final RuntimeException e = new RuntimeException();
        Observable.create(new OnSubscribe<Integer>() {

            @Override
            public void call(final Subscriber<? super Integer> sub) {
                sub.setProducer(new Producer() {

                    @Override
                    public void request(long n) {
                        if (n > 1) {
                            sub.onNext(1);
                            sub.onCompleted();
                        }
                    }
                });
            }
        }).toMultimap(new Func1<Integer,Integer>() {

            @Override
            public Integer call(Integer t) {
                throw e;
            }
        }).unsafeSubscribe(ts);
        ts.assertNoValues();
        ts.assertError(e);
        ts.assertNotCompleted();
    }

    @Test
    public void testKeySelectorFailureDoesNotAllowTwoErrorEmissions() {
        try {
            final List<Throwable> list = new CopyOnWriteArrayList<Throwable>();
            RxJavaHooks.setOnError(new Action1<Throwable>() {

                @Override
                public void call(Throwable t) {
                    list.add(t);
                }
            });
            TestSubscriber<Map<Integer, Collection<Integer>>> ts = TestSubscriber.create(0);
            final RuntimeException e1 = new RuntimeException();
            final RuntimeException e2 = new RuntimeException();
            Observable.create(new OnSubscribe<Integer>() {

                @Override
                public void call(final Subscriber<? super Integer> sub) {
                    sub.setProducer(new Producer() {

                        @Override
                        public void request(long n) {
                            if (n > 1) {
                                sub.onNext(1);
                                sub.onError(e2);
                            }
                        }
                    });
                }
            }).toMultimap(new Func1<Integer, Integer>() {

                @Override
                public Integer call(Integer t) {
                    throw e1;
                }
            }).unsafeSubscribe(ts);
            ts.assertNoValues();
            assertEquals(Arrays.asList(e1), ts.getOnErrorEvents());
            assertEquals(Arrays.asList(e2), list);
            ts.assertNotCompleted();
        } finally {
            RxJavaHooks.reset();
        }
    }

    @Test
    public void testFactoryFailureDoesNotAllowErrorThenOnNextEmissions() {
        TestSubscriber<Map<Integer, Collection<Integer>>> ts = TestSubscriber.create(0);
        final RuntimeException e = new RuntimeException();
        Observable.create(new OnSubscribe<Integer>() {

            @Override
            public void call(final Subscriber<? super Integer> sub) {
                sub.setProducer(new Producer() {

                    @Override
                    public void request(long n) {
                        if (n > 1) {
                            sub.onNext(1);
                            sub.onNext(2);
                        }
                    }
                });
            }
        }).toMultimap(new Func1<Integer,Integer>() {

            @Override
            public Integer call(Integer t) {
                throw e;
            }
        }).unsafeSubscribe(ts);
        ts.assertNoValues();
        ts.assertError(e);
        ts.assertNotCompleted();
    }

    @Test
    public void testBackpressure() {
        TestSubscriber<Object> ts = TestSubscriber.create(0);
        Observable
            .just("a", "bb", "ccc", "dddd")
            .toMultimap(lengthFunc)
            .subscribe(ts);
        ts.assertNoErrors();
        ts.assertNotCompleted();
        ts.assertNoValues();
        ts.requestMore(1);
        ts.assertValueCount(1);
        ts.assertNoErrors();
        ts.assertCompleted();
    }
}

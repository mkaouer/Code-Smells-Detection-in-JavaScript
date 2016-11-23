/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rx.internal.operators;

import java.util.*;

import rx.*;
import rx.Observable.Operator;
import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Creates windows of values into the source sequence with skip frequency and size bounds.
 * 
 * If skip == size then the windows are non-overlapping, otherwise, windows may overlap
 * or can be discontinuous. The returned Observable sequence is cold and need to be
 * consumed while the window operation is in progress.
 * 
 * <p>Note that this conforms the Rx.NET behavior, but does not match former RxJava
 * behavior, which operated as a regular buffer and mapped its lists to Observables.</p>
 * 
 * @param <T> the value type
 */
public final class OperatorWindowWithSize<T> implements Operator<Observable<T>, T> {
    final int size;
    final int skip;

    public OperatorWindowWithSize(int size, int skip) {
        this.size = size;
        this.skip = skip;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super Observable<T>> child) {
        if (skip == size) {
            ExactSubscriber e = new ExactSubscriber(child);
            e.init();
            return e;
        }
        InexactSubscriber ie = new InexactSubscriber(child);
        ie.init();
        return ie;
    }
    /** Subscriber with exact, non-overlapping window bounds. */
    final class ExactSubscriber extends Subscriber<T> {
        final Subscriber<? super Observable<T>> child;
        int count;
        UnicastSubject<T> window;
        volatile boolean noWindow = true;
        public ExactSubscriber(Subscriber<? super Observable<T>> child) {
            /**
             * See https://github.com/ReactiveX/RxJava/issues/1546
             * We cannot compose through a Subscription because unsubscribing
             * applies to the outer, not the inner.
             */
            this.child = child;
            /*
             * Add unsubscribe hook to child to get unsubscribe on outer (unsubscribing on next window, not on the inner window itself)
             */
        }
        void init() {
            child.add(Subscriptions.create(new Action0() {

                @Override
                public void call() {
                    // if no window we unsubscribe up otherwise wait until window ends
                    if (noWindow) {
                        unsubscribe();
                    }
                }
                
            }));
            child.setProducer(new Producer() {
                @Override
                public void request(long n) {
                    if (n > 0) {
                        long u = n * size;
                        if (((u >>> 31) != 0) && (u / n != size)) {
                            u = Long.MAX_VALUE;
                        }
                        requestMore(u);
                    }
                }
            });
        }
        
        void requestMore(long n) {
            request(n);
        }

        @Override
        public void onNext(T t) {
            if (window == null) {
                noWindow = false;
                window = UnicastSubject.create();
                child.onNext(window);
            }
            window.onNext(t);
            if (++count % size == 0) {
                window.onCompleted();
                window = null;
                noWindow = true;
                if (child.isUnsubscribed()) {
                    unsubscribe();
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (window != null) {
                window.onError(e);
            }
            child.onError(e);
        }

        @Override
        public void onCompleted() {
            if (window != null) {
                window.onCompleted();
            }
            child.onCompleted();
        }
    }

    /** Subscriber with inexact, possibly overlapping or skipping windows. */
    final class InexactSubscriber extends Subscriber<T> {
        final Subscriber<? super Observable<T>> child;
        int count;
        final List<CountedSubject<T>> chunks = new LinkedList<CountedSubject<T>>();
        volatile boolean noWindow = true;

        public InexactSubscriber(Subscriber<? super Observable<T>> child) {
            /**
             * See https://github.com/ReactiveX/RxJava/issues/1546
             * We cannot compose through a Subscription because unsubscribing
             * applies to the outer, not the inner.
             */
            this.child = child;
        }

        void init() {
            /*
             * Add unsubscribe hook to child to get unsubscribe on outer (unsubscribing on next window, not on the inner window itself)
             */
            child.add(Subscriptions.create(new Action0() {

                @Override
                public void call() {
                    // if no window we unsubscribe up otherwise wait until window ends
                    if (noWindow) {
                        unsubscribe();
                    }
                }

            }));
            
            child.setProducer(new Producer() {
                @Override
                public void request(long n) {
                    if (n > 0) {
                        long u = n * size;
                        if (((u >>> 31) != 0) && (u / n != size)) {
                            u = Long.MAX_VALUE;
                        }
                        requestMore(u);
                    }
                }
            });
        }
        
        void requestMore(long n) {
            request(n);
        }

        @Override
        public void onNext(T t) {
            if (count++ % skip == 0) {
                if (!child.isUnsubscribed()) {
                    if (chunks.isEmpty()) {
                        noWindow = false;
                    }
                    CountedSubject<T> cs = createCountedSubject();
                    chunks.add(cs);
                    child.onNext(cs.producer);
                }
            }

            Iterator<CountedSubject<T>> it = chunks.iterator();
            while (it.hasNext()) {
                CountedSubject<T> cs = it.next();
                cs.consumer.onNext(t);
                if (++cs.count == size) {
                    it.remove();
                    cs.consumer.onCompleted();
                }
            }
            if (chunks.isEmpty()) {
                noWindow = true;
                if (child.isUnsubscribed()) {
                    unsubscribe();
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            List<CountedSubject<T>> list = new ArrayList<CountedSubject<T>>(chunks);
            chunks.clear();
            noWindow = true;
            for (CountedSubject<T> cs : list) {
                cs.consumer.onError(e);
            }
            child.onError(e);
        }

        @Override
        public void onCompleted() {
            List<CountedSubject<T>> list = new ArrayList<CountedSubject<T>>(chunks);
            chunks.clear();
            noWindow = true;
            for (CountedSubject<T> cs : list) {
                cs.consumer.onCompleted();
            }
            child.onCompleted();
        }

        CountedSubject<T> createCountedSubject() {
            final UnicastSubject<T> bus = UnicastSubject.create();
            return new CountedSubject<T>(bus, bus);
        }
    }
    /** 
     * Record to store the subject and the emission count. 
     * @param <T> the subject's in-out type
     */
    static final class CountedSubject<T> {
        final Observer<T> consumer;
        final Observable<T> producer;
        int count;

        public CountedSubject(Observer<T> consumer, Observable<T> producer) {
            this.consumer = consumer;
            this.producer = producer;
        }
    }
}

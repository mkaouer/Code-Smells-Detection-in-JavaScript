/**
 * Copyright 2013 Netflix, Inc.
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
package rx.operators;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Observable.OnSubscribeFunc;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func2;
import rx.util.functions.Func3;
import rx.util.functions.Func4;
import rx.util.functions.Func5;
import rx.util.functions.Func6;
import rx.util.functions.Func7;
import rx.util.functions.Func8;
import rx.util.functions.Func9;
import rx.util.functions.FuncN;
import rx.util.functions.Functions;

/**
 * Returns an Observable that emits the results of a function applied to sets of items emitted, in
 * sequence, by two or more other Observables.
 * <p>
 * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/zip.png">
 * <p>
 * The zip operation applies this function in strict sequence, so the first item emitted by the new
 * Observable will be the result of the function applied to the first item emitted by each zipped
 * Observable; the second item emitted by the new Observable will be the result of the function
 * applied to the second item emitted by each zipped Observable; and so forth.
 * <p>
 * The resulting Observable returned from zip will invoke <code>onNext</code> as many times as the
 * number of <code>onNext</code> invocations of the source Observable that emits the fewest items.
 */
public final class OperationZip {

    public static <T1, T2, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Func2<? super T1, ? super T2, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        return a;
    }

    public static <T1, T2, T3, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Observable<? extends T3> o3, Func3<? super T1, ? super T2, ? super T3, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        a.addObserver(new ZipObserver<R, T3>(a, o3));
        return a;
    }

    public static <T1, T2, T3, T4, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Observable<? extends T3> o3, Observable<? extends T4> o4, Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        a.addObserver(new ZipObserver<R, T3>(a, o3));
        a.addObserver(new ZipObserver<R, T4>(a, o4));
        return a;
    }

    public static <T1, T2, T3, T4, T5, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Observable<? extends T3> o3, Observable<? extends T4> o4, Observable<? extends T5> o5, Func5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        a.addObserver(new ZipObserver<R, T3>(a, o3));
        a.addObserver(new ZipObserver<R, T4>(a, o4));
        a.addObserver(new ZipObserver<R, T5>(a, o5));
        return a;
    }

    public static <T1, T2, T3, T4, T5, T6, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Observable<? extends T3> o3, Observable<? extends T4> o4, Observable<? extends T5> o5, Observable<? extends T6> o6,
            Func6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        a.addObserver(new ZipObserver<R, T3>(a, o3));
        a.addObserver(new ZipObserver<R, T4>(a, o4));
        a.addObserver(new ZipObserver<R, T5>(a, o5));
        a.addObserver(new ZipObserver<R, T6>(a, o6));
        return a;
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Observable<? extends T3> o3, Observable<? extends T4> o4, Observable<? extends T5> o5, Observable<? extends T6> o6, Observable<? extends T7> o7,
            Func7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        a.addObserver(new ZipObserver<R, T3>(a, o3));
        a.addObserver(new ZipObserver<R, T4>(a, o4));
        a.addObserver(new ZipObserver<R, T5>(a, o5));
        a.addObserver(new ZipObserver<R, T6>(a, o6));
        a.addObserver(new ZipObserver<R, T7>(a, o7));
        return a;
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Observable<? extends T3> o3, Observable<? extends T4> o4, Observable<? extends T5> o5, Observable<? extends T6> o6, Observable<? extends T7> o7, Observable<? extends T8> o8,
            Func8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        a.addObserver(new ZipObserver<R, T3>(a, o3));
        a.addObserver(new ZipObserver<R, T4>(a, o4));
        a.addObserver(new ZipObserver<R, T5>(a, o5));
        a.addObserver(new ZipObserver<R, T6>(a, o6));
        a.addObserver(new ZipObserver<R, T7>(a, o7));
        a.addObserver(new ZipObserver<R, T8>(a, o8));
        return a;
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> OnSubscribeFunc<R> zip(Observable<? extends T1> o1, Observable<? extends T2> o2, Observable<? extends T3> o3, Observable<? extends T4> o4, Observable<? extends T5> o5, Observable<? extends T6> o6, Observable<? extends T7> o7, Observable<? extends T8> o8,
            Observable<? extends T9> o9, Func9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(Functions.fromFunc(zipFunction));
        a.addObserver(new ZipObserver<R, T1>(a, o1));
        a.addObserver(new ZipObserver<R, T2>(a, o2));
        a.addObserver(new ZipObserver<R, T3>(a, o3));
        a.addObserver(new ZipObserver<R, T4>(a, o4));
        a.addObserver(new ZipObserver<R, T5>(a, o5));
        a.addObserver(new ZipObserver<R, T6>(a, o6));
        a.addObserver(new ZipObserver<R, T7>(a, o7));
        a.addObserver(new ZipObserver<R, T8>(a, o8));
        a.addObserver(new ZipObserver<R, T9>(a, o9));
        return a;
    }

    public static <R> OnSubscribeFunc<R> zip(Iterable<? extends Observable<?>> ws, FuncN<? extends R> zipFunction) {
        Aggregator<R> a = new Aggregator<R>(zipFunction);
        for (Observable<?> w : ws) {
            ZipObserver<R, Object> zipObserver = new ZipObserver<R, Object>(a, w);
            a.addObserver(zipObserver);
        }
        return a;
    }

    /*
     * ThreadSafe
     */
    /* package accessible for unit tests */static class ZipObserver<R, T> implements Observer<T> {
        final Observable<? extends T> w;
        final Aggregator<R> a;
        private final SafeObservableSubscription subscription = new SafeObservableSubscription();
        private final AtomicBoolean subscribed = new AtomicBoolean(false);

        public ZipObserver(Aggregator<R> a, Observable<? extends T> w) {
            this.a = a;
            this.w = w;
        }

        public void startWatching() {
            if (subscribed.compareAndSet(false, true)) {
                // only subscribe once even if called more than once
                subscription.wrap(w.subscribe(this));
            }
        }

        @Override
        public void onCompleted() {
            a.complete(this);
        }

        @Override
        public void onError(Throwable e) {
            a.error(this, e);
        }

        @Override
        public void onNext(T args) {
            try {
                a.next(this, args);
            } catch (Throwable e) {
                onError(e);
            }
        }
    }

    /**
     * Receive notifications from each of the Observables we are reducing and execute the zipFunction whenever we have received events from all Observables.
     * 
     * This class is thread-safe.
     * 
     * @param <T>
     */
    /* package accessible for unit tests */static class Aggregator<T> implements OnSubscribeFunc<T> {

        private volatile SynchronizedObserver<T> observer;
        private final FuncN<? extends T> zipFunction;
        private final AtomicBoolean started = new AtomicBoolean(false);
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final ConcurrentHashMap<ZipObserver<T, ?>, Boolean> completed = new ConcurrentHashMap<ZipObserver<T, ?>, Boolean>();

        /* we use ConcurrentHashMap despite synchronization of methods because stop() does NOT use synchronization and this map is used by it and can be called by other threads */
        private ConcurrentHashMap<ZipObserver<T, ?>, ConcurrentLinkedQueue<Object>> receivedValuesPerObserver = new ConcurrentHashMap<ZipObserver<T, ?>, ConcurrentLinkedQueue<Object>>();
        /* we use a ConcurrentLinkedQueue to retain ordering (I'd like to just use a ConcurrentLinkedHashMap for 'receivedValuesPerObserver' but that doesn't exist in standard java */
        private ConcurrentLinkedQueue<ZipObserver<T, ?>> observers = new ConcurrentLinkedQueue<ZipObserver<T, ?>>();

        public Aggregator(FuncN<? extends T> zipFunction) {
            this.zipFunction = zipFunction;
        }

        /**
         * Receive notification of a Observer starting (meaning we should require it for aggregation)
         * 
         * Thread Safety => Invoke ONLY from the static factory methods at top of this class which are always an atomic execution by a single thread.
         * 
         * @param w
         */
        void addObserver(ZipObserver<T, ?> w) {
            // initialize this ZipObserver
            observers.add(w);
            receivedValuesPerObserver.put(w, new ConcurrentLinkedQueue<Object>());
        }

        /**
         * Receive notification of a Observer completing its iterations.
         * 
         * @param w
         */
        void complete(ZipObserver<T, ?> w) {
            // store that this ZipObserver is completed
            completed.put(w, Boolean.TRUE);
            // if all ZipObservers are completed, we mark the whole thing as completed
            if (completed.size() == observers.size()) {
                if (running.compareAndSet(true, false)) {
                    // this thread succeeded in setting running=false so let's propagate the completion
                    // mark ourselves as done
                    observer.onCompleted();
                }
            }
        }

        /**
         * Receive error for a Observer. Throw the error up the chain and stop processing.
         * 
         * @param w
         */
        void error(ZipObserver<T, ?> w, Throwable e) {
            if (running.compareAndSet(true, false)) {
                // this thread succeeded in setting running=false so let's propagate the error
                observer.onError(e);
                /* since we receive an error we want to tell everyone to stop */
                stop();
            }
        }

        /**
         * Receive the next value from a Observer.
         * <p>
         * If we have received values from all Observers, trigger the zip function, otherwise store the value and keep waiting.
         * 
         * @param w
         * @param arg
         */
        void next(ZipObserver<T, ?> w, Object arg) {
            if (observer == null) {
                throw new RuntimeException("This shouldn't be running if a Observer isn't registered");
            }

            /* if we've been 'unsubscribed' don't process anything further even if the things we're watching keep sending (likely because they are not responding to the unsubscribe call) */
            if (!running.get()) {
                return;
            }

            // store the value we received and below we'll decide if we are to send it to the Observer
            receivedValuesPerObserver.get(w).add(arg);

            // define here so the variable is out of the synchronized scope
            Object[] argsToZip = new Object[observers.size()];

            /* we have to synchronize here despite using concurrent data structures because the compound logic here must all be done atomically */
            synchronized (this) {
                // if all ZipObservers in 'receivedValues' map have a value, invoke the zipFunction
                for (ZipObserver<T, ?> rw : receivedValuesPerObserver.keySet()) {
                    if (receivedValuesPerObserver.get(rw).peek() == null) {
                        // we have a null meaning the queues aren't all populated so won't do anything
                        return;
                    }
                }
                // if we get to here this means all the queues have data
                int i = 0;
                for (ZipObserver<T, ?> rw : observers) {
                    argsToZip[i++] = receivedValuesPerObserver.get(rw).remove();
                }
            }
            // if we did not return above from the synchronized block we can now invoke the zipFunction with all of the args
            // we do this outside the synchronized block as it is now safe to call this concurrently and don't need to block other threads from calling
            // this 'next' method while another thread finishes calling this zipFunction
            observer.onNext(zipFunction.call(argsToZip));
        }

        @Override
        public Subscription onSubscribe(Observer<? super T> observer) {
            if (started.compareAndSet(false, true)) {
                SafeObservableSubscription subscription = new SafeObservableSubscription();
                this.observer = new SynchronizedObserver<T>(observer, subscription);
                /* start the Observers */
                for (ZipObserver<T, ?> rw : observers) {
                    rw.startWatching();
                }

                return subscription.wrap(new Subscription() {

                    @Override
                    public void unsubscribe() {
                        stop();
                    }

                });
            } else {
                /* a Observer already has subscribed so blow up */
                throw new IllegalStateException("Only one Observer can subscribe to this Observable.");
            }
        }

        /*
         * Do NOT synchronize this because it gets called via unsubscribe which can occur on other threads
         * and result in deadlocks. (http://jira/browse/API-4060)
         * 
         * AtomicObservableSubscription uses compareAndSet instead of locking to avoid deadlocks but ensure single-execution.
         * 
         * We do the same in the implementation of this method.
         * 
         * ThreadSafety of this method is provided by:
         * - AtomicBoolean[running].compareAndSet
         * - ConcurrentLinkedQueue[Observers]
         * - ZipObserver.subscription being an AtomicObservableSubscription
         */
        private void stop() {
            /* tell ourselves to stop processing onNext events by setting running=false */
            if (running.compareAndSet(true, false)) {
                /* propogate to all Observers to unsubscribe if this thread succeeded in setting running=false */
                for (ZipObserver<T, ?> o : observers) {
                    if (o.subscription != null) {
                        o.subscription.unsubscribe();
                    }
                }
            }
        }

    }
}

/**
 * Copyright 2016 Netflix, Inc.
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
package rx.plugins;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Abstract ExecutionHook with invocations at different lifecycle points of {@link Single} execution with a
 * default no-op implementation.
 * <p>
 * See {@link RxJavaPlugins} or the RxJava GitHub Wiki for information on configuring plugins:
 * <a href="https://github.com/ReactiveX/RxJava/wiki/Plugins">https://github.com/ReactiveX/RxJava/wiki/Plugins</a>.
 * <p>
 * <b>Note on thread-safety and performance:</b>
 * <p>
 * A single implementation of this class will be used globally so methods on this class will be invoked
 * concurrently from multiple threads so all functionality must be thread-safe.
 * <p>
 * Methods are also invoked synchronously and will add to execution time of the single so all behavior
 * should be fast. If anything time-consuming is to be done it should be spawned asynchronously onto separate
 * worker threads.
 *
 */
public abstract class RxJavaSingleExecutionHook { // NOPMD
    /**
     * Invoked during the construction by {@link Single#create(Single.OnSubscribe)}
     * <p>
     * This can be used to decorate or replace the <code>onSubscribe</code> function or just perform extra
     * logging, metrics and other such things and pass through the function.
     *
     * @param <T> the value type emitted by Single
     * @param f
     *            original {@link rx.Single.OnSubscribe}<{@code T}> to be executed
     * @return {@link rx.Single.OnSubscribe}<{@code T}> function that can be modified, decorated, replaced or just
     *         returned as a pass through
     */
    @Deprecated
    public <T> Single.OnSubscribe<T> onCreate(Single.OnSubscribe<T> f) {
        return f;
    }

    /**
     * Invoked before {@link Single#subscribe(Subscriber)} is about to be executed.
     * <p>
     * This can be used to decorate or replace the <code>onSubscribe</code> function or just perform extra
     * logging, metrics and other such things and pass through the function.
     *
     * @param <T> the value type emitted
     * @param singleInstance the parent single instance
     * @param onSubscribe
     *            original {@link rx.Observable.OnSubscribe}<{@code T}> to be executed
     * @return {@link rx.Observable.OnSubscribe}<{@code T}> function that can be modified, decorated, replaced or just
     *         returned as a pass through
     */
    @Deprecated
    public <T> Observable.OnSubscribe<T> onSubscribeStart(Single<? extends T> singleInstance, final Observable.OnSubscribe<T> onSubscribe) {
        // pass through by default
        return onSubscribe;
    }

    /**
     * Invoked after successful execution of {@link Single#subscribe(Subscriber)} with returned
     * {@link Subscription}.
     * <p>
     * This can be used to decorate or replace the {@link Subscription} instance or just perform extra logging,
     * metrics and other such things and pass through the subscription.
     *
     * @param <T> the value type emitted by Single
     * @param subscription
     *            original {@link Subscription}
     * @return {@link Subscription} subscription that can be modified, decorated, replaced or just returned as a
     *         pass through
     */
    @Deprecated
    public <T> Subscription onSubscribeReturn(Subscription subscription) {
        // pass through by default
        return subscription;
    }

    /**
     * Invoked after failed execution of {@link Single#subscribe(Subscriber)} with thrown Throwable.
     * <p>
     * This is <em>not</em> errors emitted via {@link Subscriber#onError(Throwable)} but exceptions thrown when
     * attempting to subscribe to a {@link Func1}<{@link Subscriber}{@code <T>}, {@link Subscription}>.
     *
     * @param <T> the value type emitted by Single
     * @param e
     *            Throwable thrown by {@link Single#subscribe(Subscriber)}
     * @return Throwable that can be decorated, replaced or just returned as a pass through
     */
    @Deprecated
    public <T> Throwable onSubscribeError(Throwable e) {
        // pass through by default
        return e;
    }

    /**
     * Invoked just as the operator functions is called to bind two operations together into a new
     * {@link Single} and the return value is used as the lifted function
     * <p>
     * This can be used to decorate or replace the {@link rx.Observable.Operator} instance or just perform extra
     * logging, metrics and other such things and pass through the onSubscribe.
     *
     * @param <T> the upstream value type (input)
     * @param <R> the downstream value type (output)
     * @param lift
     *            original {@link rx.Observable.Operator}{@code <R, T>}
     * @return {@link rx.Observable.Operator}{@code <R, T>} function that can be modified, decorated, replaced or just
     *         returned as a pass through
     */
    @Deprecated
    public <T, R> Observable.Operator<? extends R, ? super T> onLift(final Observable.Operator<? extends R, ? super T> lift) {
        return lift;
    }
}

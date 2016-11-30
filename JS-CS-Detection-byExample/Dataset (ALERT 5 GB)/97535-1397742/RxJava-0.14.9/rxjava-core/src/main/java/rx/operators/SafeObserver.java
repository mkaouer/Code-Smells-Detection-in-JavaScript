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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observer;
import rx.plugins.RxJavaPlugins;
import rx.util.CompositeException;
import rx.util.OnErrorNotImplementedException;

/**
 * Wrapper around Observer to ensure compliance with Rx contract.
 * <p>
 * The following is taken from the Rx Design Guidelines document: http://go.microsoft.com/fwlink/?LinkID=205219
 * <pre>
 * Messages sent to instances of the IObserver interface follow the following grammar:
 * 
 * OnNext* (OnCompleted | OnError)?
 * 
 * This grammar allows observable sequences to send any amount (0 or more) of OnNext messages to the subscribed
 * observer instance, optionally followed by a single success (OnCompleted) or failure (OnError) message.
 * 
 * The single message indicating that an observable sequence has finished ensures that consumers of the observable
 * sequence can deterministically establish that it is safe to perform cleanup operations.
 * 
 * A single failure further ensures that abort semantics can be maintained for operators that work on
 * multiple observable sequences (see paragraph 6.6).
 * </pre>
 * 
 * <p>
 * This wrapper will do the following:
 * <ul>
 * <li>Allow only single execution of either onError or onCompleted.</li>
 * <li>Once an onComplete or onError are performed, no further calls can be executed</li>
 * <li>If unsubscribe is called, this means we call completed() and don't allow any further onNext calls.</li>
 * <li>When onError or onComplete occur it will unsubscribe from the Observable (if executing asynchronously).</li>
 * </ul>
 * <p>
 * It will not synchronize onNext execution. Use the {@link SynchronizedObserver} to do that.
 * 
 * @param <T>
 */
public class SafeObserver<T> implements Observer<T> {

    private final Observer<? super T> actual;
    private final AtomicBoolean isFinished = new AtomicBoolean(false);
    private final SafeObservableSubscription subscription;

    public SafeObserver(SafeObservableSubscription subscription, Observer<? super T> actual) {
        this.subscription = subscription;
        this.actual = actual;
    }

    @Override
    public void onCompleted() {
        if (isFinished.compareAndSet(false, true)) {
            try {
                actual.onCompleted();
            } catch (Throwable e) {
                // handle errors if the onCompleted implementation fails, not just if the Observable fails
                onError(e);
            }
            // auto-unsubscribe
            subscription.unsubscribe();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (isFinished.compareAndSet(false, true)) {
            try {
                actual.onError(e);
            } catch (Throwable e2) {
                if (e2 instanceof OnErrorNotImplementedException) {
                    /**
                     * onError isn't implemented so throw
                     * 
                     * https://github.com/Netflix/RxJava/issues/198
                     * 
                     * Rx Design Guidelines 5.2
                     * 
                     * "when calling the Subscribe method that only has an onNext argument, the OnError behavior will be
                     * to rethrow the exception on the thread that the message comes out from the observable sequence.
                     * The OnCompleted behavior in this case is to do nothing."
                     */
                    throw (OnErrorNotImplementedException) e2;
                } else {
                    // if the onError itself fails then pass to the plugin
                    // see https://github.com/Netflix/RxJava/issues/216 for further discussion
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e2);
                    // and throw exception despite that not being proper for Rx
                    // https://github.com/Netflix/RxJava/issues/198
                    throw new RuntimeException("Error occurred when trying to propagate error to Observer.onError", new CompositeException(Arrays.asList(e, e2)));
                }
            }
            // auto-unsubscribe
            subscription.unsubscribe();
        }
    }

    @Override
    public void onNext(T args) {
        try {
            if (!isFinished.get()) {
                actual.onNext(args);
            }
        } catch (Throwable e) {
            // handle errors if the onNext implementation fails, not just if the Observable fails
            onError(e);
        }
    }

}

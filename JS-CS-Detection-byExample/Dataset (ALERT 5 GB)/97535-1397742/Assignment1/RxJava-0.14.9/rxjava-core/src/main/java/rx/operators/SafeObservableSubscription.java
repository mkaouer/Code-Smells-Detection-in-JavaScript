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

import java.util.concurrent.atomic.AtomicReference;

import rx.Subscription;

/**
 * Thread-safe wrapper around Observable Subscription that ensures unsubscribe can be called only once.
 * <p>
 * Also used to:
 * <p>
 * <ul>
 * <li>allow the AtomicObserver to have access to the subscription in asynchronous execution for checking if unsubscribed occurred without onComplete/onError.</li>
 * <li>handle both synchronous and asynchronous subscribe() execution flows</li>
 * </ul>
 */
public final class SafeObservableSubscription implements Subscription {

    private static final Subscription UNSUBSCRIBED = new Subscription()
    {
        @Override
        public void unsubscribe()
        {
        }
    };
    private final AtomicReference<Subscription> actualSubscription = new AtomicReference<Subscription>();

    public SafeObservableSubscription() {
    }

    public SafeObservableSubscription(Subscription actualSubscription) {
        this.actualSubscription.set(actualSubscription);
    }

    /**
     * Wraps the actual subscription once it exists (if it wasn't available when constructed)
     * 
     * @param actualSubscription
     *            the wrapped subscription
     * @throws IllegalStateException
     *             if trying to set more than once (or use this method after setting via constructor)
     */
    public SafeObservableSubscription wrap(Subscription actualSubscription) {
        if (!this.actualSubscription.compareAndSet(null, actualSubscription)) {
            if (this.actualSubscription.get() == UNSUBSCRIBED) {
                actualSubscription.unsubscribe();
                return this;
            }
            throw new IllegalStateException("Can not set subscription more than once.");
        }
        return this;
    }

    @Override
    public void unsubscribe() {
        // get the real thing and set to null in an atomic operation so we will only ever call unsubscribe once
        Subscription actual = actualSubscription.getAndSet(UNSUBSCRIBED);
        // if it's not null we will unsubscribe
        if (actual != null) {
            actual.unsubscribe();
        }
    }

    public boolean isUnsubscribed() {
        return actualSubscription.get() == UNSUBSCRIBED;
    }
}

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

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import rx.Observable;
import rx.Observable.OnSubscribeFunc;
import rx.Observer;
import rx.Subscription;

/**
 * Bypasses a specified number of elements at the end of an observable sequence.
 */
public class OperationSkipLast {

    /**
     * Bypasses a specified number of elements at the end of an observable
     * sequence.
     * <p>
     * This operator accumulates a queue with a length enough to store the first
     * count elements. As more elements are received, elements are taken from
     * the front of the queue and produced on the result sequence. This causes
     * elements to be delayed.
     * 
     * @param source
     *            the source sequence.
     * @param count
     *            number of elements to bypass at the end of the source
     *            sequence.
     * @return An observable sequence containing the source sequence elements
     *         except for the bypassed ones at the end.
     * 
     * @throws IndexOutOfBoundsException
     *             count is less than zero.
     */
    public static <T> OnSubscribeFunc<T> skipLast(
            Observable<? extends T> source, int count) {
        return new SkipLast<T>(source, count);
    }

    private static class SkipLast<T> implements OnSubscribeFunc<T> {
        private final int count;
        private final Observable<? extends T> source;

        private SkipLast(Observable<? extends T> source, int count) {
            this.count = count;
            this.source = source;
        }

        public Subscription onSubscribe(final Observer<? super T> observer) {
            if (count < 0) {
                throw new IndexOutOfBoundsException(
                        "count could not be negative");
            }
            final SafeObservableSubscription subscription = new SafeObservableSubscription();
            return subscription.wrap(source.subscribe(new Observer<T>() {

                private final ReentrantLock lock = new ReentrantLock();

                /**
                 * Store the last count elements until now.
                 */
                private final Deque<T> deque = new LinkedList<T>();

                @Override
                public void onCompleted() {
                    observer.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }

                @Override
                public void onNext(T value) {
                    if (count == 0) {
                        // If count == 0, we do not need to put value into deque
                        // and remove it at once. We can emit the value
                        // directly.
                        try {
                            observer.onNext(value);
                        } catch (Throwable ex) {
                            observer.onError(ex);
                            subscription.unsubscribe();
                        }
                        return;
                    }
                    lock.lock();
                    try {
                        deque.offerLast(value);
                        if (deque.size() > count) {
                            // Now deque has count + 1 elements, so the first
                            // element in the deque definitely does not belong
                            // to the last count elements of the source
                            // sequence. We can emit it now.
                            observer.onNext(deque.removeFirst());
                        }
                    } catch (Throwable ex) {
                        observer.onError(ex);
                        subscription.unsubscribe();
                    } finally {
                        lock.unlock();
                    }
                }

            }));
        }
    }
}

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

import rx.Observable.Operator;
import rx.Subscriber;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;

/**
 * Returns an Observable that emits all distinct items emitted by the source.
 *
 * @param <T> the value type
 * @param <U> the key type
 */
public final class OperatorDistinct<T, U> implements Operator<T, T> {
    final Func1<? super T, ? extends U> keySelector;

    static final class Holder {
        static final OperatorDistinct<?,?> INSTANCE = new OperatorDistinct<Object,Object>(UtilityFunctions.<Object>identity());
    }

    /**
     * Returns a singleton instance of OperatorDistinct that was built using
     * the identity function for comparison (<code>new OperatorDistinct(UtilityFunctions.identity())</code>).
     *
     * @param <T> the value type
     * @return Operator that emits distinct values only (regardless of order) using the identity function for comparison
     */
    @SuppressWarnings("unchecked")
    public static <T> OperatorDistinct<T, T> instance() {
        return (OperatorDistinct<T, T>) Holder.INSTANCE;
    }

    public OperatorDistinct(Func1<? super T, ? extends U> keySelector) {
        this.keySelector = keySelector;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        return new Subscriber<T>(child) {
            Set<U> keyMemory = new HashSet<U>();

            @Override
            public void onNext(T t) {
                U key = keySelector.call(t);
                if (keyMemory.add(key)) {
                    child.onNext(t);
                } else {
                    request(1);
                }
            }

            @Override
            public void onError(Throwable e) {
                keyMemory = null;
                child.onError(e);
            }

            @Override
            public void onCompleted() {
                keyMemory = null;
                child.onCompleted();
            }

        };
    }
}

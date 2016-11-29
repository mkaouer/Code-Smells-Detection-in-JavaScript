/**
 * Copyright 2015 Netflix, Inc.
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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * Utility functions for use with backpressure.
 *
 */
public final class BackpressureUtils {
    /** Utility class, no instances. */
    private BackpressureUtils() {
        throw new IllegalStateException("No instances!");
    }
    /**
     * Adds {@code n} to {@code requested} field and returns the value prior to
     * addition once the addition is successful (uses CAS semantics). If
     * overflows then sets {@code requested} field to {@code Long.MAX_VALUE}.
     * 
     * @param requested
     *            atomic field updater for a request count
     * @param object
     *            contains the field updated by the updater
     * @param n
     *            the number of requests to add to the requested count
     * @return requested value just prior to successful addition
     */
    public static <T> long getAndAddRequest(AtomicLongFieldUpdater<T> requested, T object, long n) {
        // add n to field but check for overflow
        while (true) {
            long current = requested.get(object);
            long next = addCap(current, n);
            if (requested.compareAndSet(object, current, next)) {
                return current;
            }
        }
    }

    /**
     * Adds {@code n} to {@code requested} and returns the value prior to addition once the
     * addition is successful (uses CAS semantics). If overflows then sets
     * {@code requested} field to {@code Long.MAX_VALUE}.
     * 
     * @param requested
     *            atomic long that should be updated
     * @param n
     *            the number of requests to add to the requested count
     * @return requested value just prior to successful addition
     */
    public static long getAndAddRequest(AtomicLong requested, long n) {
        // add n to field but check for overflow
        while (true) {
            long current = requested.get();
            long next = addCap(current, n);
            if (requested.compareAndSet(current, next)) {
                return current;
            }
        }
    }
    
    /**
     * Multiplies two positive longs and caps the result at Long.MAX_VALUE.
     * @param a the first value
     * @param b the second value
     * @return the capped product of a and b
     */
    public static long multiplyCap(long a, long b) {
        long u = a * b;
        if (((a | b) >>> 31) != 0) {
            if (b != 0L && (u / b != a)) {
                u = Long.MAX_VALUE;
            }
        }
        return u;
    }
    
    /**
     * Adds two positive longs and caps the result at Long.MAX_VALUE.
     * @param a the first value
     * @param b the second value
     * @return the capped sum of a and b
     */
    public static long addCap(long a, long b) {
        long u = a + b;
        if (u < 0L) {
            u = Long.MAX_VALUE;
        }
        return u;
    }
    
    /**
     * Atomically subtracts a value from the requested amount unless it's at Long.MAX_VALUE.
     * @param requested the requested amount holder
     * @param n the value to subtract from the requested amount, has to be positive (not verified)
     * @return the new requested amount
     * @throws IllegalStateException if n is greater than the current requested amount, which
     * indicates a bug in the request accounting logic
     */
    public static long produced(AtomicLong requested, long n) {
        for (;;) {
            long current = requested.get();
            if (current == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            long next = current - n;
            if (next < 0L) {
                throw new IllegalStateException("More produced than requested: " + next);
            }
            if (requested.compareAndSet(current, next)) {
                return next;
            }
        }
    }
}

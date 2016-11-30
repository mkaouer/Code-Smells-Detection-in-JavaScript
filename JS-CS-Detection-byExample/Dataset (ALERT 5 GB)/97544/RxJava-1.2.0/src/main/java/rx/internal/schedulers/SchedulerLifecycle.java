/**
 * Copyright 2016 Netflix, Inc.
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

package rx.internal.schedulers;

/**
 * Represents the capability of a Scheduler to be start or shut down its maintained
 * threads.
 */
public interface SchedulerLifecycle {
    /**
     * Allows the Scheduler instance to start threads
     * and accept tasks on them.
     * <p>Implementations should make sure the call is idempotent and thread-safe.
     */
    void start();
    /**
     * Instructs the Scheduler instance to stop threads
     * and stop accepting tasks on any outstanding Workers.
     * <p>Implementations should make sure the call is idempotent and thread-safe.
     */
    void shutdown();
}
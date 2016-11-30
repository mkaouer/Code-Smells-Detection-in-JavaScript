/*
 * Copyright 2014 Realm Inc.
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
package io.realm;

import android.test.AndroidTestCase;
import android.util.Log;

import io.realm.entities.Dog;
import io.realm.internal.android.LooperThread;

public class NotificationsTest extends AndroidTestCase {
    public void testMessageToDeadThread() {
        Realm realm = Realm.getInstance(getContext());

        // Number of handlers before
        final int handlersBefore = LooperThread.handlers.size();

        // Make sure the Looper Thread is alive
        LooperThread looperThread = LooperThread.getInstance();
        assertTrue(looperThread.isAlive());

        Thread thread = new Thread() {
            @Override
            public void run() {
                Realm r = Realm.getInstance(getContext());
                assertFalse(handlersBefore == LooperThread.handlers.size());
                r.addChangeListener(new RealmChangeListener() {
                    @Override
                    public void onChange() {
                        Log.i("Notification Test", "Notification Received");
                    }
                });
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            fail();
        }
        assertFalse(thread.isAlive()); // Make sure the thread is dead
        realm.beginTransaction();
        Dog dog = realm.createObject(Dog.class);
        dog.setName("Rex");
        realm.commitTransaction();

        // Give some time to log the exception
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(0, looperThread.exceptions.size());
    }
}

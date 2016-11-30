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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.realm.entities.AllTypes;
import io.realm.entities.CyclicType;
import io.realm.entities.AnnotationNameConventions;
import io.realm.entities.Dog;
import io.realm.internal.Row;


public class RealmObjectTest extends AndroidTestCase {

    private Realm testRealm;

    private static final int TEST_SIZE = 5;
    private static final boolean REMOVE_FIRST = true;
    private static final boolean REMOVE_LAST = false;

    @Override
    protected void setUp() throws Exception {
        Realm.deleteRealmFile(getContext());
        testRealm = Realm.getInstance(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        testRealm.close();
    }

    // Row realmGetRow()
    public void testRealmGetRowReturnsValidRow() {

        testRealm.beginTransaction();
        RealmObject realmObject = testRealm.createObject(AllTypes.class);

        Row row = realmObject.row;

        testRealm.commitTransaction();
        assertNotNull("RealmObject.realmGetRow returns zero ", row);
        assertEquals(8, row.getColumnCount());
    }

    public void testStringEncoding() {
        String[] strings = {"ABCD", "ÆØÅ", "Ö∫Ë", "ΠΑΟΚ", "Здравей"};

        testRealm.beginTransaction();
        testRealm.clear(AllTypes.class);

        for (String str : strings) {
            AllTypes obj1 = testRealm.createObject(AllTypes.class);
            obj1.setColumnString(str);
        }
        testRealm.commitTransaction();

        RealmResults<AllTypes> objects = testRealm.allObjects(AllTypes.class);
        assertEquals(strings.length, objects.size());
        int i = 0;
        for (AllTypes obj : objects) {
            String s = obj.getColumnString();
            assertEquals(strings[i], s);
            i++;
        }
    }

    // removing original object and see if has been removed
    public void testRemoveFromRealm() {
        Realm realm = Realm.getInstance(getContext());
        realm.beginTransaction();
        Dog rex = realm.createObject(Dog.class);
        rex.setName("Rex");
        Dog fido = realm.createObject(Dog.class);
        fido.setName("Fido");
        realm.commitTransaction();

        RealmResults<Dog> allDogsBefore = realm.where(Dog.class).equalTo("name", "Rex").findAll();
        assertEquals(1, allDogsBefore.size());

        realm.beginTransaction();
        rex.removeFromRealm();
        realm.commitTransaction();

        RealmResults<Dog> allDogsAfter = realm.where(Dog.class).equalTo("name", "Rex").findAll();
        assertEquals(0  , allDogsAfter.size());

        fido.getName();
        try {
            rex.getName();
            realm.close();
            fail();
        } catch (IllegalStateException ignored) {}

        // deleting rex twice should fail
        realm.beginTransaction();
        try {
            rex.removeFromRealm();
            realm.close();
            fail();
        } catch (IllegalStateException ignored) {}
        realm.commitTransaction();
        realm.close();
    }

    // query for an object, remove it and see it has been removed from realm
    public void testRemoveResultFromRealm() {
        Realm realm = Realm.getInstance(getContext());
        realm.beginTransaction();
        realm.clear(Dog.class);
        Dog dogToAdd = realm.createObject(Dog.class);
        dogToAdd.setName("Rex");
        realm.commitTransaction();

        assertEquals(1, realm.allObjects(Dog.class).size());

        Dog dogToRemove = realm.where(Dog.class).findFirst();
        assertNotNull(dogToRemove);
        realm.beginTransaction();
        dogToRemove.removeFromRealm();
        realm.commitTransaction();

        assertEquals(0, realm.allObjects(Dog.class).size());
        try {
            dogToAdd.getName();
            realm.close();
            fail();
        }
        catch (IllegalStateException ignored) {}
        try {
            dogToRemove.getName();
            realm.close();
            fail();
        }
        catch (IllegalStateException ignored) {}
        realm.close();
    }

    public void removeOneByOne(boolean atFirst) {
        Set<Long> ages = new HashSet<Long>();
        testRealm.beginTransaction();
        testRealm.clear(Dog.class);
        for (int i = 0; i < TEST_SIZE; i++) {
            Dog dog = testRealm.createObject(Dog.class);
            dog.setAge(i);
            ages.add((long) i);
        }
        testRealm.commitTransaction();

        assertEquals(TEST_SIZE, testRealm.allObjects(Dog.class).size());

        RealmResults<Dog> dogs = testRealm.allObjects(Dog.class);
        for (int i = 0; i < TEST_SIZE; i++) {
            testRealm.beginTransaction();
            Dog dogToRemove;
            if (atFirst) {
                dogToRemove = dogs.first();
            } else {
                dogToRemove = dogs.last();
            }
            ages.remove(Long.valueOf(dogToRemove.getAge()));
            dogToRemove.removeFromRealm();

            // object is no longer valid
            try {
                dogToRemove.getAge();
                fail();
            }
            catch (IllegalStateException ignored) {}

            testRealm.commitTransaction();

            // and removed from realm and remaining objects are place correctly
            RealmResults<Dog> remainingDogs = testRealm.allObjects(Dog.class);
            assertEquals(TEST_SIZE - i - 1, remainingDogs.size());
            for (Dog dog : remainingDogs) {
                assertTrue(ages.contains(Long.valueOf(dog.getAge())));
            }
        }
    }

    public void testRemoveFromRealmAtPosition() {
        removeOneByOne(REMOVE_FIRST);
        removeOneByOne(REMOVE_LAST);
    }

    public boolean methodWrongThread(final boolean callGetter) throws ExecutionException, InterruptedException {
        Realm realm = Realm.getInstance(getContext());
        realm.beginTransaction();
        realm.createObject(AllTypes.class);
        realm.commitTransaction();
        final AllTypes allTypes = realm.where(AllTypes.class).findFirst();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    if (callGetter) {
                        allTypes.getColumnFloat();
                    } else {
                        allTypes.setColumnFloat(1.0f);
                    }
                    return false;
                } catch (IllegalStateException ignored) {
                    return true;
                }
            }
        });

        Boolean result = future.get();
        realm.close();
        return result;
    }

    public void testGetSetWrongThread() throws ExecutionException, InterruptedException {
        assertTrue(methodWrongThread(true));
        assertTrue(methodWrongThread(false));
    }

    public void testEquals() {
        testRealm.beginTransaction();
        CyclicType ct = testRealm.createObject(CyclicType.class);
        ct.setName("Foo");
        testRealm.commitTransaction();

        CyclicType ct1 = testRealm.where(CyclicType.class).findFirst();
        CyclicType ct2 = testRealm.where(CyclicType.class).findFirst();

        assertTrue(ct1.equals(ct1));
        assertTrue(ct2.equals(ct2));
    }

    public void testEqualsAfterModification() {
        testRealm.beginTransaction();
        CyclicType ct = testRealm.createObject(CyclicType.class);
        ct.setName("Foo");
        testRealm.commitTransaction();

        CyclicType ct1 = testRealm.where(CyclicType.class).findFirst();
        CyclicType ct2 = testRealm.where(CyclicType.class).findFirst();

        testRealm.beginTransaction();
        ct1.setName("Baz");
        testRealm.commitTransaction();

        assertTrue(ct1.equals(ct1));
        assertTrue(ct2.equals(ct2));
    }

    public void testEqualsStandAlone() {
        testRealm.beginTransaction();
        CyclicType ct1 = testRealm.createObject(CyclicType.class);
        ct1.setName("Foo");
        testRealm.commitTransaction();

        CyclicType ct2 = new CyclicType();
        ct2.setName("Bar");

        assertFalse(ct1.equals(ct2));
        assertFalse(ct2.equals(ct1));
    }

    public void testCyclicEquals() {
        testRealm.beginTransaction();
        CyclicType foo = createCyclicData();
        testRealm.commitTransaction();

        assertEquals(foo, testRealm.where(CyclicType.class).equalTo("name", "Foo").findFirst());
    }

    public void testCyclicToString() {
        testRealm.beginTransaction();
        CyclicType foo = createCyclicData();
        testRealm.commitTransaction();

        String expected = "CyclicType = [{name:Foo},{object:CyclicType},{objects:RealmList<CyclicType>[0]}]";
        assertEquals(expected, foo.toString());
    }

    public void testCyclicHashCode() {
        testRealm.beginTransaction();
        CyclicType foo = createCyclicData();
        testRealm.commitTransaction();

        assertEquals(1344723738, foo.hashCode());
    }

    private CyclicType createCyclicData() {
        CyclicType foo = testRealm.createObject(CyclicType.class);
        foo.setName("Foo");
        CyclicType bar = testRealm.createObject(CyclicType.class);
        bar.setName("Bar");

        // Setup cycle on normal object references
        foo.setObject(bar);
        bar.setObject(foo);
        return foo;
    }

    public void testDateType() {
        long testDatesNotValid[] = {Long.MIN_VALUE, Long.MAX_VALUE};
        long testDatesValid[] = {-1000, 0, 1000};
        long testDatesLoosePrecision[] = {1, 1001};

        // test valid dates
        testRealm.beginTransaction();
        for (long value : testDatesValid) {
            AllTypes allTypes = testRealm.createObject(AllTypes.class);
            allTypes.setColumnDate(new Date(value));
        }
        testRealm.commitTransaction();

        int i = 0;
        for (AllTypes allTypes : testRealm.allObjects(AllTypes.class)) {
            assertEquals("Item " + i, new Date(testDatesValid[i]), allTypes.getColumnDate());
            i++;
        }

        // test valid dates but with precision lost
        testRealm.beginTransaction();
        testRealm.clear(AllTypes.class);
        for (long value : testDatesLoosePrecision) {
            AllTypes allTypes = testRealm.createObject(AllTypes.class);
            allTypes.setColumnDate(new Date(value));
        }
        testRealm.commitTransaction();

        i = 0;
        for (AllTypes allTypes : testRealm.allObjects(AllTypes.class)) {
            assertFalse("Item " + i, new Date(testDatesLoosePrecision[i]) == allTypes.getColumnDate());
            assertEquals("Item " + i, new Date(1000*(testDatesLoosePrecision[i]/1000)), allTypes.getColumnDate());
            i++;
        }

        // test invalid dates
        for (long value : testDatesNotValid) {
            try {
                testRealm.beginTransaction();
                testRealm.clear(AllTypes.class);
                AllTypes allTypes = testRealm.createObject(AllTypes.class);
                allTypes.setColumnDate(new Date(value));
                testRealm.commitTransaction();
                fail();
            } catch (IllegalArgumentException ignored) { testRealm.cancelTransaction(); }
        }
    }

    private Date newDate(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void addDate(int year, int month, int dayOfMonth) {
        Date date = newDate(year, month, dayOfMonth);

        testRealm.beginTransaction();
        testRealm.clear(AllTypes.class);
        AllTypes allTypes = testRealm.createObject(AllTypes.class);
        allTypes.setColumnDate(date);
        testRealm.commitTransaction();

        AllTypes object = testRealm.allObjects(AllTypes.class).first();

        // Realm does not support millisec precision
        assertEquals(1000 * (date.getTime() / 1000), 1000 * (object.getColumnDate().getTime() / 1000));
    }

    public void testDateTypeOutOfRange() {
        // ** Must throw if date is too old
        for (int i = 0; i < 2; i++) {
            try {
                addDate(1900 + i, 1, 1);
                fail();
            } catch (IllegalArgumentException ignored) {
                testRealm.cancelTransaction();
            }
        }

        // ** Supported dates works
        for (int i = 2; i < 10; i++) {
            addDate(1900 + i, 1, 1);
        }

        // ** Must throw if date is too new
        for (int i = 0; i < 2; i++) {
            try {
                addDate(2038 + i, 1, 20);
                fail();
            } catch (IllegalArgumentException ignored) {
                testRealm.cancelTransaction();
            }
        }
    }

}

/**
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import rx.util.functions.Action1;
import rx.util.functions.Func1;

public class ObservableWindowTests {

    @Test
    public void testWindow() {
        final ArrayList<List<Integer>> lists = new ArrayList<List<Integer>>();
        Observable.from(1, 2, 3, 4, 5, 6)
                .window(3).map(new Func1<Observable<Integer>, List<Integer>>() {

                    @Override
                    public List<Integer> call(Observable<Integer> o) {
                        return o.toList().toBlockingObservable().single();
                    }

                }).toBlockingObservable().forEach(new Action1<List<Integer>>() {

                    @Override
                    public void call(List<Integer> t) {
                        lists.add(t);
                    }
                });

        assertArrayEquals(lists.get(0).toArray(new Integer[3]), new Integer[] { 1, 2, 3 });
        assertArrayEquals(lists.get(1).toArray(new Integer[3]), new Integer[] { 4, 5, 6 });
        assertEquals(2, lists.size());

    }
}

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

package rx.lang.kotlin

import org.mockito.Mock
import rx.Observable
import org.junit.Before
import org.mockito.MockitoAnnotations
import org.junit.Test
import rx.subscriptions.Subscriptions
import org.mockito.Mockito.*
import org.mockito.Matchers.*
import rx.Observer
import org.junit.Assert.*
import rx.Notification
import rx.Subscription
import kotlin.concurrent.thread
import rx.Observable.OnSubscribeFunc
import rx.lang.kotlin.BasicKotlinTests.AsyncObservable

/**
 * This class use plain Kotlin without extensions from the language adaptor
 */
public class BasicKotlinTests {

    [Mock] var a: ScriptAssertion? = null
    [Mock] var w: Observable<Int>? = null

    [Before]
    public fun before() {
        MockitoAnnotations.initMocks(this)
    }

    fun received<T>(): (T?) -> Unit {
        return {(result: T?) -> a!!.received(result) }
    }

    [Test]
    public fun testCreate() {
        Observable.create<String>{
            it!!.onNext("Hello")
            it.onCompleted()
            Subscriptions.empty()
        }!!.subscribe { result ->
            a!!.received(result)
        }

        verify(a, times(1))!!.received("Hello")
    }

    [Test]
    public fun testFilter() {
        Observable.from(1, 2, 3)!!.filter { it!! >= 2 }!!.subscribe(received())
        verify(a, times(0))!!.received(1);
        verify(a, times(1))!!.received(2);
        verify(a, times(1))!!.received(3);
    }

    [Test]
    public fun testLast() {
        assertEquals("three", Observable.from("one", "two", "three")!!.toBlockingObservable()!!.last())
    }

    [Test]
    public fun testLastWithPredicate() {
        assertEquals("two", Observable.from("one", "two", "three")!!.toBlockingObservable()!!.last { x -> x!!.length == 3 })
    }

    [Test]
    public fun testMap1() {
        Observable.from(1)!!.map { v -> "hello_$v" }!!.subscribe(received())
        verify(a, times(1))!!.received("hello_1")
    }

    [Test]
    public fun testMap2() {
        Observable.from(1, 2, 3)!!.map { v -> "hello_$v" }!!.subscribe((received()))
        verify(a, times(1))!!.received("hello_1")
        verify(a, times(1))!!.received("hello_2")
        verify(a, times(1))!!.received("hello_3")
    }

    [Test]
    public fun testMaterialize() {
        Observable.from(1, 2, 3)!!.materialize()!!.subscribe((received()))
        verify(a, times(4))!!.received(any(javaClass<Notification<Int>>()))
        verify(a, times(0))!!.error(any(javaClass<Exception>()))
    }

    [Test]
    public fun testMergeDelayError() {
        Observable.mergeDelayError(
                Observable.from(1, 2, 3),
                Observable.merge(
                        Observable.from(6),
                        Observable.error(NullPointerException()),
                        Observable.from(7)
                ),
                Observable.from(4, 5)
        )!!.subscribe(received(), { e -> a!!.error(e) })
        verify(a, times(1))!!.received(1)
        verify(a, times(1))!!.received(2)
        verify(a, times(1))!!.received(3)
        verify(a, times(1))!!.received(4)
        verify(a, times(1))!!.received(5)
        verify(a, times(1))!!.received(6)
        verify(a, times(0))!!.received(7)
        verify(a, times(1))!!.error(any(javaClass<NullPointerException>()))
    }

    [Test]
    public fun testMerge() {
        Observable.merge(
                Observable.from(1, 2, 3),
                Observable.merge(
                        Observable.from(6),
                        Observable.error(NullPointerException()),
                        Observable.from(7)
                ),
                Observable.from(4, 5)
        )!!.subscribe(received(), { e -> a!!.error(e) })
        verify(a, times(1))!!.received(1)
        verify(a, times(1))!!.received(2)
        verify(a, times(1))!!.received(3)
        verify(a, times(0))!!.received(4)
        verify(a, times(0))!!.received(5)
        verify(a, times(1))!!.received(6)
        verify(a, times(0))!!.received(7)
        verify(a, times(1))!!.error(any(javaClass<NullPointerException>()))
    }

    [Test]
    public fun testScriptWithMaterialize() {
        TestFactory().observable.materialize()!!.subscribe((received()))
        verify(a, times(2))!!.received(any(javaClass<Notification<Int>>()))
    }

    [Test]
    public fun testScriptWithMerge() {
        val factory = TestFactory()
        Observable.merge(factory.observable, factory.observable)!!.subscribe((received()))
        verify(a, times(1))!!.received("hello_1")
        verify(a, times(1))!!.received("hello_2")
    }

    [Test]
    public fun testFromWithIterable() {
        val list = listOf(1, 2, 3, 4, 5)
        assertEquals(5, Observable.from(list)!!.count()!!.toBlockingObservable()!!.single())
    }

    [Test]
    public fun testFromWithObjects() {
        val list = listOf(1, 2, 3, 4, 5)
        assertEquals(2, Observable.from(list, 6)!!.count()!!.toBlockingObservable()!!.single())
    }

    [Test]
    public fun testStartWith() {
        val list = listOf(10, 11, 12, 13, 14)
        val startList = listOf(1, 2, 3, 4, 5)
        assertEquals(6, Observable.from(list)!!.startWith(0)!!.count()!!.toBlockingObservable()!!.single())
        assertEquals(10, Observable.from(list)!!.startWith(startList)!!.count()!!.toBlockingObservable()!!.single())
    }

    [Test]
    public fun testScriptWithOnNext() {
        TestFactory().observable.subscribe((received()))
        verify(a, times(1))!!.received("hello_1")
    }

    [Test]
    public fun testSkipTake() {
        Observable.from(1, 2, 3)!!.skip(1)!!.take(1)!!.subscribe(received())
        verify(a, times(0))!!.received(1)
        verify(a, times(1))!!.received(2)
        verify(a, times(0))!!.received(3)
    }

    [Test]
    public fun testSkip() {
        Observable.from(1, 2, 3)!!.skip(2)!!.subscribe(received())
        verify(a, times(0))!!.received(1)
        verify(a, times(0))!!.received(2)
        verify(a, times(1))!!.received(3)
    }

    [Test]
    public fun testTake() {
        Observable.from(1, 2, 3)!!.take(2)!!.subscribe(received())
        verify(a, times(1))!!.received(1)
        verify(a, times(1))!!.received(2)
        verify(a, times(0))!!.received(3)
    }

    [Test]
    public fun testTakeLast() {
        TestFactory().observable.takeLast(1)!!.subscribe((received()))
        verify(a, times(1))!!.received("hello_1")
    }

    [Test]
    public fun testTakeWhile() {
        Observable.from(1, 2, 3)!!.takeWhile { x -> x!! < 3 }!!.subscribe(received())
        verify(a, times(1))!!.received(1)
        verify(a, times(1))!!.received(2)
        verify(a, times(0))!!.received(3)
    }

    [Test]
    public fun testTakeWhileWithIndex() {
        Observable.from(1, 2, 3)!!.takeWhileWithIndex { x, i -> i!! < 2 }!!.subscribe(received())
        verify(a, times(1))!!.received(1)
        verify(a, times(1))!!.received(2)
        verify(a, times(0))!!.received(3)
    }

    [Test]
    public fun testToSortedList() {
        TestFactory().numbers.toSortedList()!!.subscribe(received())
        verify(a, times(1))!!.received(listOf(1, 2, 3, 4, 5))
    }

    [Test]
    public fun testForEach() {
        Observable.create(AsyncObservable())!!.toBlockingObservable()!!.forEach(received())
        verify(a, times(1))!!.received(1)
        verify(a, times(1))!!.received(2)
        verify(a, times(1))!!.received(3)
    }

    [Test(expected = javaClass<RuntimeException>())]
    public fun testForEachWithError() {
        Observable.create(AsyncObservable())!!.toBlockingObservable()!!.forEach { throw RuntimeException("err") }
        fail("we expect an exception to be thrown")
    }

    [Test]
    public fun testLastOrDefault() {
        assertEquals("two", Observable.from("one", "two")!!.toBlockingObservable()!!.lastOrDefault("default") { x -> x!!.length == 3 })
        assertEquals("default", Observable.from("one", "two")!!.toBlockingObservable()!!.lastOrDefault("default") { x -> x!!.length > 3 })
    }

    [Test(expected = javaClass<IllegalStateException>())]
    public fun testSingle() {
        assertEquals("one", Observable.from("one")!!.toBlockingObservable()!!.single { x -> x!!.length == 3 })
        Observable.from("one", "two")!!.toBlockingObservable()!!.single { x -> x!!.length == 3 }
        fail()
    }

    [Test]
    public fun testDefer() {
        Observable.defer { Observable.from(1, 2) }!!.subscribe(received())
        verify(a, times(1))!!.received(1)
        verify(a, times(1))!!.received(2)
    }

    [Test]
    public fun testAll() {
        Observable.from(1, 2, 3)!!.all { x -> x!! > 0 }!!.subscribe(received())
        verify(a, times(1))!!.received(true)
    }

    [Test]
    public fun testZip() {
        val o1 = Observable.from(1, 2, 3)!!
        val o2 = Observable.from(4, 5, 6)!!
        val o3 = Observable.from(7, 8, 9)!!

        val values = Observable.zip(o1, o2, o3) { a, b, c -> listOf(a, b, c) }!!.toList()!!.toBlockingObservable()!!.single()!!
        assertEquals(listOf(1, 4, 7), values[0])
        assertEquals(listOf(2, 5, 8), values[1])
        assertEquals(listOf(3, 6, 9), values[2])
    }

    [Test]
    public fun testZipWithIterable() {
        val o1 = Observable.from(1, 2, 3)!!
        val o2 = Observable.from(4, 5, 6)!!
        val o3 = Observable.from(7, 8, 9)!!

        val values = Observable.zip(listOf(o1, o2, o3)) { args -> listOf(*args) }!!.toList()!!.toBlockingObservable()!!.single()!!
        assertEquals(listOf(1, 4, 7), values[0])
        assertEquals(listOf(2, 5, 8), values[1])
        assertEquals(listOf(3, 6, 9), values[2])
    }

    [Test]
    public fun testGroupBy() {
        var count = 0

        Observable.from("one", "two", "three", "four", "five", "six")!!
                .groupBy { s -> s!!.length }!!
                .mapMany { groupObervable ->
            groupObervable!!.map { s ->
                "Value: $s Group ${groupObervable.getKey()}"
            }
        }!!
                .toBlockingObservable()!!.forEach { s ->
            println(s)
            count++
        }

        assertEquals(6, count)
    }

    public trait ScriptAssertion{
        fun error(e: Throwable?)

        fun received(e: Any?)
    }

    public class TestFactory(){
        var counter = 1

        val numbers: Observable<Int>
            get(){
                return Observable.from(1, 3, 2, 5, 4)!!
            }

        val onSubscribe: TestOnSubscribe
            get(){
                return TestOnSubscribe(counter++)
            }

        val observable: Observable<String>
            get(){
                return Observable.create(onSubscribe)!!
            }

    }

    class AsyncObservable : OnSubscribeFunc<Int>{
        override fun onSubscribe(t1: Observer<in Int>?): Subscription? {
            thread {
                Thread.sleep(50)
                t1!!.onNext(1)
                t1.onNext(2)
                t1.onNext(3)
                t1.onCompleted()
            }
            return Subscriptions.empty()
        }
    }

    class TestOnSubscribe(val count: Int) : OnSubscribeFunc<String>{
        override fun onSubscribe(t1: Observer<in String>?): Subscription? {
            t1!!.onNext("hello_$count")
            t1.onCompleted()
            return Subscription { }
        }

    }
}
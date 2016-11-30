/**
 * Copyright 2014 Netflix, Inc.
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

package rx.lang.scala

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Subscriptions are returned from all `Observable.subscribe` methods to allow unsubscribing.
 *
 * This interface is the equivalent of `IDisposable` in the .NET Rx implementation.
 */
trait Subscription {

  private [scala] val unsubscribed = new AtomicBoolean(false)
  private [scala] val asJavaSubscription: rx.Subscription = new rx.Subscription {
    override def unsubscribe() { unsubscribed.compareAndSet(false, true) }
    override def isUnsubscribed(): Boolean = { unsubscribed.get() }
  }


  /**
   * Call this method to stop receiving notifications on the Observer that was registered when
   * this Subscription was received.
   */
  def unsubscribe() = asJavaSubscription.unsubscribe()

  /**
   * Checks if the subscription is unsubscribed.
   */
  def isUnsubscribed = unsubscribed.get()

}

object Subscription {

  /**
   * Creates an [[rx.lang.scala.Subscription]] from an [[rx.Subscription]].             ß
   */
  private [scala] def apply(subscription: rx.Subscription): Subscription = {
    subscription match {
      case x: rx.subscriptions.BooleanSubscription => new rx.lang.scala.subscriptions.BooleanSubscription(x)
      case x: rx.subscriptions.CompositeSubscription => new rx.lang.scala.subscriptions.CompositeSubscription(x)
      case x: rx.subscriptions.MultipleAssignmentSubscription => new rx.lang.scala.subscriptions.MultipleAssignmentSubscription(x)
      case x: rx.subscriptions.SerialSubscription => new rx.lang.scala.subscriptions.SerialSubscription(x)
      case x: rx.Subscription => apply{ x.unsubscribe }
    }
  }

  /**
   * Creates an [[rx.lang.scala.Subscription]] that invokes the specified action when unsubscribed.
   */
  def apply(u: => Unit): Subscription = new Subscription() {
    override val asJavaSubscription = new rx.Subscription {
      override def unsubscribe() { if(unsubscribed.compareAndSet(false, true)) { u } }
      override def isUnsubscribed(): Boolean = { unsubscribed.get() }
    }
  }

  /**
   * Creates an empty [[rx.lang.scala.Subscription]].
   */
  def apply(): Subscription = Subscription {}

}
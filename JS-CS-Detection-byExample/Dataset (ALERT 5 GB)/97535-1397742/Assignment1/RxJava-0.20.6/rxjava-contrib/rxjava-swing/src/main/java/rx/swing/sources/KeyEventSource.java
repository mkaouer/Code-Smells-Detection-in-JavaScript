/**
 * Copyright 2014 Netflix, Inc.
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
package rx.swing.sources;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.SwingObservable;
import rx.subscriptions.SwingSubscriptions;

public enum KeyEventSource { ; // no instances

    /**
     * @see rx.observables.SwingObservable#fromKeyEvents(Component)
     */
    public static Observable<KeyEvent> fromKeyEventsOf(final Component component) {
        return Observable.create(new OnSubscribe<KeyEvent>() {
            @Override
            public void call(final Subscriber<? super KeyEvent> subscriber) {
                SwingObservable.assertEventDispatchThread();
                final KeyListener listener = new KeyListener() {
                    @Override
                    public void keyPressed(KeyEvent event) {
                        subscriber.onNext(event);
                    }

                    @Override
                    public void keyReleased(KeyEvent event) {
                        subscriber.onNext(event);
                    }

                    @Override
                    public void keyTyped(KeyEvent event) {
                        subscriber.onNext(event);
                    }
                };
                component.addKeyListener(listener);

                subscriber.add(SwingSubscriptions.unsubscribeInEventDispatchThread(new Action0() {
                    @Override
                    public void call() {
                        component.removeKeyListener(listener);
                    }
                }));
            }
        });
    }

    /**
     * @see rx.observables.SwingObservable#fromPressedKeys(Component)
     */
    public static Observable<Set<Integer>> currentlyPressedKeysOf(Component component) {
        class CollectKeys implements Func2<Set<Integer>, KeyEvent, Set<Integer>>{
            @Override
            public Set<Integer> call(Set<Integer> pressedKeys, KeyEvent event) {
                Set<Integer> afterEvent = new HashSet<Integer>(pressedKeys);
                switch (event.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        afterEvent.add(event.getKeyCode());
                        break;
                        
                    case KeyEvent.KEY_RELEASED:
                        afterEvent.remove(event.getKeyCode());
                        break;
                      
                    default: // nothing to do
                }
                return afterEvent;
            }
        }
        
        Observable<KeyEvent> filteredKeyEvents = fromKeyEventsOf(component).filter(new Func1<KeyEvent, Boolean>() {
            @Override
            public Boolean call(KeyEvent event) {
                return event.getID() == KeyEvent.KEY_PRESSED || event.getID() == KeyEvent.KEY_RELEASED;
            }
        });
        
        return filteredKeyEvents.scan(Collections.<Integer>emptySet(), new CollectKeys()).skip(1);
    }
    
}

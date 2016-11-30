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
package rx;

/**
 * An object representing a notification sent to an {@link Observable}.
 * 
 * For the Microsoft Rx equivalent see: http://msdn.microsoft.com/en-us/library/hh229462(v=vs.103).aspx
 */
public class Notification<T> {

    private final Kind kind;
    private final Throwable throwable;
    private final T value;

    /**
     * A constructor used to represent an onNext notification.
     * 
     * @param value
     *            The data passed to the onNext method.
     */
    public Notification(T value) {
        this.value = value;
        this.throwable = null;
        this.kind = Kind.OnNext;
    }

    /**
     * A constructor used to represent an onError notification.
     * 
     * @param exception
     *            The exception passed to the onError notification.
     */
    public Notification(Throwable exception) {
        this.throwable = exception;
        this.value = null;
        this.kind = Kind.OnError;
    }

    /**
     * A constructor used to represent an onCompleted notification.
     */
    public Notification() {
        this.throwable = null;
        this.value = null;
        this.kind = Kind.OnCompleted;
    }

    /**
     * Retrieves the exception associated with an onError notification.
     * 
     * @return Throwable associated with an onError notification.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Retrieves the data associated with an onNext notification.
     * 
     * @return The data associated with an onNext notification.
     */
    public T getValue() {
        return value;
    }

    /**
     * Retrieves a value indicating whether this notification has a value.
     * 
     * @return a value indicating whether this notification has a value.
     */
    public boolean hasValue() {
        return isOnNext() && value != null;
    }

    /**
     * Retrieves a value indicating whether this notification has an exception.
     * 
     * @return a value indicating whether this notification has an exception.
     */
    public boolean hasThrowable() {
        return isOnError() && throwable != null;
    }

    /**
     * Retrieves the kind of the notification: OnNext, OnError, OnCompleted
     * 
     * @return the kind of the notification: OnNext, OnError, OnCompleted
     */
    public Kind getKind() {
        return kind;
    }

    public boolean isOnError() {
        return getKind() == Kind.OnError;
    }

    public boolean isOnCompleted() {
        return getKind() == Kind.OnCompleted;
    }

    public boolean isOnNext() {
        return getKind() == Kind.OnNext;
    }

    public static enum Kind {
        OnNext, OnError, OnCompleted
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[").append(super.toString()).append(" ").append(getKind());
        if (hasValue())
            str.append(" ").append(getValue());
        if (hasThrowable())
            str.append(" ").append(getThrowable().getMessage());
        str.append("]");
        return str.toString();
    }

    @Override
    public int hashCode() {
        int hash = getKind().hashCode();
        if (hasValue())
            hash = hash * 31 + getValue().hashCode();
        if (hasThrowable())
            hash = hash * 31 + getThrowable().hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj.getClass() != getClass())
            return false;
        Notification<?> notification = (Notification<?>) obj;
        if (notification.getKind() != getKind())
            return false;
        if (hasValue() && !getValue().equals(notification.getValue()))
            return false;
        if (hasThrowable() && !getThrowable().equals(notification.getThrowable()))
            return false;
        return true;
    }
}

;
; Copyright 2013 Netflix, Inc.
;  
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
; 
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;
(ns rx.lang.clojure.examples.rx-examples
  (:require [rx.lang.clojure.interop :as rx])
  (:import rx.Observable rx.subscriptions.Subscriptions))

; NOTE on naming conventions. I'm using camelCase names (against clojure convention)
; in this file as I'm purposefully keeping functions and methods across
; different language implementations in-sync for easy comparison.

; --------------------------------------------------
; Hello World!
; --------------------------------------------------

(defn hello
  [& args]
  (-> (Observable/from args)
    (.subscribe (rx/action [v] (println (str "Hello " v "!"))))))

; To see output
(comment
  (hello "Ben" "George"))

; --------------------------------------------------
; Create Observable from Existing Data
; --------------------------------------------------


(defn existingDataFromNumbersUsingFrom []
  (Observable/from [1 2 3 4 5 6]))

(defn existingDataFromObjectsUsingFrom []
  (Observable/from ["a" "b" "c"]))

(defn existingDataFromListUsingFrom []
  (let [list [5, 6, 7, 8]]
    (Observable/from list)))

(defn existingDataWithJust []
  (Observable/just "one object"))

; --------------------------------------------------
; Custom Observable
; --------------------------------------------------

(defn customObservableBlocking []
  "This example shows a custom Observable that blocks
   when subscribed to (does not spawn an extra thread).

  returns Observable<String>"
  (Observable/create
    (rx/fn [observer]
      (doseq [x (range 50)] (-> observer (.onNext (str "value_" x))))
      ; after sending all values we complete the sequence
      (-> observer .onCompleted)
      ; return a NoOpSubsription since this blocks and thus
      ; can't be unsubscribed from
      (Subscriptions/empty))))

; To see output
(comment
  (.subscribe (customObservableBlocking) (rx/action* println)))

(defn customObservableNonBlocking []
  "This example shows a custom Observable that does not block
  when subscribed to as it spawns a separate thread.

  returns Observable<String>"
  (Observable/create
    (rx/fn [observer]
      (let [f (future
                (doseq [x (range 50)]
                  (-> observer (.onNext (str "anotherValue_" x))))
                ; after sending all values we complete the sequence
                (-> observer .onCompleted))]
        ; return a subscription that cancels the future
        (Subscriptions/create (rx/action [] (future-cancel f)))))))

; To see output
(comment
  (.subscribe (customObservableNonBlocking) (rx/action* println)))


; --------------------------------------------------
; Composition - Simple
; --------------------------------------------------

(defn simpleComposition []
  "Asynchronously calls 'customObservableNonBlocking' and defines
   a chain of operators to apply to the callback sequence."
  (->
    (customObservableNonBlocking)
    (.skip 10)
    (.take 5)
    (.map (rx/fn [v] (str v "_transformed")))
    (.subscribe (rx/action [v] (println "onNext =>" v)))))

; To see output
(comment
  (simpleComposition))


; --------------------------------------------------
; Composition - Multiple async calls combined
; --------------------------------------------------

(defn getUser [userId]
  "Asynchronously fetch user data

  return Observable<Map>"
  (Observable/create
    (rx/fn [observer]
      (let [f (future
                (try
                  ; simulate fetching user data via network service call with latency
                  (Thread/sleep 60)
                  (-> observer (.onNext {:user-id userId
                                         :name "Sam Harris"
                                         :preferred-language (if (= 0 (rand-int 2)) "en-us" "es-us") }))
                  (-> observer .onCompleted)
                  (catch Exception e (-> observer (.onError e))))) ]
        ; a subscription that cancels the future if unsubscribed
        (Subscriptions/create (rx/action [] (future-cancel f)))))))

(defn getVideoBookmark [userId, videoId]
  "Asynchronously fetch bookmark for video

  return Observable<Integer>"
  (Observable/create
    (rx/fn [observer]
      (let [f (future
                (try
                  ; simulate fetching user data via network service call with latency
                  (Thread/sleep 20)
                  (-> observer (.onNext {:video-id videoId
                                         ; 50/50 chance of giving back position 0 or 0-2500
                                         :position (if (= 0 (rand-int 2)) 0 (rand-int 2500))}))
                  (-> observer .onCompleted)
                  (catch Exception e (-> observer (.onError e)))))]
        ; a subscription that cancels the future if unsubscribed
        (Subscriptions/create (rx/action [] (future-cancel f)))))))

(defn getVideoMetadata [videoId, preferredLanguage]
  "Asynchronously fetch movie metadata for a given language
  return Observable<Map>"
  (Observable/create
    (rx/fn [observer]
      (let [f (future
                (try
                  ; simulate fetching video data via network service call with latency
                  (Thread/sleep 50)
                  ; contrived metadata for en-us or es-us
                  (if (= "en-us" preferredLanguage)
                    (-> observer (.onNext {:video-id videoId
                                           :title "House of Cards: Episode 1"
                                           :director "David Fincher"
                                           :duration 3365})))
                  (if (= "es-us" preferredLanguage)
                    (-> observer (.onNext {:video-id videoId
                                           :title "Cámara de Tarjetas: Episodio 1"
                                           :director "David Fincher"
                                           :duration 3365})))
                  (-> observer .onCompleted)
                  (catch Exception e (-> observer (.onError e))))) ]
        ; a subscription that cancels the future if unsubscribed
        (Subscriptions/create (rx/action [] (future-cancel f)))))))


(defn getVideoForUser [userId videoId]
  "Get video metadata for a given userId
  - video metadata
  - video bookmark position
  - user data
  return Observable<Map>"
  (let [user-observable           (-> (getUser userId)
                                    (.map (rx/fn [user] {:user-name (:name user)
                                                      :language (:preferred-language user)})))
        bookmark-observable       (-> (getVideoBookmark userId videoId)
                                    (.map (rx/fn [bookmark] {:viewed-position (:position bookmark)})))
        ; getVideoMetadata requires :language from user-observable so nest inside map function
        video-metadata-observable (-> user-observable
                                    (.mapMany
                                      ; fetch metadata after a response from user-observable is received
                                      (rx/fn [user-map]
                                        (getVideoMetadata videoId (:language user-map)))))]
    ; now combine 3 async sequences using zip
    (-> (Observable/zip bookmark-observable video-metadata-observable user-observable
                        (rx/fn [bookmark-map metadata-map user-map]
                          {:bookmark-map bookmark-map
                           :metadata-map metadata-map
                           :user-map user-map}))
      ; and transform into a single response object
      (.map (rx/fn [data]
              {:video-id       videoId
               :video-metadata (:metadata-map data)
               :user-id        userId
               :language       (:language (:user-map data))
               :bookmark       (:viewed-position (:bookmark-map data)) })))))

; To see output like this:
;    {:video-id 78965, :video-metadata {:video-id 78965, :title Cámara de Tarjetas: Episodio 1,
;      :director David Fincher, :duration 3365}, :user-id 12345, :language es-us, :bookmark 0}
;
(comment
  (-> (getVideoForUser 12345 78965)
    (.subscribe
      (rx/action [x] (println "--- Object ---\n" x))
      (rx/action [e] (println "--- Error ---\n" e))
      (rx/action [] (println "--- Completed ---")))))


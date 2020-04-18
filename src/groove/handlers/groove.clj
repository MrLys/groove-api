(ns groove.handlers.groove
  (:require [groove.handlers.response :refer [response-handler]]
            [clojure.tools.logging :as log]
            [groove.bulwark :as blwrk]))
(defn- failed? [groove]
  (log/info (str "fail? " (= "fail" (:state groove))))
  (= "fail" (:state groove)))
(defn- pass? [groove]
  (log/info (str "pass? " (= "pass" (:state groove))))
  (= "pass" (:state groove)))
(defn- success? [groove]
  (log/info (str "success? " (= "success" (:state groove))))
  (= "success" (:state groove)))
(defn- consecutive? [current prev]
  (log/info (str "consecutive? " (.isEqual (.toLocalDate (:date current)) (.plusDays (.toLocalDate (:date prev)) -1))))
  (.isEqual (.toLocalDate (:date current)) (.plusDays (.toLocalDate (:date prev)) -1)))


(defn- calc-streak [grooves c n]
  (log/info (format "calc-streak c: %d n: %d" c n))
  (if (>= n (count grooves))
   c
  (let [current (nth grooves n)
        previous (nth grooves (dec n))]
    (log/info (format "Gotten to streak c-state:%s ",c n (:state current)))
    (cond (failed? (nth grooves n)) c
          (not (consecutive? current previous)) c
          (pass? current) (calc-streak grooves c (inc n))
          (success? current) (calc-streak grooves (inc c) (inc n))
          :else c))))

(defn calculate-streak [request user-habit-id]
  (let [grooves (blwrk/get-grooves-descending request user-habit-id)]
    (log/info (format "calculating streak with count: %d" (count grooves)))
    (cond (failed? (first grooves))  0
          (pass?  (first grooves)) (calc-streak grooves 0 1)
          (success? (first grooves)) (calc-streak grooves 1 1))))
          
(defn update-streak [groove request]
  (blwrk/update-current-habit-streak! request (:user_habit_id groove) (calculate-streak request (:user_habit_id groove))))

(defn update-groove [groove request]
  (let [date (:date groove)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)]
    (if (.isAfter date today)
      {:error "Cannot update grooves into the future"}
      (let [ret (blwrk/update-groove groove request)]
        (update-streak groove request)
        ret))))


(defn update-groove-handler [groove request]
  (response-handler :PATCH update-groove groove request))

(defn get-grooves-handler [request habit start end]
  (blwrk/get-by-dates request habit start end))

(defn get-all-grooves-handler [request user start end]
  (blwrk/get-all-by-dates request start end));(java.time.Instant/ofEpochMilli  start) (java.time.Instant/ofEpochMilli  end)))

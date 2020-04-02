(ns groove.handlers.groove
  (:require [groove.handlers.response :refer [response-handler]]
            [groove.bulwark :as blwrk]))

(defn update-groove [groove request]
  (let [date (:date groove)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)]
    (if (.isAfter date today)
      {:error "Cannot update grooves into the future"}
      (blwrk/update-groove groove request))))


(defn update-groove-handler [groove request]
  (response-handler :PATCH update-groove groove request))

(defn get-grooves-handler [request habit start end]
  (blwrk/get-by-dates request habit start end))

(defn get-all-grooves-handler [request user start end]
  (blwrk/get-all-by-dates request start end));(java.time.Instant/ofEpochMilli  start) (java.time.Instant/ofEpochMilli  end)))

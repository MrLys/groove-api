(ns groove.handlers.habit
  (:require [groove.handlers.response :refer [response-handler]]
            [groove.util.utils :refer [parseLong]]
            [groove.bulwark :as blwrk]))




(defn- lower-habit-name [habit]
  (assoc habit :name (.toLowerCase (:name habit))))

(defn create-habit-handler [habit request]
  (response-handler :POST blwrk/create-habit (lower-habit-name habit) request))

(defn get-habit-handler [habit-id request]
  (response-handler :GET blwrk/get-habit habit-id request))

(defn get-habits-handler [request]
  (response-handler :GET blwrk/get-habits request))

(defn get-grooves [grooves user_habit_id]
  (filter #(= user_habit_id (:user_habit_id %1)) grooves))

(defn build-habit-map [habits grooves req]
  (reduce
    (fn [coll x]
      (assoc coll
             (keyword (:name x))
             {:name (:name x)
              :id (:id x)
              :owner_id (:id (:identity req))
              :current_streak (:current_streak x)
              :grooves (get-grooves grooves (:id x))})) {} habits))

(defn get-all-habits-with-grooves
  ([req start end]
   (let [habits (blwrk/get-habits req)
         grooves (blwrk/get-all-grooves-by-date-range req start end)]
     (build-habit-map habits grooves req)))
  ([req]
   (let [habits (blwrk/get-habits req)
         grooves (blwrk/get-all-grooves-by-date-range req)]
     (build-habit-map habits grooves req))))

(defn get-habit-with-all-grooves [req user-habit-id start end]
  (let [habit (first (blwrk/get-habit user-habit-id req))
        grooves (blwrk/get-grooves-by-dates-and-habits req user-habit-id start end)]
    {:name (:name habit)
     :id (:id habit)
     :owner_id (:id (:identity req))
     :current_streak (:current_streak habit)
     :grooves grooves}))

(defn get-all-grooves-by-habits [req start end]
  (response-handler :GET get-all-habits-with-grooves req start end))

(defn get-all-grooves-by-habit [req user-habit-id start end]
  (response-handler :GET get-habit-with-all-grooves req user-habit-id start end))


(ns groove.test-utils
  (:require [groove.db :as db]
            [groove.util.utils :refer [parseLong]]))


(defn get-mock-request [user]
  {:identity (db/new-user! user)})

(defn create-user
  ([] (get-mock-request {:password "password" :username "test@test.no" :email "test@test.no"}))
  ([offset] (get-mock-request {:password "password" :username (str "test" offset "@test.no") :email (str "test" offset "@test.no")})))

(defn create-groove [req resp today offset state]
  "Create a groove based on request a user habit and date today"
  {
   :owner_id (parseLong (:id (:identity req)))
   :state state
   :user_habit_id (parseLong (:id resp))
   :date (.plusDays today offset)
   })


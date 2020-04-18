(ns groove.bulwark
  (:require [groove.db :as db]
            [groove.models.groove :refer [Groove]]
            [clojure.tools.logging :as log]
            [schema-tools.core :as st]
            [buddy.hashers :as hashers]
            [groove.util.validation :refer [valid-token?]]
            [groove.util.utils :refer [format-groove parseLong sanitize]]))

(defn- get-user-id [request]
  (parseLong (:id (:identity request))))

(defn get-user [user-id]
  (db/get-user user-id))

(defn get-user-count []
  (db/get-user-count))

(defn- validate-permission [user id]
  (log/info (str "Userid: " user " accessing data for user " id))
  (= (parseLong user) (parseLong id)))

(defn- validate-user-permission [req id]
    (validate-permission (:id (:identity req)) id))

(defn get-last-by-habit [request habit-id]
  (db/get-last-by-habit (get-user-id request) habit-id))

(defn update-current-habit-streak! [request user-habit streak]
  (db/update-current-habit-streak! (get-user-id request) user-habit streak))

(defn get-by-dates [request user-habit-id start-date end-date]
    (db/get-grooves-by-date-range (get-user-id request) user-habit-id start-date end-date))

(defn get-grooves-by-dates-and-habits [request user-habit-id start-date end-date]
    (db/get-grooves-by-date-range (get-user-id request) user-habit-id start-date end-date))

(defn get-all-by-dates [request start-date end-date]
    (db/get-all-grooves-by-date-range (get-user-id request) start-date end-date))

(defn update-groove [groove request]
  (let [logged-in-user (get-user-id request)
        user-habit-id (parseLong (:user_habit_id groove))
        owner-id (parseLong (:owner_id groove))
        valid-user? (validate-permission logged-in-user owner-id)
        habit (db/get-user-habit-by-user-id-and-user-habit-id owner-id user-habit-id)
        groove-id (db/groove-id-by-user-habit-date logged-in-user user-habit-id (:date groove))]
   (if (and valid-user? (not (nil? habit)))
     (if (nil? groove-id)
         (db/create-groove (format-groove groove)) ; this should probably use the id as param after groove/
         (do (db/update-groove groove-id (:state groove) :state )
           groove))
     {:error "Not authorized"})))

(defn create-habit [habit req] ;; One only gets this far if the user is authenticated
  (log/info (str "creating habit" habit))
  (if (validate-user-permission req (:owner_id habit))
    (let [db-habit (db/get-habit-by-name (:name habit))]
      (if (empty? db-habit)
        (sanitize (db/create-user-habit (db/create-habit (:name habit)) (:owner_id habit)))
        (if (nil? (db/get-user-habit (:owner_id habit) (:id db-habit)))
          (db/create-user-habit db-habit (:owner_id habit))
          {:error "You are already tracking that habit"})))
    {:error "Not authorized"}))

(defn get-habits [request]
  (db/get-all-habits-by-user-id (get-user-id request)))

(defn get-habit [habit-id request]
  (db/get-user-habit-with-name (get-user-id request) habit-id))

(defn- activation-helper [id]
  (db/activate-user id))

(defn new-activation-token! [token id date]
  (db/new-activation-token! token id date))

(defn activate-user [token]
  (let [token (db/get-token-by-token token)]
    (if (valid-token? token) ;; user can be activated
      (activation-helper (:user_id token))
      {:error "Not authorized"})))

(defn new-user [user]
  (-> (db/new-user! user)
      (st/dissoc :digest)
      (st/dissoc :refresh_token)))

; Using user-id ensures the requester only receives data connected to the user
(defn get-all-grooves-by-date-range
  ([req start end]
   (db/get-all-grooves-by-date-range (get-user-id req) start end))
  ([req]
   (db/get-all-grooves-by-date-range (get-user-id req))))

(defn update-user-password! [user]
  (let [digest (hashers/derive (:password user))]
    (db/update-user! (:user_id user) :digest digest)))

(defn invalidate-password-token! [token-id user-id ]
  (db/invalidate-password-token! token-id user-id))

(defn get-password-token [token]
  (get-password-token token))

(defn get-user-by-field [field value]
  (let [user (db/get-registered-user-by-field field value)]
    (when-not (nil? user)
      {:user-data (-> user
                      (st/dissoc :digest)
                      (st/dissoc :refresh_token))})))


(defn new-password-token! [user-id expiration token]
  (db/new-password-token! user-id expiration token))


(defn get-all-teams [request]
  (db/get-all-teams-by-user-id (get-user-id request)))

(defn create-team [request name]
  (db/create-team (get-user-id request) name))

(defn get-grooves-descending [request user-habit-id]
  (db/get-grooves-descending (get-user-id request) user-habit-id))

(ns groove.util.validation
  (:require [buddy.core.hash :as hash]
            [toucan.db :as db]
            [groove.models.user :refer [User]]
            [buddy.core.codecs :refer :all]
            [clj-http.client :as client]))

(def ^:private states #{"none", "fail", "success", "pass"})
(def ^:private breach_limit 1)
(def ^:private lower_limit 8)
(def ^:private upper_limit 128)
(def ^:private email-regex
  #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(defn- filter-dgst [suffix dgsts]
  (filter (fn [dgst] (= suffix (.toLowerCase (:hash dgst)))) dgsts))

(defn- match [suffix dgsts] (:freq (first (filter-dgst suffix dgsts))))

(defn- digest-in-digests? [suffix dgsts] ;-- only one match
  (let [match (match suffix dgsts)]
    (if (empty? match)
      false
      (> (Integer/parseInt match) breach_limit))))

(defn- get-prefix [^String dgst]
  (subs dgst 0 5))

(defn- get-suffix [^String dgst]
  (subs dgst 5))

(defn- structure [body]
  (re-seq #"(\w+):(\d+)\r\n" body))

(defn build-map [body]
  (let [body-map (structure body)]
    (if body-map
      (map (fn [x] {:hash (nth x 1) :freq (nth x 2)}) body-map)
      {})))

(defn- fetch-db [prefix]
  (client/get (str "https://api.pwnedpasswords.com/range/" prefix)))

(defn- digest [^String pwd]
  (-> (hash/sha1 pwd)
      (bytes->hex)))

(defn valid-name? [^String name]
  (<= (.length name) 50))

(defn valid-username? [^String name]
  (valid-name? name))

(defn valid-email? [^String email]
  (boolean (and
             (string? email)
             (re-matches email-regex email))))

(defn valid-groove? [state]
  (contains? states (.toLowerCase state)))

(defn valid-date? [date]
  (if date
    true
    false))

(defn valid-user-id? [id]
  (> id 0))

(defn valid-habit-id? [id]
  (> id 0))

(defn- found-in-breach? [^String pwd]
  (let [dgst (digest pwd)]
  (digest-in-digests?
    (get-suffix dgst)
    (build-map
      (:body (fetch-db (get-prefix dgst)))))))

(defn valid-password? [^String pwd]
  (and
    (> (.length pwd) lower_limit)
    (< (.length pwd) upper_limit)
    (not (found-in-breach? pwd))))


(defn valid-auth-scheme?  [request] 0)

(defn valid-token? [token]
  (if (nil? token)
    false
    (let [calendarNow (java.util.Calendar/getInstance)
          calendarToken (java.util.Calendar/getInstance)]
      (.setTime calendarToken (:expiration token))
      (.after calendarToken calendarNow))))

(defn valid-password-token? [token]
  (= (.length token) 36))


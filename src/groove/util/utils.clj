(ns groove.util.utils
  (:require [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-http.client :as client]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [ring.util.codec :as codec]
            [clj-time.coerce :as c]))




(defn create-activation-token []
  (str (java.util.UUID/randomUUID)))

(defn format-groove [groove]
  (assoc groove :state (.toLowerCase (:state groove))))

(defn parseLong [s]
  (if (or (instance? Long s)
          (instance? Integer s))
    s
    (if (re-matches #"\d+" s)
      (Long/valueOf s)
      (throw (.IllegalArgumentException (str s " is not a valid Long"))))))



(defn sanitize [m & args]
  "Updates each value in a map with the url-encoded variation"
  (if (not (map? m))
    m
    (do
      (log/info (str "sanitizing!\n" m))
      (reduce (fn [r [k v]]
            (if (map? v)
              (assoc r k (sanitize v))
              (assoc r k (apply codec/url-encode v args)))) {} m))))

(def truthy? #{"true"})

(defn build-grooves-by-habits [grooves]
  (log/info grooves)
  (reduce (fn [coll x]
            (assoc coll
                   (keyword (:name x))
                   {:name (:name x) :id (:id x) :owner_id (:owner_id x) :grooves (vec (conj (:grooves ((keyword (:name x)) coll)) (dissoc x :name)))}))
          {}
          grooves))

(defn send-to-slack
  "Taken from: https://gist.github.com/mikebroberts/9604828"
  [url text]
  (log/info "A new user has been registered")
  (log/info "sending slack message")
  (log/info (str "url" url))
  (log/info (str "payload: " text))
  (client/post url {:form-params {:payload (json/write-str {:text text})}}))

(defn error
  ([error] (log/debug (:error error)) error)
  ([message error] (log/debug (:error error)) error))

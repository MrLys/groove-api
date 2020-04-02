(ns groove.db-test-utils
  (:require [groove.util.utils :refer [parseLong]]
            [toucan.models :as models]
            [toucan.db :as db]
            [clojure.java.jdbc :as j]
            [environ.core :refer [env]]))


(def pg-db {:dbtype (env :dbtype) 
            :dbname (env :dbname)
            :host "localhost"
            :subname (env :subname)
            :user  (env :dbuser)
            :password (env :password)
            :ssl false})

(defn test-long[l1 l2] 
  (= (parseLong l1) (parseLong l2)))

(defn clear [table]
  (j/execute! pg-db [(format "TRUNCATE public.%s CASCADE" (name table))]))
; Based on:
; https://gist.github.com/quux00/3606159#file-gistfile1-clj
(defn setup-db []
  (db/set-default-db-connection! pg-db)
  (models/set-root-namespace! 'groove.models))
(defn one-time-setup []
  (setup-db)
  (clear :user)
  (clear :habit))

(defn one-time-teardown []
  (clear :user)
  (clear :habit))

(defn once-fixture [f]
  (one-time-setup)
  (f)
  (one-time-teardown))

(defn setup []
  (clear :user)
  (clear :habit))

(defn teardown []
  (clear :user)
  (clear :habit))

(defn each-fixture [f]
  (setup)
  (f)
  (teardown))


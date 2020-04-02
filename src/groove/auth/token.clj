(ns groove.auth.token
  (:require [clj-time.core :as time]
            [clj-time.coerce :as c]
            [buddy.sign.jwt :as jwt]
            [environ.core :refer [env]]
            [schema-tools.core :as st]))

(defn create-token [user]
  (let [exp (time/plus (time/now) (time/days 15))
        formatted-user (-> user
                           (st/update-in [:username] str)
                           (st/update-in [:email] str)
                           (st/update-in [:id] str)
                           (st/assoc     :exp (c/to-long exp)))
        token-contents (st/select-keys formatted-user [:username :email :id :exp])]
    (jwt/sign token-contents (env :secret) {:alg :hs512})))

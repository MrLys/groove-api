(ns groove.core
  (:gen-class)
  (:require [compojure.api.sweet :refer [api context]]
            [groove.routes.groove :refer [groove-routes]]
            [groove.routes.user :refer [user-routes]]
            [clojure.tools.logging :as log]
            [groove.routes.habit :refer [habit-routes]]
            [toucan.db :as db]
            [toucan.models :as models]
            [groove.util.utils :refer [parseLong]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer :all]
            [ring.adapter.jetty :refer [run-jetty]]))


(def db-spec
  {:dbtype (env :dbtype)
   :dbname (env :dbname)
   :user (env :dbuser)
   :subame (env :subname)
   :password (env :password)})

(def swagger-config
  {:ui "/api/v1/docs"
   :spec "/swagger.json"
   :data {:info {:version "1.0.0", :title "RESTful groove API"
                 :securityDefinitions {:api_key {:type "apiKey" :name "Authorization" :in "header"}}}}})

(defn setup-db []
  (log/info (str "Setting up db " (:dbname db-spec)))
  (db/set-default-db-connection! db-spec)
  (models/set-root-namespace! 'groove.models))

(def app
  (api
    {:swagger swagger-config}
    (context "/api" [] groove-routes user-routes habit-routes)))

(defn -main
  [& args]
  (setup-db)
  (run-server app {:port (parseLong (env :port))})) ;; http-kit complains that it cannot parse from string to Number?

(defn -dev-main
  [& args]
  (db/set-default-db-connection! db-spec)
  (models/set-root-namespace! 'groove.models)
  (run-jetty (wrap-reload #'app) {:port (parseLong (env :port))}))

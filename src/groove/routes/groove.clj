(ns groove.routes.groove
  (:require [schema.core :as s]
            [toucan.db :as db]
            [groove.handlers.groove :refer :all]
            [groove.middleware :refer [wrap-token-auth]]
            [compojure.api.sweet :refer [GET POST PATCH]]
            [groove.util.validation :refer :all]))

(s/defschema GrooveRequestSchema
  {:owner_id (s/constrained s/Int valid-user-id?)
   :state (s/constrained s/Str valid-groove?)
   :user_habit_id (s/constrained s/Int valid-habit-id?)
   :date (s/constrained java.time.LocalDate valid-date?)})

(def groove-routes
  [(GET "/grooves/:userId/:habit_id"  [:as request]
            :header-params [authorization :- String]
            :tags ["grooves"]
            :middleware [wrap-token-auth]
            :path-params [userId :- Long, habit_id :- Long]
            :query-params [start_date :- java.time.LocalDate, end_date :- java.time.LocalDate]
            (get-grooves-handler request userId habit_id start_date end_date))
   (GET "/grooves/:userId"  [:as request]
            :header-params [authorization :- String]
            :tags ["grooves"]
            :middleware [wrap-token-auth]
            :path-params [userId :- Long]
            :query-params [start_date :- java.time.LocalDate, end_date :- java.time.LocalDate]
            (get-all-grooves-handler request userId start_date end_date))
   (PATCH "/groove" [:as request]
             :header-params [authorization :- String]
             :middleware [wrap-token-auth]
             :tags ["grooves"]
             :body [create-groove-req GrooveRequestSchema]
             (update-groove-handler create-groove-req request))])

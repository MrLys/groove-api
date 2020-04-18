(ns groove.routes.habit
  (:require [schema.core :as s]
            [groove.handlers.habit :refer :all]
            [groove.util.validation :refer :all]
            [groove.middleware :refer [wrap-token-auth]]
            [compojure.api.sweet :refer [GET POST PATCH]]))

(s/defschema HabitRequestSchema
  {:owner_id (s/constrained s/Int valid-habit-id?)
   :name (s/constrained s/Str valid-name?)})

(def habit-routes
  [(POST "/api/habit" [:as request]
         :tags ["Habits"]
         :header-params [authorization :- String]
         :middleware [wrap-token-auth]
         :body [create-habit-req HabitRequestSchema]
         (create-habit-handler create-habit-req request))

   (GET "/api/habits" [:as request]
        :header-params [authorization :- String]
        :tags ["Habits"]
        :middleware [wrap-token-auth]
        :query-params [start_date :- java.time.LocalDate, end_date :- java.time.LocalDate]
        (get-all-grooves-by-habits request start_date end_date))
   (GET "/api/habit/:user_habit_id" [:as request]
        :header-params [authorization :- String]
        :tags ["Habits"]
        :middleware [wrap-token-auth]
        :query-params [start_date :- java.time.LocalDate, end_date :- java.time.LocalDate]
        :path-params [user_habit_id :- Long]
        (get-all-grooves-by-habit request user_habit_id start_date end_date))])

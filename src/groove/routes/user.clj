(ns groove.routes.user
  (:require [groove.handlers.user :refer :all]
            [groove.util.validation :refer :all]
            [compojure.api.sweet :refer [GET POST PATCH]]
            [groove.middleware :refer [auth-credentials-reponse wrap-basic-auth wrap-token-auth]]
            [ring.util.http-response :refer [created ok]]
            [schema.core :as s]))

(s/defschema UserRequestSchema
  {:username (s/constrained s/Str valid-username?)
   :email (s/constrained s/Str valid-email?)
   :password (s/constrained s/Str valid-password?)})

(s/defschema ForgotPasswordRequestSchema
   {:password (s/constrained s/Str valid-password?)
    :token (s/constrained s/Str valid-password-token?)})

(def user-routes
  [(-> (GET "/api/all-data" [:as request]
             :tags ["User"]
             :header-params [authorization :- String]
             :middleware [wrap-token-auth]
             (get-all-data-handler request)))
   (-> (POST "/api/login" [:as request]
             :tags ["User"]
             :header-params [authorization :- String]
             :middleware [wrap-basic-auth]
             (auth-credentials-reponse request)))
   (POST "/api/register" []
         :tags ["User"]
         :body [user UserRequestSchema]
         (create-user user))
   (POST "/api/activate" []
         :tags ["User"]
         :query-params [activation-token :- String]
         (activate-user-handler activation-token))
   (POST "/api/forgot" []
         :tags ["User"]
         :query-params [email :- String]
         (forgot-password email))
   (POST "/api/update" []
         :tags ["User"]
         :body [request ForgotPasswordRequestSchema]
         (update-user-handler request))])


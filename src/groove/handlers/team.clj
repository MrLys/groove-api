(ns groove.handlers.team
  (:require [groove.handlers.response :refer [response-handler]]
            [groove.util.utils :refer [parseLong build-grooves-by-habits]]
            [groove.bulwark :as blwrk]))

(defn get-all-teams-by-user-id [req]
  (blwrk/get-all-teams req))

(defn get-all-teams-handler [req]
  (response-handler :GET get-all-teams-by-user-id req))

(defn get-all-team-members [req team]
  []) 

(defn get-all-team-members-handler [req team]
  (response-handler :GET get-all-team-members req team))

(defn create-team [req name]
  (blwrk/create-team req name))

(defn create-team-handler [req name]
  (response-handler :POST create-team req name))

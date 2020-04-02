(ns groove.handlers.response
  (:require [ring.util.http-response :refer [ok not-found created unauthorized]]
            [compojure.api.sweet :refer [GET POST PATCH PUT]]))

(defn- valid-response? [value]
  (and
    (not (empty? value))
    (not (nil? value))
    (nil? (:error value))))

(def ^:private handler-map {:GET (fn [v] (if (valid-response? v) (ok v) (not-found "Not found")))
                  :POST (fn [v] (if (valid-response? v) (created v) (unauthorized "Not authorized!")))
                  :PATCH (fn [v] (if (valid-response? v) (ok v) (unauthorized "Not authorized!")))
                  :PUT (fn [v] (if (valid-response? v) (ok v) (unauthorized "Not authorized!")))})
; Get the function or deciding the response
; call the getter/handler function with the args and pass the result to
; handler function from the map
(defn response-handler [req-type f & args]
  ((req-type handler-map) (apply f args)))

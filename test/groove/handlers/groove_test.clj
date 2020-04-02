(ns groove.handlers.groove-test
  (:require [clojure.test :refer :all]
            [groove.util.utils :refer [parseLong]]
            [groove.test-utils :refer :all]
            [groove.db-test-utils :refer :all]
            [groove.handlers.groove :as handler]
            [groove.bulwark :as blwrk]
            [groove.db :as db]))

(deftest update-groove-test
  "Update grooves"
  (let [req (create-user)
        user1 (:identity req)
        req2 (create-user 1)
        user2 (:identity req2)
        habit1 {:name "piano" :owner_id (parseLong (:id user1))}
        habit2 {:name "exercise" :owner_id (parseLong (:id user1))}
        habit3 {:name "exercise" :owner_id (parseLong (:id user2))}
        h1 (blwrk/create-habit habit1 req)
        h2 (blwrk/create-habit habit2 req)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)
        g1 (create-groove req h1 today -5 "success")
        g2 (create-groove req h1 today 1 "fail") ;; invalid
        g3 (create-groove req h1 today -4 "success")
        g4 (create-groove req h2 today -3 "success")
        g5 (create-groove req h2 today 0 "fail")
        g6 (create-groove req h2 today -22 "success")
        g7 (create-groove req2 h1 today -22 "success") ;; req 2 should not be able to create groove for habit 1.
        resp1 (handler/update-groove g1 req)
        resp2 (handler/update-groove g2 req) ;; should fail
        resp3 (handler/update-groove g3 req)
        resp4 (handler/update-groove g4 req)
        resp5 (handler/update-groove g5 req)
        resp6 (handler/update-groove g6 req)
        resp7 (handler/update-groove g7 req2)
        resp8 (handler/get-all-grooves-handler req user1 (.plusDays today -22) today)]
    (and
      (is (nil? (:error resp1)))
      (is (not (nil? (:error resp2))))
      (is (nil? (:error resp3)))
      (is (nil? (:error resp4)))
      (is (nil? (:error resp5)))
      (is (nil? (:error resp6)))
      (is (not (nil? (:error resp7))))
      (is (not (empty? resp8)))
      (is (= (.length resp8) 5)))))

(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)


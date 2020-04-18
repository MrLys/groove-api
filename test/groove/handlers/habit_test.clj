(ns groove.handlers.habit-test
  (:require [clojure.test :refer :all]
            [groove.util.utils :refer [parseLong]]
            [groove.test-utils :refer :all]
            [groove.db-test-utils :refer :all]
            [groove.handlers.habit :as handler]
            [groove.handlers.groove :as groove]
            [groove.bulwark :as blwrk]
            [groove.db :as db]))

(deftest create-habit-test
  "Update grooves"
  (let [req (create-user)
        user1 (:identity req)
        req2 (create-user 1)
        user2 (:identity req2)
        habit1 {:name "piano" :owner_id (parseLong (:id user1))}
        habit2 {:name "exercise" :owner_id (parseLong (:id user1))}
        habit3 {:name "running" :owner_id (parseLong (:id user1))}
        h1 (blwrk/create-habit habit1 req)
        h2 (blwrk/create-habit habit2 req)
        h3 (handler/create-habit-handler habit3 req)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)
        g1 (create-groove req h1 today -5 "success")
        g2 (create-groove req h1 today 1 "fail") ;; invalid
        g3 (create-groove req h1 today -4 "success")
        g4 (create-groove req h2 today -3 "success")
        g5 (create-groove req h2 today 0 "fail")
        g6 (create-groove req h2 today -22 "success")
        g7 (create-groove req2 h1 today -22 "success") ;; req 2 should not be able to create groove for habit 1.
        g8 (create-groove req h1 today -23 "success") ;; should not be included in the request for getting all grooves (resp8)
        resp1 (groove/update-groove g1 req)
        resp2 (groove/update-groove g2 req) ;; should fail
        resp3 (groove/update-groove g3 req)
        resp4 (groove/update-groove g4 req)
        resp5 (groove/update-groove g5 req)
        resp6 (groove/update-groove g6 req)
        resp7 (groove/update-groove g7 req2)
        _ (groove/update-groove g7 req) ; should not be included
        resp8 (handler/get-all-grooves-by-habits req (.plusDays today -22) today)]
    (and
      (is (nil? (:error resp1)))
      (is (not (nil? (:error resp2))))
      (is (nil? (:error resp3)))
      (is (nil? (:error resp4)))
      (is (nil? (:error resp5)))
      (is (nil? (:error resp6)))
      (is (not (nil? (:error resp7))))
      (is (not (empty? resp8)))
      (is (nil? (:error h3)))
      (is (not (empty? resp8)))
      (is (= 3 (count (keys (:body resp8)))))
      (is (= (:name ((keyword (:name habit1)) (:body resp8))) (:name habit1)))
      (is (= (:name ((keyword (:name habit2)) (:body resp8))) (:name habit2)))
      (is (= (:name ((keyword (:name habit3)) (:body resp8))) (:name habit3)))
      (is (= 2 (count (:grooves ((keyword (:name habit1)) (:body resp8))))))
      (is (= 3 (count (:grooves ((keyword (:name habit2)) (:body resp8))))))
      (is (= 0 (count (:grooves ((keyword (:name habit3)) (:body resp8)))))))))




(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)


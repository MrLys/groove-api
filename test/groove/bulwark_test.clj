(ns groove.bulwark-test
  (:require [clojure.test :refer :all]
            [groove.util.utils :refer [parseLong create-activation-token]]
            [groove.bulwark :as blwrk]
            [groove.db-test-utils :refer :all]
            [groove.test-utils :refer :all]
            [groove.db :as db]))


(defn- each-fixture-wrapper [])


(defn- generate-grooves [user habit start-date end-date]
  "Generates some grooves for a habit for a date range")


(deftest create-habit-test
  "Test creation of a habit"
  (let [req (create-user)
        user (:identity req)
        habit {:name "piano" :owner_id (parseLong (:id user))}
        response (blwrk/create-habit habit req)
        db-habit (db/get-habit-by-name (:name habit))]
    (and
      (is (not (nil? db-habit)))
      (is (not (nil? (db/get-user-habit (:id user) (:id db-habit)))))
      ;(is (= (str (:status response)) "200")) ;; http status ok
      (is (test-long (:habit_id response) (:id db-habit))) ; correct habit id
      (is (test-long (:owner_id response) (:id user)))  ; correct user id
      (is (test-long (:habit_id response) (:habit_id (db/get-user-habit (:id user) (:id db-habit)))))
      (is (test-long (:owner_id response) (:owner_id (db/get-user-habit (:id user) (:id db-habit))))))))

(deftest get-habit-test
  "Test creation of a habit"
  (let [req1 (create-user 1)
        user1 (:identity req1)
        req2 (create-user 2)
        user2 (:identity req2)
        habit1 {:name "piano" :owner_id (parseLong (:id user1))}
        habit2 {:name "exercise" :owner_id (parseLong (:id user2))}
        _ (blwrk/create-habit habit1 req1)
        _ (blwrk/create-habit habit2 req2)]
    (and
      ; User1 should not have access to habit2
      ; User2 should not have access to habit1
      (is (nil? (db/get-user-habit (:id user2) (:id (db/get-habit-by-name (:name habit1))))))
      (is (nil? (db/get-user-habit (:id user1) (:id (db/get-habit-by-name (:name habit2))))))
      (is (= (count (blwrk/get-habits req1)) 1))
      (is (= (count (blwrk/get-habits req2)) 1)))))

(deftest create-duplicate-habits
  "Create a duplicate habit"
  (let [req (get-mock-request {:password "password" :username "test@test.no" :email "test@test.no"})
        user (:identity req)
        habit1 {:name "exercise" :owner_id (parseLong (:id user))}
        habit2 {:name "piano" :owner_id (parseLong (:id user))}
        _ (blwrk/create-habit habit1 req)
        _ (blwrk/create-habit habit2 req)
        habits (blwrk/get-habits req)
        habit3 (blwrk/create-habit habit1 req)]
    (and
      (is (nil? (:error habits)))
      (is (= (count habits) 2))
      (is (not (nil? (:error habit3)))))))


(deftest update-groove-test
  "Test updating/creating grooves.
  Also tests get by dates of grooves
  Creates two users.
  Creates two habits for user 1
  Updates three grooves per habit
  Verify
  Update again."
  (let [req1 (create-user)
        user1 (:identity req1)
        req2 (create-user 2)
        user2 (:identity req2)
        habit1 {:name "exercise" :owner_id (parseLong (:id user1))}
        habit2 {:name "piano" :owner_id (parseLong (:id user1))}
        resp1 (blwrk/create-habit habit1 req1)
        resp2 (blwrk/create-habit habit2 req1)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)
        g1 (create-groove req1 resp1 today -1 "success")
        _ (blwrk/update-groove g1 req1)
        g2 (create-groove req1 resp1 today -4 "success")
        _ (blwrk/update-groove g2 req1)
        g3 (create-groove req1 resp1 today -5 "fail")
        _ (blwrk/update-groove g3 req1)
        g4 (create-groove req1 resp2 today 0 "success")
        _ (blwrk/update-groove g4 req1)
        g5 (create-groove req1 resp2 today -2 "success")
        _ (blwrk/update-groove g5 req1)
        g6 (create-groove req1 resp2 today -3 "fail")
        _ (blwrk/update-groove g6 req1)
        user1-grooves-habit1 (blwrk/get-by-dates req1 (parseLong (:id resp1)) (.plusDays today -6) today)
        user1-grooves-habit2 (blwrk/get-by-dates req1 (parseLong (:id resp2)) (.plusDays today -6) today)
        user2-grooves-habit1 (blwrk/get-by-dates req2 (parseLong (:id resp1)) (.plusDays today -6) today)]
    (println user1-grooves-habit1)
    (and
      (is (= (count user1-grooves-habit1) 3))
      (is (= (count user1-grooves-habit2) 3))
      (is (= (count user2-grooves-habit1) 0))
      (is (= (:state (nth user1-grooves-habit1 2) (:state g1))))
      (is (= (:state (nth user1-grooves-habit1 1) (:state g2))))
      (is (= (:state (nth user1-grooves-habit1 0) (:state g3))))
      (is (= (:state (nth user1-grooves-habit2 2) (:state g4))))
      (is (= (:state (nth user1-grooves-habit2 1) (:state g5))))
      (is (= (:state (nth user1-grooves-habit2 0) (:state g6))))
      (do
        (blwrk/update-groove (create-groove req1 resp1 today -1 "fail") req1)
        (let [updated (blwrk/get-by-dates req1 (parseLong (:id resp1)) (.plusDays today -6) today)]
          (is (= (count updated) 3))
          (is (= (:state (nth updated 2)) "fail"))
          (is (= (:state (nth updated 1)) (:state g2)))
          (is (= (:state (nth updated 0)) (:state g3))))))))

(deftest get-all-by-dates-test
  "Test getting grooves by date"
  (let [req1 (create-user)
        user1 (:identity req1)
        req2 (create-user 2)
        user2 (:identity req2)
        habit1 {:name "exercise" :owner_id (parseLong (:id user1))}
        habit2 {:name "piano" :owner_id (parseLong (:id user1))}
        resp1 (blwrk/create-habit habit1 req1)
        resp2 (blwrk/create-habit habit2 req1)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)
        g1 (create-groove req1 resp1 today -1 "success")
        _ (blwrk/update-groove g1 req1)
        g2 (create-groove req1 resp1 today -4 "success")
        _ (blwrk/update-groove g2 req1)
        g3 (create-groove req1 resp1 today -5 "fail")
        _ (blwrk/update-groove g3 req1)
        g4 (create-groove req1 resp2 today 0 "success")
        _ (blwrk/update-groove g4 req1)
        g5 (create-groove req1 resp2 today -2 "success")
        _ (blwrk/update-groove g5 req1)
        g6 (create-groove req1 resp2 today -3 "fail")
        _ (blwrk/update-groove g6 req1)
        grooves (blwrk/get-all-by-dates req1 (.plusDays today -6) (.plusDays today 1))]
    (println (nth grooves 0))
    (println (str "user_habit_id of grooves[0] " (:user_habit_id (nth grooves 0))))
    (println resp1)
    (and
      (is (= (count grooves) 6))
      (is (test-long (:owner_id (nth grooves 0)) (:id user1)))
      (is (reduce #(and %1 (test-long (:owner_id %2) (:id user1))) grooves)) ; all grooves belong to user1
      (is (reduce #(and %1 (or (test-long (:user_habit_id %2) (:id resp1)) (test-long (:user_habit_id %2) (:id resp2)))) grooves))))) ; all should be of user_habit1 or 2

(deftest get-all-grooves-by-date-range-test
  (let [req1 (create-user)
        user1 (:identity req1)
        req2 (create-user 2)
        user2 (:identity req2)
        habit1 {:name "exercise" :owner_id (parseLong (:id user1))}
        habit2 {:name "piano" :owner_id (parseLong (:id user1))}
        habit3 {:name "reading" :owner_id (parseLong (:id user2))}
        resp1 (blwrk/create-habit habit1 req1)
        resp2 (blwrk/create-habit habit2 req1)
        resp3 (blwrk/create-habit habit3 req2)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)
        g1 (create-groove req1 resp1 today -1 "success")
        _ (blwrk/update-groove g1 req1)
        g2 (create-groove req1 resp1 today -4 "success")
        _ (blwrk/update-groove g2 req1)
        g3 (create-groove req1 resp1 today -5 "fail")
        _ (blwrk/update-groove g3 req1)
        g4 (create-groove req1 resp2 today 0 "success")
        _ (blwrk/update-groove g4 req1)
        g5 (create-groove req1 resp2 today -2 "success")
        _ (blwrk/update-groove g5 req1)
        g6 (create-groove req1 resp2 today -3 "fail")
        _ (blwrk/update-groove g6 req1)
        g7 (create-groove req2 resp3 today -3 "fail")
        _ (blwrk/update-groove g7 req2)
        all (blwrk/get-all-grooves-by-date-range req1 (.plusDays today -6) (.plusDays today 1))]
    (and
      (is (= (count all) 6))
      (is (reduce #(and %1 (test-long (:owner_id %2) (:id user1))) true all)) ; all grooves belong to user1
      (is (reduce #(and %1 (or (test-long (:user_habit_id %2) (:id resp1)) (test-long (:user_habit_id %2) (:id resp2)))) true all))))) ; all should be of user_habit1 or 2


(deftest activation-token-test
  "Try creating an activation token
  and check its validity"
  (let [token1 (create-activation-token)
        token2 (create-activation-token)
        req (create-user)
        valid-token (blwrk/new-activation-token! token1 (:id (:identity req)) (.plusDays (java.time.LocalDate/now java.time.ZoneOffset/UTC) 1))
        resp (blwrk/activate-user token1)]
    (and
     (is (nil? (:error resp)))
     (is resp)
     (is (not (nil? (:error (blwrk/activate-user token2))))))))


(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)

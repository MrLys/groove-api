(ns groove.db-test
  (:require [clojure.test :refer :all]
            [groove.db :as db]
            [groove.db-test-utils :refer :all]))


(deftest new-user-test
  "Create a new user, check that it \"persist\""
  (let [user {:password "password" :username "test@test.no" :email "test@test.no"}
        db-user (db/new-user! user)
        in-db (db/get-registered-user-by-field :username "test@test.no")]
    (testing "Is the user created, and does the user have the correct values?"
      (and
        (is (:username db-user) (:username in-db))
        (is (:email db-user) (:email in-db))
        (is (:id db-user) (:id in-db))))))

(deftest new-existing-user-test
  "Create a new user that already exists"
  (let [user {:password "password" :username "test@test.no" :email "test@test.no"}
        db-user (db/new-user! user)]
      (is (thrown? org.postgresql.util.PSQLException (db/new-user! user)))))

(deftest get-all-users-empty
  "Get all users in empty db and assert the number is correct"
  (is (= 0 (count (db/get-all-users)))))

(deftest get-all-users
  "Get all users in empty db and assert the number is correct"
  (db/new-user! {:password "password" :username "test@test.no" :email "test@test.no"})
  (db/new-user! {:password "password" :username "test2@test.no" :email "test2@test.no"})
  (db/new-user! {:password "password" :username "test3@test.no" :email "test3@test.no"})
  (is (= 3 (count (db/get-all-users)))))

(deftest create-habit-test
  "Create a habit, verify it in db"
  (let [db-habit (db/create-habit "exercising")
        in-db (db/get-habit-by-name "exercising")]
    (and
      (is (= (:name db-habit) (:name in-db)))
      (is (= (:id db-habit) (:id in-db))))))

(deftest create-existing-habit-test
  "Create a habit, verify it in db"
  (let [db-habit (db/create-habit "exercising")]
    (is (thrown? org.postgresql.util.PSQLException (db/create-habit "exercising")))))

(deftest activate-user-test
  "Create a user and activate it in the db"
  (let [user (db/new-user! {:password "password" :username "test@test.no" :email "test@test.no"})
        updated? (db/activate-user (:id user))
        db-user (db/get-user (:id user))]
    (and (is updated?)
         (is (:activated db-user)))))

(deftest create-user-habit
  (let [user (db/new-user! {:password "password" :username "test@test.no" :email "test@test.no"})
        exercise (db/create-habit "exercising")
        user-habit (db/create-user-habit exercise (:id user))]
    (and
      (is (= (:id exercise) (:habit_id user-habit)))
      (is (= (:id user) (:owner_id user-habit))))))


(deftest create-existing-user-habit
  "Try to create a user and then the same user-habit twice"
  (let [_ (setup-db)
        user (db/new-user! {:password "password" :username "test@test.no" :email "test@test.no"})
        exercise (db/create-habit "exercising")
        user-habit (db/create-user-habit  exercise (:id user))]
    (is (thrown? org.postgresql.util.PSQLException (db/create-user-habit exercise (:id user))))))

(deftest get-habits-by-userid-test
  "Create a user and two habits, assert that the correct values are
  stored in the db"
  (let [user (db/new-user! {:password "password" :username "test@test.no" :email "test@test.no"})
        habit1 (db/create-habit "exercising")
        user-habit1 (db/create-user-habit habit1 (:id user))
        habit2 (db/create-habit "piano")
        user-habit2 (db/create-user-habit habit2 (:id user))
        habits (db/get-all-habits-by-user-id (:id user))]
    (and
      (is (= (count habits) 2))
      (is (or (= (:id (nth habits 0)) (:id user-habit1))
              (= (:id (nth habits 0)) (:id user-habit2))))
      (is (or (= (:name (nth habits 0)) (:name habit1))
              (= (:name (nth habits 0)) (:name habit2))))
      (is (or (= (:id (nth habits 1)) (:id user-habit1))
              (= (:id (nth habits 1)) (:id user-habit2))))
      (is (or (= (:name (nth habits 0)) (:name habit1))
              (= (:name (nth habits 0)) (:name habit2)))))))

(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)

(ns groove.handlers.user-test
  (:require [clojure.test :refer :all]
            [groove.util.utils :refer [parseLong create-activation-token]]
            [groove.util.validation :refer :all]
            [groove.bulwark :as blwrk]
            [groove.db-test-utils :refer :all]
            [groove.handlers.user :as handler]
            [groove.handlers.habit :as habit-handler]
            [groove.handlers.groove :as groove-handler]
            [groove.handlers.team :as team-handler]
            [groove.test-utils :refer :all]
            [groove.db :as db]))



(deftest create-user-and-password-token
  "Test creation of a user, activation,
   forgot password password, then update password. With handlers"
  (let [request {:password "password" :username "test@test.no" :email "test@test.no"}
        user (handler/create-user request)
        activation-token (db/get-token-by-user-id (:id user))
        token (handler/forgot-password-handler (:email request))]
    (and
      (is (nil? (:error user)))
      (is (> (:id user) 0))
      (is (nil? (:error activation-token)))
      (is (valid-token? activation-token))
      (is (blwrk/activate-user activation-token))
      (is (nil? (:error token)))
      (is (valid-token? token))
      (is (= (:user_id token) (:id user)))
      (is (not (:user token)))
      (is (handler/update-user-password {:user_id (:id user) :password (:password "e8ms2cu1fese7h8k6fcog")})))))

(deftest get-all-data
  "Creates some data, uses get-all-data to verify all data is present"
  (let [user (handler/create-user {:password "password" :username "test@test.no" :email "test@test.no"})
        r {:identity user}
        user2 (handler/create-user {:password "password" :username "test2@test.no" :email "test2@test.no"})
        r2 {:identity user2}
        activation-token (db/get-token-by-user-id (:id user))
        _ (blwrk/activate-user activation-token)
        habit1 {:name "piano" :owner_id (parseLong (:id user))}
        habit2 {:name "exercise" :owner_id (parseLong (:id user))}
        habit3 {:name "exercise" :owner_id (parseLong (:id user2))}
        h1 (blwrk/create-habit habit1 r)
        h2 (blwrk/create-habit habit2 r)
        h3 (blwrk/create-habit habit3 r2)
        today (java.time.LocalDate/now java.time.ZoneOffset/UTC)
        g1 (create-groove r h1 today -5 "success")
        g2 (create-groove r h1 today -4 "success")
        g3 (create-groove r h2 today -3 "success")
        g4 (create-groove r h2 today 0 "fail")
        g5 (create-groove r h2 today -22 "success")
        g6 (create-groove r2 h3 today -1 "success")
        _ (groove-handler/update-groove g1 r)
        _ (groove-handler/update-groove g2 r)
        _ (groove-handler/update-groove g3 r)
        _ (groove-handler/update-groove g4 r)
        _ (groove-handler/update-groove g5 r)
        _ (groove-handler/update-groove g6 r2)
        t1 (team-handler/create-team r "Superteam")
        t2 (team-handler/create-team r2 "Not so good team")
        all-data (handler/get-all-data r)]
    (println all-data)
    (and
      (is (nil? (:error all-data)))
      (is (= (:id (:user all-data)) (:id user)))
      (is (= (:username (:user all-data) (:username user))))
      (is (= (:email (:user all-data) (:email user))))
      (is (= (count (:teams (:user all-data))) 1))
      (is (= (:name (:teams (:user all-data))) (:name t1)))
      (is (= (count (:habits (:user all-data))) 2))
      (is (= (:name ((keyword (:name habit1)) (:habits (:user all-data)))) (:name habit1)))
      (is (= (:name ((keyword (:name habit2)) (:habits (:user all-data)))) (:name habit2)))
      (is (= (count (:grooves ((keyword (:name habit1)) (:habits (:user all-data))))) 2))
      (is (= (count (:grooves ((keyword (:name habit2)) (:habits (:user all-data))))) 3)))))


(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)

(ns groove.mail
  (:require [environ.core :refer [env]]))
;; Credit must be given where credit is due!
;; This is shamelessly taken from https://nakkaya.com/2009/11/10/using-java-mail-api-from-clojure/
;(defn mail [& m]
;  (let [mail (apply hash-map m)
;        props (java.util.Properties.)]
;
;    (doto props
;      (.setProperty "mail.smtp.host" "mail.smtpbucket.com")
;      (.setProperty "mail.smtp.port" "8025"))
;
;    (if (= (:ssl mail) true)
;      (doto props
;        (.setProperty "mail.smtp.socketFactory.class"
;                      "javax.net.ssl.SSLSocketFactory")
;        (.setProperty "mail.smtp.socketFactory.fallback" "false")
;        (.setProperty "mail.smtp.socketFactory.port" (str (:port mail)))
;        (.put "mail.smtp.starttls.enable" "true")))
;
;    (let  [session (javax.mail.Session/getDefaultInstance props)
;           msg     (javax.mail.internet.MimeMessage. session)
;           recipients (reduce #(str % "," %2) (:to mail))]
;
;      (.setFrom msg (javax.mail.internet.InternetAddress. "noreply@rutta.no"))
;
;      (.addRecipient msg
;                     (javax.mail.Message$RecipientType/TO)
;                     (javax.mail.internet.InternetAddress. (str (:to mail))))
;
;      (.setSubject msg (:subject mail))
;      (.setContent msg (:text mail) "text/html")
;      (javax.mail.Transport/send msg))))
(defn mail [& m]
  (let [mail (apply hash-map m)
        props (java.util.Properties.)]

    (doto props
      (.setProperty "mail.smtp.host" (:mailhost env))
      (.setProperty "mail.smtp.port" (:mailport env)))

    (if (= (:ssl mail) true)
      (doto props
        (.setProperty "mail.smtp.socketFactory.class"
                      "javax.net.ssl.SSLSocketFactory")
        (.setProperty "mail.smtp.socketFactory.fallback" "false")
        (.setProperty "mail.smtp.socketFactory.port" (str (:port mail)))
        (.put "mail.smtp.starttls.enable" "true")
        (.put "mail.smtp.auth" "true")
        (.put "mail.debug" (:maildebug env))
        (.put "mail.debug.auth" (:maildebugauth env))
        (.put "mail.store.protocol" "pop3")
        (.put "mail.transport.protocol" "smtp")))

    (let  [authenticator (proxy [javax.mail.Authenticator] []
                           (getPasswordAuthentication []
                             (javax.mail.PasswordAuthentication. 
                               (:user mail) (:password mail))))
           session (javax.mail.Session/getDefaultInstance props authenticator)
           msg     (javax.mail.internet.MimeMessage. session)
           recipients (:to mail)]

      (.setFrom msg (javax.mail.internet.InternetAddress. (:user mail)))

      (.addRecipient msg
                     (javax.mail.Message$RecipientType/TO)
                     (javax.mail.internet.InternetAddress. (str (:to mail))))

      (.setSubject msg (:subject mail))
      (.setContent msg (:text mail) "text/html")
      (javax.mail.Transport/send msg))))


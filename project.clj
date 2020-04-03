(defproject groove "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://rutta.heroku.com"
  :min-lein-version "2.0.0"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ; web
                 [prismatic/schema "1.1.9"]
                 [metosin/compojure-api "2.0.0-alpha26"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring "1.7.1"]
                 [clj-http "3.10.0"]
                 [javax.mail/javax.mail-api "1.6.2"]
                 [com.sun.mail/javax.mail "1.6.2"]
                 [com.sun.mail/smtp "1.6.3"]
                 [toucan "1.1.9"]
                 [clj-time "0.15.2"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [metosin/schema-tools "0.12.0"]
                 [org.postgresql/postgresql "42.2.4"]
                 [buddy/buddy-hashers "1.4.0"]
                 [buddy/buddy-auth "2.2.0"]
                 [http-kit "2.3.0"]
                 [ring-cors "0.1.13"]
                 ;logging 
                 [org.clojure/tools.logging "0.6.0"]
                 ;environment
                 [environ "1.1.0"]]
  :plugins [[lein-environ "1.1.0"]
            [lein-cloverage "1.1.2"]]
  :main ^:skip-aot groove.core
  :target-path "target/%s"
  :profiles { :dev [:project/dev :profiles/dev]
             :test [:project/test :profiles/test]
        	     ;; only edit :profiles/* in profiles.clj
             :profiles/dev  {}
             :profiles/test {}
             :project/dev {:source-paths ["src" "tool-src"]
                           :dependencies [[midje "1.6.3"]]
                           :plugins [[lein-auto "0.1.3"]]
                           }
             :project/test {}
             :uberjar {:main groove.core :aot :all :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]}}
  )

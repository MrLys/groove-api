{:profiles/dev
  {:env
   {:dbtype "postgres"
    :subname "//localhost:5432/groove_api"
    :password "password"
    :secret "password"
    :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]
    :port 3000}}
  :profiles/test
  {:env
   {:dbtype "postgres"
    :dbnametest "groove_api"
    :dbusertest "Thomas"
    :passwordtest "password"
    :subname "//localhost:5432/groove_api"
    :secret "password"
    :istest true
    :origin "http://localhost:8080"
    :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]
    :port 3000}
   :main groove.core/-dev-main}}



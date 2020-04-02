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
    :password "password"
    :subname "//localhost:5432/groove_api"
    :secret "password"
    :istest true
    :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]
    :port 3000}}}



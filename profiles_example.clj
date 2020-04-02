{:profiles/dev
  {:env
   {:dbtype "postgres"
    :dbname "name of db"
    :user   "postgres role" ;; run `psql postgres` and then `SELECT * FROM pg_roles;`
    :password "password"    ;; db password
    :secret "password"      ;; secret for application
    :port 3000}}}
 
  

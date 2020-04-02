psql -U $DBUSER -d $DBNAME -a -f clear_tables.sql
psql -U $DBUSER -d $DBNAME -a -f create_tables.sql
sh ../../create_test_user.sh

# all2us

A url shortening web service

## Development Environment Setup

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

You'll also need a postgresql database server. You can use pallet to
set one up. By default, pallet will use virtualbox. Run this to setup
a virtual machine configured with apache, postgres, and the all2us
webapp service:

    lein uberjar
    lein pallet up

Then, run the db migrations to create the database tables. The db
migrations assume the database server's hostname is 'postgres', so
first add the following 2 lines to your local hosts file:

Make sure to update your hosts file with the ip address of the vm (use
the ip address of the virtual machine that was just created using pallet)

    192.168.59.133 postgres
    192.168.59.133 all2.us

Then, use lein ragtime to run the migrations

    lien do clean, ragtime migrate

If all goes well, you can browse to `http://all2.us` and see the app. 

## Known issues

When running `lein ragtime migrate` I've seen this:
    Exception in thread "main" java.lang.RuntimeException: No such var: sql/with-connection, compiling:(ragtime/sql/database.clj:22:3)
	    at clojure.lang.Compiler.analyze(Compiler.java:6464)

Looks like a known issue with ragtime:
https://github.com/weavejester/ragtime/issues/46

To fix, do a `lein do clean, ragtime migrate`

## License

Copyright © 2015 Dave Paroulek

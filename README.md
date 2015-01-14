# all2us

A url shortening service

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

You'll also need a postgresql database server. You can use pallet to
set one up. By default, pallet will use virtualbox. Run this command
to create a virtual server configured with apache and postgres:

    lein pallet up

## Running

Use pallet to setup a virtual server with:

- apache installed and configured with all2.us virtualhost
- postgres installed with all2us db and user configured

Make sure to update your hosts file with the ip address of the vm:

    192.168.59.133 postgres
    192.168.59.133 all2.us

After setting up db for the first time, remember to create the db
tables:

    lein ragtime migrate

Then you should be able to access the site here:

    http://all2.us

To run the service locally, use:

    lein run

Or build and run an uberjar:

    lein uberjar
    java -jar ./target/all2us.jar

## Known issues

When running `lein ragtime migrate` I've seen this:
    Exception in thread "main" java.lang.RuntimeException: No such var: sql/with-connection, compiling:(ragtime/sql/database.clj:22:3)
	    at clojure.lang.Compiler.analyze(Compiler.java:6464)

Looks like a known issue with ragtime:
https://github.com/weavejester/ragtime/issues/46

To fix, do a `lein do clean, ragtime migrate`

## License

Copyright © 2015 Dave Paroulek

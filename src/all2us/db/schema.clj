(ns all2us.db.schema)

;; Make sure that hostname 'postgres' resolves to your postgres server

;; To create db user, connect to psql and run:
;; CREATE ROLE all2us LOGIN PASSWORD 'all2us';
;; CREATE DATABASE all2us OWNER all2us

(def db-spec
  {:subprotocol "postgresql"
   :subname "//postgres/all2us"
   :user "all2us"
   :password "all2us"})



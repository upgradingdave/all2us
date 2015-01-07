(ns all2us.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [all2us.db.schema :as schema]
            [upgradingdave.url :as url]))

(defdb db schema/db-spec)

(defentity urls)

(defn create-url 
  "pass a long url, and this will create a new record in the database
  and return a short url"
  [url]
  (let [{:keys [id url] :as rec} (insert urls (values {:url url}))
        short-url (url/encode id)]
    (update-url id url short-url)
    (assoc rec :short_url short-url)))

(defn update-url [id url short-url]
  (update urls
  (set-fields {:url url
               :short_url short-url})
  (where {:id id})))

(defn get-url [id]
  (first (select urls
                 (where {:id id})
                 (limit 1))))

(defn find-urls 
  "Options: 
    :limit -> limit number records returned
    :order -> a field name (keyword) to order results
    :offest -> number or rows to skip
  "
  [& [{max :limit orderby :order :as options :or {orderby :lastmodified}}]]
  (select urls
          (limit max)
          (order orderby)))

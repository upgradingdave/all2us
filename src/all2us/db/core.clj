(ns all2us.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [all2us.db.schema :as schema]
            [upgradingdave.url :as url]))

(defdb db schema/db-spec)

(defentity urls)

(defn update-url [id url short-url]
  (update urls
  (set-fields {:url url
               :short_url short-url})
  (where {:id id})))

(defn create-url 
  "pass a long url, and this will create a new record in the database
  and return a short url"
  [url]
  (let [{:keys [id url] :as rec} (insert urls (values {:url url}))
        short-url (url/encode id)]
    (update-url id url short-url)
    (assoc rec :short_url short-url)))

(defn get-url [id]
  (first (select urls
                 (where {:id id})
                 (limit 1))))

(defn find-urls 
  "Options: 
    :limit -> limit number records returned
    :order -> a field name (keyword) to order results
    :asc -> true for ascending (default) or false for descending
    :offest -> number or rows to skip
  "
  [& [{asc :asc max :limit orderby :order idx :offset
       :as options 
       :or {asc true 
            orderby :lastmodified
            idx 0}}]]
  (select urls
          (limit max)
          (order orderby (if asc :ASC :DESC))
          (offset idx)))

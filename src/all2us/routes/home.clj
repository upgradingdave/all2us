(ns all2us.routes.home
  (:require [compojure.core :refer :all]
            [all2us.layout :as layout]
            [all2us.util :as util]
            [all2us.db.core :as db]
            [upgradingdave.url :as url]
            [noir.response :as resp]
            [hiccup.core :as h]
            [noir.validation :as v]
))

(defn check-url! 
  "Perform a number of validation checks to make sure url passed is
  valid. *errors* dynamic variable that is bound to the request is
  updated if any errors are found. This method returns true or false.
  Use (v/get-errors :url) to get the actual error if this returns
  false"
  [url]
  (v/rule (v/has-value? url)
          [:url "A url is required"]))

(defn create-form 
  "generate the html needed for the create url form"
  []
  (h/html 
   [:form {:method "post" :action "/urls" :id "url-form"} 
    [:div {:class "form-group"}
     [:label {:for "url" :id "url-label"} "Url to Shorten"]
     [:input {:class "form-control" :type "text" :name "url" :id "url"}]]
    [:button {:class "btn btn-default" :type "submit"} "Shorten this Url"]
]))

(defn how-short? 
  "calculate difference between url and short url"
  [{u :url s :short_url}]
  (let [diff (- (count u) (count (str "http://all2.us/" s)))]
    (if (<= diff 0)
      ""
      (str "(Shorter by " diff " letters!) "))))

(defn home-page []
  (layout/render
    "home.html" {:create-form (create-form)
                 :urls (map #(assoc % :diff (how-short? %)) 
                            (db/find-urls {:limit 7 :asc false}))}))

(defn create-url [url]
  (check-url! url)
  (if (v/errors? :url)
    (resp/status 400 (resp/json {:errors (v/get-errors)}))
    (resp/json (db/create-url url))))

(defroutes home-routes
  (GET "/" [] (home-page))

  ;; Some simple user interfaces
  (GET "/urls/create" [] (create-form))

  ;; REST API
  (POST "/urls" [url] (create-url url))

  (GET "/urls" {{limit :limit asc :asc offset :offset order :order} :params}
       (let [limit (if limit (Integer/parseInt limit) 50)
             asc (if asc (Boolean/parseBoolean asc) asc)
             offset (if offset (Integer/parseInt offset) offset)
             order (keyword order)
             options {:limit limit :asc asc :offset offset :order order}]
         (resp/json (db/find-urls 
                     (into {} (filter (fn [[k v]] (not (nil? v)) ) options))))))

  (GET "/urls/:url" [url] (resp/json (db/get-url (url/decode url))))

  ;; Redirects
  ;; this needs to be last so it matches when nothing else matched
  (GET "/:url" [url] (if-let [url (:url (db/get-url (url/decode url)))] 
                       (resp/redirect url))))

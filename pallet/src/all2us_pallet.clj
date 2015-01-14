(ns all2us-pallet
    "Code to install and configure services necessary for all2us"
    (:use [pallet.repl])
    (:require
     [pallet.actions :refer [package-manager remote-file directory]]
     [pallet.api :refer [group-spec server-spec node-spec plan-fn]]
     [pallet.crate.automated-admin-user :refer [automated-admin-user]]
     [pallet.actions :refer [user exec exec-script]]
     [pallet.crate.postgres :as postgres]
     [httpd.crate.httpd :as httpd]
     [pallet.crate.etc-hosts :as hosts]
     [pallet.crate.java :as java]
    ))

(def ^:dynamic *deploy-path* "/home/dparoulek/apps/all2.us")

(def ^:dynamic *deploy-user* "dparoulek")

;;TODO: turn this into a function or use pallet.crate.service
(def ^:dynamic *upstart-content*
  (str
   "description \"all2us\"" \newline
   "author \"upgradingdave\"" \newline
   \newline
   "start on runlevel [2345]" \newline
   "stop on runlevel [!2345]" \newline
   \newline
   "expect fork" \newline
   \newline
   "script" \newline
   "    cd /home/dparoulek/apps/all2.us" \newline
   "    sudo -u dparoulek java -jar /home/dparoulek/apps/all2.us/all2us.jar >/home/dparoulek/apps/all2.us/all2us.log 2>&1" \newline
   "    emit all2us_running" \newline
   "end script" \newline))

(def default-node-spec
  (node-spec
   :image {:image-id :ubuntu-14.04
           :os-family :ubuntu}
   :hardware {:min-cores 1}))

(def
  ^{:doc "Setup some misc stuff on the server"}
  base-server
  (server-spec
   :phases
   {:bootstrap (plan-fn 
                ;; allows login with pki
                (automated-admin-user)
                ;; refresh apt-get
                (package-manager :update))
    :configure (plan-fn 
                ;; setup postgres user
                (user "postgres")
                ;; setup etc/hosts
                (exec 
                 nil 
                 "sudo sh -c 'echo \"127.0.0.1 postgres\" >> /etc/hosts'")
                ;; deploy application uberjar
                (directory *deploy-path* 
                           :owner *deploy-user* 
                           :group *deploy-user*)
                (remote-file (str *deploy-path* "/all2us.jar") 
                             :local-file "target/all2us.jar"
                             :owner *deploy-user*
                             :group *deploy-user*)
                ;; setup upstart to controll all2us as service
                (remote-file "/etc/init/all2us.conf"
                             :owner "root"
                             :group "root"
                             :mode 644
                             :content *upstart-content*
                             :literal true))}))

(def
  ^{:doc "Define a server spec for installing and setting up httpd"}
  httpd-server
  (server-spec
   :phases
   {:configure (plan-fn
                ;; in addition to installing apache (which we get for
                ;; free by extending the httpd/server-spec in our
                ;; group-spec) we also want to set up a virtualhost
                (httpd/install-vhost "all2.us"
                 {:server-admin-email "upgradingdave@gmail.com"
                  :document-root-path *deploy-path*
                  :port "3000"})
                ;; our virtual host requires a few mods so lets set
                ;; those up as well
                (httpd/a2enmod "rewrite")
                (httpd/a2enmod "proxy_http")
                ;; In order to activate all this, lets restart the server
                (httpd/apache2ctl "restart"))}))

(defn postgres-role-and-db-action [alias]
  (plan-fn
   (postgres/create-role
    alias
    :user-parameters [:login
                      :encrypted :password (str "'" alias  "'")])
   (postgres/create-database
    alias :db-parameters [:owner alias])))

(defn postgres-role-and-db
  "Setup postgres role and db. Alias is used as role name and db name"
  [alias]
  (server-spec
   :phases
   {:configure (postgres-role-and-db-action alias)}
   :default-phases [:configure]))

(def
  ^{:doc "Defines a group spec that can be passed to converge or lift."}
  all2us-spec
  (group-spec
   "all2us"
   :extends [base-server
             ;; install postgres
             (postgres/server-spec
              {:version "9.3"
               :options {:listen_addresses ["*"]}
               :permissions [["host" "all" "all" "all" "md5"]]})
             (postgres-role-and-db "all2us")
             ;; install apache httpd
             (httpd/server-spec {})
             ;; configure httpd
             httpd-server
             ;; install java
             (java/server-spec {:vendor :oracle :components #{:jdk}})
             ]
   :phases 
   {:configure (plan-fn 
                ;; start the service!
                (exec {:language :bash} "sudo service all2us start"))}
   :node-spec default-node-spec
   :default-phases [:install :configure]))

;; convenience methods

(defn service [& [compute-service-kw]]
  (let [compute-service (or compute-service-kw :vmfest)]
    (pallet.configure/compute-service compute-service)))

(def s (atom nil))

(defn summary [& [sess]]
  (let [sess (or sess @s)]
    (session-summary sess)))

(defn detail [& [sess]]
  (let [sess (or sess @s)]
    (explain-session sess)))

(defn startup [& [compute-service]]
  (let [compute-service (or compute-service (service :vmfest))]
    (summary
     (swap! s (fn [o] (pallet.api/converge {all2us-spec 1}
                                           :compute compute-service))))))

(defn run [phase-fn & [compute-service]]
  (let [compute-service (or compute-service (service :vmfest))]
    (summary
     (swap! s (fn [o] (pallet.api/converge {all2us-spec 1}
                                           :compute compute-service
                                           :phase phase-fn))))))

(defn shutdown [& [compute-service]]
  (let [compute-service (or compute-service (service :vmfest))]
    (summary
     (swap! s (fn [o] (pallet.api/converge {all2us-spec 0}
                                           :compute compute-service))))))

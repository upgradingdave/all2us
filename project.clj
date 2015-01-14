(defproject
  upgradingdave/all2us
  "0.1.0-alpha.1"
  :description
  "FIXME: write description"
  :url
  "http://example.com/FIXME"
  :dependencies
  [[prone "0.8.0"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [selmer "0.7.7"]
   [com.taoensso/tower "3.0.2"]
   [markdown-clj "0.9.58" :exclusions [com.keminglabs/cljx]]
   [im.chit/cronj "1.4.3"]
   [com.taoensso/timbre "3.3.1"]
   [org.postgresql/postgresql "9.3-1102-jdbc41"]
   [noir-exception "0.2.3"]
   [korma "0.4.0"]
   [lib-noir "0.9.5"]
   [org.clojure/clojure "1.6.0"]
   [environ "1.0.0"]
   [ring-server "0.3.1"]
   [ragtime "0.3.6"]
   [http-kit "2.1.19"]

   ;; pallet
   [com.palletops/pallet "0.8.0-RC.9"]
   [com.palletops/pallet-jclouds "1.7.3"]
   [org.apache.jclouds/jclouds-allblobstore "1.7.2"]
   [org.apache.jclouds/jclouds-allcompute "1.7.2"]
   [org.apache.jclouds.driver/jclouds-slf4j "1.7.2"
    :exclusions [org.slf4j/slf4j-api]]
   [org.apache.jclouds.driver/jclouds-sshj "1.7.2" 
    :exclusions [net.schmizz/sshj]]
   [ch.qos.logback/logback-classic "1.0.9"]
   [com.palletops/pallet-vmfest "0.4.0-alpha.1"]
   ;; pallet crates
   [com.palletops/postgres-crate "0.8.1-9.3.ubuntu"]
   [com.palletops/java-crate "0.8.0-beta.6"]
   [upgradingdave/httpd "0.1.0-alpha.1"
    :exclusions [net.schmizz/sshj]]
   ]
  :source-paths ["src" "pallet/src"]
  :repl-options
  {:init-ns all2us.repl}
  :jvm-opts
  ["-server"]
  :plugins
  [[lein-ring "0.9.0"]
   [lein-environ "1.0.0"]
   [lein-ancient "0.5.5"]
   [ragtime/ragtime.lein "0.3.6"]
   [com.palletops/pallet-lein "0.8.0-alpha.1"]]
  :ring
  {:handler all2us.handler/app,
   :init all2us.handler/init,
   :destroy all2us.handler/destroy
   :uberwar-name "all2us.war"}
  :profiles
  {:uberjar {:omit-source true, :env {:production true}, :aot :all},
   :production
   {:ring
    {:open-browser? falsen, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies
    [[ring-mock "0.1.5"]
     [ring/ring-devel "1.3.2"]
     [pjstadig/humane-test-output "0.6.0"]],
    :injections
    [(require 'pjstadig.humane-test-output)
     (pjstadig.humane-test-output/activate!)],
    :env {:dev true}}}
  :ragtime
  {:migrations ragtime.sql.files/migrations,
   :database
   "jdbc:postgresql://postgres/all2us?user=all2us&password=all2us"}
  :uberjar-name
  "all2us.jar"
  :min-lein-version "2.0.0"
  :main all2us.core)

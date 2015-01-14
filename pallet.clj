;;; Pallet project configuration file

;;; By default, the pallet.api and pallet.crate namespaces are already referred.
;;; The pallet.crate.automated-admin-user/automated-admin-user us also referred.

(require '[all2us-pallet :refer [all2us-group-spec]])

(defproject all2us
  :provider {:vmfest
             {:node-spec
              {:image  {:os-family :ubuntu
                        :image-id :ubuntu-14.04}
               :hardware {:min-cores 1}}
              :selectors #{:default}}
             :aws
             {:node-spec
              {:image {:os-family :ubuntu
                       :image-id "us-east-1/ami-16d7b37e"}
               :hardware {:hardware-id "t1.micro"}}
              :selectors #{:default}}}
  :groups [(all2us-group-spec :aws)])

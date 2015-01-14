;;; Pallet project configuration file

;;; By default, the pallet.api and pallet.crate namespaces are already referred.
;;; The pallet.crate.automated-admin-user/automated-admin-user us also referred.

(require '[all2us-pallet :refer [all2us-spec]])

(defproject all2us
  :provider {:vmfest
             {:node-spec
              {:image {:os-family :ubuntu :os-version-matches "14.04"
                       :os-64-bit true}}
              :selectors #{:default}}}
  :groups [all2us-spec]
)

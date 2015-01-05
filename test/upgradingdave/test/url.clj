(ns upgradingdave.test.url
  (:use clojure.test
        upgradingdave.url))

(deftest test-app
  (testing "encode and decode"
    (is (= "mS" (encode 442)))
    (is (= 123 (decode (encode 123))))
    ;; check to ensure that (decode (encode n)) equals n for 0 to 1000
    (is (reduce (fn [v n] (and v (true? n))) 
                (for [n (range 1000)] (= n (decode (encode n))))))))

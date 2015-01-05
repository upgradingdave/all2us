(ns upgradingdave.url)

(def ^:dynamic *alphabet*
  "a couple of notes: purposely did not use 1's, l's, zeros or o's
  so it's easier to read. removed vowels so there's less chance of
  random words."
  "bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ-_23456789")

(defn offset2char
  "returns character found at offset in *alphabet*"
  [offset]
  (nth *alphabet* offset))

(defn char2offset
  "returns zero based offset of charactor or string c inside *alphabet*"
  [c]
  (.indexOf *alphabet* (str c)))

(defn base 
  "simply returns the number of characters in *alphabet*. this is the
  base that we're using to convert back and forth to and from"
  []
  (count *alphabet*))

(defn basify
  "take a number num (with base 10) and create a vector where each
  element represents a factor of a new number with new base. For
  example, (baseify 62 125) will convert the number 125 into (2 1)
  which represents 125 = 2*62^1+1*62^0"
  [base num]
  (if (= num 0)
    [0]
    (loop [res [] num num]
      (if (<= num 0)
        (reverse res)

        (recur 
         (conj res (mod num base))
         (int (/ num base)))))))

(defn encode 
  "encode number `num` by using the *alphabet* defined above to create a
  string that represents the number using the bijective algorithm
  described here
  http://stackoverflow.com/questions/742013/how-to-code-a-url-shortener.
  On my macbook pro in Jan 2015, this can encode up to 110 billion
  integers before running into integer out of range."
  [num]
  (apply str (map offset2char (basify (base) num))))

(defn decode
  "decode string `orig` by using the *alphabet* defined above to return
  a number that represents the string using bijective algorithm
  described here
  http://stackoverflow.com/questions/742013/how-to-code-a-url-shortener"
  [orig]
  (let [base (count *alphabet*)]
    ;; calculate idx of each char in *alphabet*. Multiply by base
    ;; and add the offset
    (reduce (fn [i idx] (+ (* i base) idx) ) 0 (map char2offset orig))))

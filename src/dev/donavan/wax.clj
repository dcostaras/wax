(ns dev.donavan.wax
  (:require [com.rpl.specter :as sp]))

(def document-example-2
  {:length 7
   :type :document
   :selection [1 1]
   :script
   [{:word "foo"
     :type :word
     :length 3}
    {:word " "
     :type :word
     :length 1}
    {:word "bar"
     :type :word
     :length 3}]})

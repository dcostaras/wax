(ns dev.donavan.wax-1
  (:require [com.rpl.specter :as s]))

;; Explicit length tracking

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

(def action-example-1
  {:type :key
   :key "a"
   :length 1})

(defn document->index
  ;; TODO spec arg checking with guardrails
  ;; TODO specter this
  [{[s e] :selection :as document}]
  (let [lengths (map :length (:script document))]
    (loop [lengths lengths
           running-total 0
           index 0]

      (cond
        (> (first lengths) e)
        index

        :else
        (recur (rest lengths)
               (+ running-total (first lengths))
               (inc index))))))

(defn action->transformation
  [{action-length :length :as action}]
  (s/terminal
   (fn [{node-length :length :as node}]
     (if (< #p action-length
            #p node-length)
       (do
         ;; (prn (:length node))
         ;; (prn (:key action))
         ;; (prn node)
         ;; (prn )
         #p (document->index node)
         node)
       node))))

(defn document->string
  [document]
  (prn '--------------------------------------------------)
  (s/multi-transform
   (s/multi-path (action->transformation action-example-1))
   document))

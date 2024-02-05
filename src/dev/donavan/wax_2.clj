(ns dev.donavan.wax-2
  (:require [com.rpl.specter :as s]))

;; Keeping selection up to date as a path into the document

(defn run
  [doc]
  (prn '--------------------------------------------------)
  (-> (s/multi-transform
       [(:selection doc)
        (s/terminal
         (fn [x]
           ;; (prn x)
           "b"))
        ]
       doc)
      (:contents)
      (prn)
      ))

(defn select
  [doc]
  (prn '--------------------------------------------------)
  (s/select
   (:selection doc)
   doc))


;; Insert within a word
;; (let [doc {:selection [:contents 0 :contents 0 :text (s/srange 0 1)]
;;            :action {:insert "y"}
;;            :contents [{:type :sentance
;;                        :contents [{:type :word
;;                                    :text "foo"}
;;                                   {:type :word
;;                                    :text "bar"}]}]}]
;;   (run doc))











;;; Insert across words
(let [
      doc {:selection [:contents
                       (s/srange 0 2)
                       (fn [x]
                         (prn x)
                         x)
                       ;; (s/view (fn [x]
                       ;;           (-> x
                       ;;               (update-in [0 :text] subs 2)
                       ;;               (update-in [1 :text] subs 0 1))))
                       ]
           :contents [{:type :word
                       :text "foo"}
                      {:type :word
                       :text "bar"}]}]
  (run doc))












;;; text navigator
(do
  (s/defnav text []
    (select*
     [this structure next-fn]
     (next-fn (apply str structure)))
    (transform*
     [this structure next-fn]
     (let [res (next-fn (apply str structure))]
       [(subs res 0 3)
        (subs res 3)])))

  (let [vs ["foo" "bar"]]
    (s/multi-transform
     [text
      (s/terminal
       (fn [x]
         (prn x)
         x))]
     vs)
    ))

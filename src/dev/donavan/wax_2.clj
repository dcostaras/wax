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
           [{:type :word
             :text "b"}]))
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
;;; Selection navigator
(comment
  (do

    (s/defnav selection* [[a b c]]
      (select*
       [this structure next-fn]
       (throw (ex-info "not done" {}))
       (next-fn structure))
      (transform*
       [this structure next-fn]
       (let [res* (next-fn structure)
             left (-> structure
                      (first)
                      (update :text subs a b))
             right (-> structure
                       (last)
                       (update :text subs c))
             res (-> []
                     (conj left)
                     (into res*)
                     (conj right))]
         res)))

    (defn selection
      [a b substrings]
      [(s/srange a b)
       (selection* substrings)])


    (let [doc {:selection [:contents
                           (selection 0 2 [0 1 2])
                           (fn [x]
                             ;; (prn ['x x])
                             x)
                           ]
               :contents [{:type :word
                           :text "foo"}
                          {:type :link
                           :text "bar"}]}]
      (run doc))))

;; Pure view version
;; (s/view (fn [x]
;;           (let [left (-> x
;;                          (nth 0)
;;                          (update :text subs 0 2))
;;                 inner (-> x
;;                           (update-in [0 :text] subs 2)
;;                           (update-in [1 :text] subs 0 1))
;;                 right (-> x
;;                           (nth 1)
;;                           (update :text subs 1))]
;;             (-> [left]
;;                 (into inner)
;;                 (conj right)))))
;; (s/srange 1 3)











;;; text navigator
(comment
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
      )))

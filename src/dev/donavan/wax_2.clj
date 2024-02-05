(ns dev.donavan.wax-2
  (:require [com.rpl.specter :as s]))

;;;; Keeping selection up to date as a path into the document

(defn run
  [doc]
  (prn '--------------------------------------------------)
  (-> (s/multi-transform
       [(:selection doc)
        (s/terminal
         (fn [x]
           (prn ['terminal x])
           [{:type :word
             :text "a"}]))
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








;;; Insert within a word
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

    (s/defnav selection* [a b [c d e]]
      (select*
       [this structure next-fn]
       (throw (ex-info "not done" {}))
       (next-fn structure))
      (transform*
       [this structure next-fn]
       (let [unmodified-left (s/select-one
                              (s/srange 0 a)
                              structure)
             unmodified-right (s/select-one
                               (s/srange-dynamic (constantly b) count)
                               structure)
             left (->> structure
                       (s/select-one (s/nthpath a))
                       (s/transform
                        :text
                        (fn [x]
                          (subs x 0 d))))
             right (->> structure
                        (s/select-one (s/nthpath (dec b)))
                        (s/transform
                         :text
                         (fn [x]
                           (subs x (inc d)))))
             structure* (->> structure
                             (s/select-one (s/srange a b))
                             (s/multi-transform
                              (s/multi-path
                               [s/FIRST
                                :text
                                (s/terminal
                                 (fn foo [x]
                                   (subs x d)))]
                               [s/LAST
                                :text
                                (s/terminal
                                 (fn bar [x]
                                   (subs x 0 e)))])))
             res* (next-fn structure*)
             res (-> unmodified-left

                     (conj left)
                     (into res*)
                     (conj right)

                     ;; If we keep using vectors we can use rrb-vectors for efficient concat
                     (into unmodified-right))]
         res)))

    (defn selection
      [a b substrings path]
      [ ;; (s/srange a b)
       (selection* a b substrings)
       path])

    (s/defnav foo [a b]
      (select*
       [this structure next-fn]
       (throw (ex-info "not done" {}))
       (next-fn structure))
      (transform*
       [this structure next-fn]
       (next-fn structure)))

    (let [doc {:selection [:contents
                           (s/multi-path
                            ;; FIXME how to edit changed section of document, e.g., how to recombine
                            ;; {:type :word, :text "b"} {:type :word, :text "a"} {:type :word, :text
                            ;; "z"} into "baz.
                            (selection
                             1 3 [0 1 2]
                             (fn baz [x]
                               (prn ['selection x])
                               x))
                            (fn [x]
                              (prn ['after x])
                              x))]
               :contents [{:type :link
                           :text "foo"}
                          {:type :word
                           :text "bar"}
                          {:type :word
                           :text "baz"}
                          {:type :link
                           :text "qux"}]}]
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











;;; string text subselect navigator
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

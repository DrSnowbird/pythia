(ns core.main
   (:require [core.nlu.context.short_term_memory :as stm]
             [core.external.gf_server :as gf]
             [core.external.ner.ner   :as ner]
             [core.nlu.interpretation :refer [interpret handle-effects]]))


(declare parse-and-interpret)


;; Main

(defn dispatch [pipeline grammar input]

  (case pipeline
       :generate-random     (println "\n" (gf/generate-random grammar))
       :parse-and-interpret (parse-and-interpret grammar input)
       (java.lang.System/exit 1))
)


;; Parsing and interpretation pipeline

(defn parse-and-interpret [grammar normalized-input]

  (stm/init!)

  (let [ ;; named entity recognition
         new-input (ner/recognize normalized-input)
         ;; parsing 
         parses    (gf/request-parse grammar new-input)
         ;; interpretation
         interpretations       (apply concat (map interpret parses))
         final-interpretations (map handle-effects interpretations)
       ]

    (if-not (empty? final-interpretations)
            (first final-interpretations))
      ; else: Partial parsing
      ; TODO (robustness/...)
  ))
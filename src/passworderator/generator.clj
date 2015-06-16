(ns passworderator.generator
  (:require [clojure.java.io :as io]
            [clojure.string :refer [split]]))

(def locales ["de" "en" "es" "fr" "nl"])

(def wordlist
  (memoize
   (fn [locale]
     (->>
      (-> locale
          (str ".txt")
          io/resource
          slurp
          (split #"\n"))
      (filter (partial re-matches #"^[a-z]+$"))))))

(defn generate [& [{:keys [locale
                           max-word-length
                           words]
                    :or {locale "en"
                         max-word-length 10
                         words 4}}]]
  (let [wordlist (filter #(< (count %) max-word-length)
                         (wordlist locale))]
    (take words (repeatedly #(rand-nth wordlist)))))

(defn combinations [& [{:keys [locale
                               max-word-length
                               words]
                        :or {locale "en"
                             max-word-length 10
                             words 4}}]]
  (let [wordcount (count (filter #(< (count %) max-word-length)
                                 (wordlist locale)))]
    {:words wordcount
     :combinations (Math/pow wordcount words)}))

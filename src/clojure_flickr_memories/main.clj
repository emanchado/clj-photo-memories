(ns clojure-flickr-memories.main
  (:use [clojure-flickr-memories.search-engine :as search-engine])
  (:gen-class))

(defn -main
  "Receives three parameters: Flickr userid, start date and end date,
  and retrieves all the pictures taken by the given user between the
  given dates."
  [& args]
  (println (apply search-engine/search-photos (map #(nth args %) (range 3)))))

(ns clj-flickr-memories.main
  (:require [clj-flickr-memories.flickr-client :as fc])
  (:require [clj-flickr-memories.mail :as mail])
  (:require [clj-flickr-memories.date :refer [find-this-week-in-past-year]])
  (:require [clojure.edn :as edn])
  (:require [guns.cli.optparse :refer [parse]])
  (:import java.util.Date java.text.SimpleDateFormat)
  (:gen-class))

(defn -main
  "Receives three parameters: Flickr URL name, start date and end date,
  and retrieves all the pictures taken by the given user between the
  given dates."
  [& args]
  (let [config (clojure.edn/read-string (slurp "config.edn"))
        username (fc/user-id-from-url-name (first args))
        raw-date-from (second args)
        rfc3339-formatter (SimpleDateFormat. "yyyy-MM-dd")
        reference-date (if (> (count args) 1)
                         (.parse rfc3339-formatter raw-date-from)
                         (Date.))]
    (loop [years-back 1]
      (let [[week-start week-end] (find-this-week-in-past-year reference-date
                                                               years-back)
            date-from-string (.format rfc3339-formatter week-start)
            date-to-string (.format rfc3339-formatter week-end)
            photos (fc/search-photos username date-from-string date-to-string)]
        (if (> (count photos) 0)
          (let [html-mail-text (apply str (mail/mail-template date-from-string
                                                              date-to-string
                                                              photos))]
            (if (> (count args) 2)
              (mail/send-mail (nth args 2)
                              (str "Flickr memories for " date-from-string)
                              html-mail-text
                              config)
              (println html-mail-text)))
          (if (< years-back 5)
            (recur (inc years-back))
            (println "Giving up, can't find anything this week :-(")))))))

(ns clj-photo-memories.main
  (:require [clj-photo-memories.flickr-client :as fc])
  (:require [clj-photo-memories.trovebox-client :as tc])
  (:require [clj-photo-memories.service-protocol :as service])
  (:require [clj-photo-memories.mail :as mail])
  (:require [clj-photo-memories.date :refer [find-this-week-in-past-year]])
  (:require [clojure.edn :as edn])
  (:require [guns.cli.optparse :refer [parse]])
  (:import java.util.Date java.text.SimpleDateFormat)
  (:import java.net.URL java.net.MalformedURLException)
  (:gen-class))

(def options
  [["-u" "--base-url URL" "Base URL for the server API"
    :default nil
    :assert [#(URL. %) "%s is not a valid URL"]
    ]
   ["-s" "--static-domain SUBDOMAIN" "Subdomain (after 'farm<N>') for image file URLs. Flickr-only."
    :default nil
    ]
   ["-t" "--type TYPE" "Type of API (can be 'flickr' or 'trovebox')"
    :default "flickr"
    ]])

(defn parse-command-line [args]
  (try
    (let [[options remaining-args options-summary] (parse args options)]
      (when (or (> (count remaining-args) 3)
                (< (count remaining-args) 1))
        (println options-summary)
        (System/exit 1))
      [options remaining-args])
    (catch MalformedURLException e
      (println "Malformed URL in the -u argument")
      (System/exit 1))
    (catch AssertionError e
      (println "Invalid options")
      (System/exit 1))))

(defn -main
  "Receives three parameters: API username, start date and end date,
  and retrieves all the pictures taken by the given user between the
  given dates."
  [& args]
  (let [[options rest-args] (parse-command-line args)]
    (let [config (clojure.edn/read-string (slurp "config.edn"))
          username (first rest-args)
          raw-date-from (second rest-args)
          rfc3339-formatter (SimpleDateFormat. "yyyy-MM-dd")
          reference-date (if (> (count rest-args) 1)
                           (.parse rfc3339-formatter raw-date-from)
                           (Date.))]
      (loop [years-back 5]
        (let [[week-start week-end] (find-this-week-in-past-year reference-date
                                                                 years-back)
              date-from-string (.format rfc3339-formatter week-start)
              date-to-string (.format rfc3339-formatter week-end)
              constructor (if (= (:type options) "flickr")
                            fc/->FlickrClient
                            tc/->TroveboxClient)
              client (constructor options)
              photos (service/search-photos client username date-from-string date-to-string)]
          (if (pos? (count photos))
            (let [html-mail-text (clojure.string/join (mail/mail-template date-from-string
                                                                          date-to-string
                                                                          photos))]
              (if (> (count rest-args) 2)
                (mail/send-mail (nth rest-args 2)
                                (str "Photo memories for " date-from-string)
                                html-mail-text
                                config)
                (println html-mail-text)))
            (if (> years-back 1)
              (recur (dec years-back))
              (println "Giving up, can't find anything this week :-("))))))))

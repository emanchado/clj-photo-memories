(ns clj-flickr-memories.main
  (:require [clj-flickr-memories.flickr-client :as fc])
  (:require [clj-flickr-memories.mail :as mail])
  (:require [clj-flickr-memories.date :refer [find-this-week-in-past-year]])
  (:require [clojure.edn :as edn])
  (:require [guns.cli.optparse :refer [parse]])
  (:import java.util.Date java.text.SimpleDateFormat)
  (:import java.net.URL java.net.MalformedURLException)
  (:gen-class))

(def options
  [["-u" "--base-url URL" "Base URL for the Flickr API"
    :default "http://api.flickr.com/services/rest"
    :assert [#(URL. %) "%s is not a valid URL"]
    ]
   ["-s" "--static-domain SUBDOMAIN" "Subdomain (after 'farm<N>') for image file URLs"
    :default "static.flickr.com"
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
  "Receives three parameters: Flickr URL name, start date and end date,
  and retrieves all the pictures taken by the given user between the
  given dates."
  [& args]
  (let [[options rest-args] (parse-command-line args)]
    (binding [fc/*base-url* (:base-url options)
              fc/*static-domain* (:static-domain options)]
      (let [config (clojure.edn/read-string (slurp "config.edn"))
            username (fc/user-id-from-url-name (first rest-args))
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
                photos (fc/search-photos username date-from-string date-to-string)]
            (if (> (count photos) 0)
              (let [html-mail-text (apply str (mail/mail-template date-from-string
                                                                  date-to-string
                                                                  photos))]
                (if (> (count rest-args) 2)
                  (mail/send-mail (nth rest-args 2)
                                  (str "Flickr memories for " date-from-string)
                                  html-mail-text
                                  config)
                  (println html-mail-text)))
              (if (> years-back 1)
                (recur (dec years-back))
                (println "Giving up, can't find anything this week :-(")))))))))

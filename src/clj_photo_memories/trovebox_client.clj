(ns clj-photo-memories.trovebox-client
  (:import java.util.Calendar java.text.SimpleDateFormat)
  (:require [clj-photo-memories.service-protocol :as service-protocol]
            [clj-http.client :as http]
            [clojure.data.json :as json]))

(defrecord TroveboxClient [options])

(defn- multi-get [h keys]
  (reduce (fn [acc k]
            (conj acc [(keyword k) (get h k)]))
          {}
          keys))

(defn photos-in-json-result [json-text]
  (let [json-data (json/read-str json-text)
        results (get json-data "result")]
    (map (fn [result]
           (let [basic-photo (multi-get result ["id" "title" "description"])]
             (merge basic-photo
                    {:page-url (get result "url")
                     :thumbnail-url (get result "path500x300")})))
         results)))

(defn day-before [date]
  (let [rfc3339-formatter (SimpleDateFormat. "yyyy-MM-dd")
        calendar (Calendar/getInstance)]
    (.setTime calendar (.parse rfc3339-formatter date))
    (.add calendar Calendar/DATE -1)
    (.format rfc3339-formatter (.getTime calendar))))

(extend-type TroveboxClient service-protocol/ServiceClient
             (search-photos [this username from-date to-date]
               (try
                 (let [base-url (:base-url (:options this))
                       query-params {:query-params {:returnSizes "500x300"
                                                    :takenAfter (day-before
                                                                 from-date)
                                                    :takenBefore to-date}}
                       response (http/get (str base-url "/photos/list.json")
                                          query-params)]
                   (photos-in-json-result (:body response)))
                 (catch Exception e
                   (println (str "Couldn't get photos from the Trovebox server at " (:base-url (:options this)) " for " from-date "-" to-date))))))

(ns clj-photo-memories.trovebox-client
  (:require [clj-photo-memories.service-protocol :as service-protocol]
            [clj-http.client :as http-client]
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
           (multi-get result ["id" "owner" "title" "description" "url"]))
         results)))

(extend-type TroveboxClient service-protocol/ServiceClient
             (search-photos [this username from-date to-date]
               (try
                 (let [base-url (:base-url (:options this))
                       response (http-client/get (str base-url "/photos/list.json")
                                                 {:query-params {:takenBefore to-date
                                                                 :takenAfter from-date}})]
                   (photos-in-json-result (:body response)))
                 (catch Exception e
                   (println (str "Couldn't get photos from the Trovebox server at " (:base-url (:options this)) " for " from-date "-" to-date))))))

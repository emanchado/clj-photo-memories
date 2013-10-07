(ns clojure-flickr-memories.search-engine
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clj-http.client :as http-client])
  (:use clojure.contrib.zip-filter.xml))

(def ^:dynamic *api-key* "30c195ccce757cd281132f0bef44de0d")
(def ^:dynamic *base-url* "http://api.flickr.com/services/rest")

(defn photos-in-xml-result [xml]
  (let [java-stream (java.io.ByteArrayInputStream. (.getBytes xml))
        zipped-xml (zip/xml-zip (xml/parse java-stream))
        photo-list (xml-> zipped-xml :photos :photo)]
    (map (fn [photo-zipped-xml]
           (let [photo-attrs (:attrs (first photo-zipped-xml))]
             {:id (:id photo-attrs)
              :secret (:secret photo-attrs)
              :owner (:owner photo-attrs)
              :farm-id (:farm photo-attrs)
              :server-id (:server photo-attrs)
              :title (:title photo-attrs)
              :description (first (:content (first (filter #(= (:tag %) :description) (:content (first photo-zipped-xml))))))}))
         photo-list)))

(defn search-photos [user-id from-date to-date & {:keys [base-url api-key]
                                                  :or {base-url *base-url*
                                                       api-key *api-key*}}]
  (let [response (http-client/get base-url
                                  {:query-params {:method "flickr.photos.search"
                                                  :api_key api-key
                                                  :extras "description"
                                                  :user_id user-id
                                                  :min_taken_date from-date
                                                  :max_taken_date to-date}})]
    (photos-in-xml-result (:body response))))

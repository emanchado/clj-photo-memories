(ns clj-photo-memories.flickr-client
  (:require [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clj-http.client :as http-client])
  (:use clojure.contrib.zip-filter.xml))

(def ^:dynamic *api-key* "30c195ccce757cd281132f0bef44de0d")
(def ^:dynamic *base-url* "https://api.flickr.com/services/rest")
(def ^:dynamic *static-domain* "static.flickr.com")

(defn user-id-from-xml-result [xml]
  (let [java-stream (java.io.ByteArrayInputStream. (.getBytes xml))
        zipped-xml (zip/xml-zip (xml/parse java-stream))
        status (first (xml-> zipped-xml (attr :stat)))
        user-info (xml-> zipped-xml :user)]
    (if (= status "ok")
      (first (xml-> zipped-xml :user (attr :id)))
      (throw (IllegalArgumentException. (str "Error fetching user: " xml))))))

(defn user-id-from-url-name [url-name & {:keys [base-url api-key]
                                         :or {base-url *base-url*
                                              api-key *api-key*}}]
  "The Flickr API is pretty fucked up wrt. to how to get a user id. It
   needs either the full name of the user, or the full URL to its
   photo stream. This function receives only the relevant part of the
   URL ('emanchado' in http://www.flickr.com/photos/emanchado/) and
   returns the user id."
  (let [full-url (str "http://www.flickr.com/photos/" url-name)
        response (http-client/get base-url
                                  {:query-params {:method "flickr.urls.lookupUser"
                                                  :api_key api-key
                                                  :url full-url}})]
    (user-id-from-xml-result (:body response))))

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

(defn search-photos [username from-date to-date & [options]]
  (try
    (let [base-url (if (nil? (:base-url options)) *base-url* (:base-url options))
          api-key (if (nil? (:api-key options)) *api-key* (:api-key options))
          user-id (user-id-from-url-name username)
          response (http-client/get base-url
                                    {:query-params {:method "flickr.photos.search"
                                                    :api_key api-key
                                                    :extras "description"
                                                    :user_id user-id
                                                    :min_taken_date from-date
                                                    :max_taken_date to-date}})]
      (photos-in-xml-result (:body response)))
    (catch Exception e
      (println (str "Couldn't get photos from the Flickr server for " from-date "-" to-date))
      ())))

(defn photo-image-url [photo]
  (str "http://farm" (:farm-id photo) "." *static-domain* "/"
       (:server-id photo) "/"
       (:id photo) "_" (:secret photo) ".jpg"))

(defn photo-page-url [photo]
  (str "http://www.flickr.com/photos/" (:owner photo) "/" (:id photo)))

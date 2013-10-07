(ns clj-flickr-memories.main
  (:use [clj-flickr-memories.flickr-client :as flickr-client])
  (:require [net.cgrand.enlive-html :as html])
  (:gen-class))

(html/deftemplate mail-template "templates/mail.html"
  [date-from date-to photos]
  ; Figure out why the fuck this doesn't work with replace-vars
  [:head :title] (html/content (str "Flickr pictures from " date-from
                                    " to " date-to))
  [:h1 :#date-from] (html/content date-from)
  [:h1 :#date-to] (html/content date-to)
  [:h1 :#year-from] (html/content date-from)
  [:div.photo] (html/clone-for [photo photos]
                               [:a] (html/set-attr :href
                                                   (str "http://farm"
                                                        (:farm-id photo)
                                                        ".static.flickr.com/"
                                                        (:server-id photo)
                                                        "/"
                                                        (:id photo)
                                                        "_"
                                                        (:secret photo)
                                                        ".jpg"))
                               [:img] (html/set-attr :alt (:title photo)
                                                     :src (:title photo))
                               [:div.description :em] (html/content (:description photo))))

(defn -main
  "Receives three parameters: Flickr userid, start date and end date,
  and retrieves all the pictures taken by the given user between the
  given dates."
  [& args]
  (let [username (flickr-client/user-id-from-url-name (first args))
        date-from (second args)
        date-to (nth args 2)]
    (println (apply str (mail-template date-from date-to (flickr-client/search-photos username date-from date-to))))))

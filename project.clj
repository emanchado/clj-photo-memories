(defproject clj-photo-memories "0.1.0-SNAPSHOT"
  :description "Small utility to fetch old pictures from a Flickr/Trovebox account"
  :url "http://github.com/emanchado/clj-photo-memories"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [clj-http "0.7.7"]
                 [enlive "1.1.4"]
                 [javax.mail/mail "1.4.3"]
                 [guns.cli/optparse "1.1.1"]]
  :main clj-photo-memories.main)

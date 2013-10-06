(defproject clojure-flickr-memories "0.1.0-SNAPSHOT"
  :description "Small utility to fetch old pictures from a Flickr account"
  :url "http://github.com/emanchado/clojure-flickr-memories"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [clj-http "0.7.7"]
                 [enlive "1.1.4"]]
  :main clojure-flickr-memories.main)

(ns clj-photo-memories.trovebox-client
  (:require [clj-photo-memories.service-protocol :as service-protocol]))

(defrecord TroveboxClient [options])

(extend-type TroveboxClient service-protocol/ServiceClient
             (search-photos [this username from-date to-date]
               []))

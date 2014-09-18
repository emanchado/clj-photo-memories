(ns clj-photo-memories.service-protocol)

(defprotocol ServiceClient
  (search-photos [this username from-date to-date]))

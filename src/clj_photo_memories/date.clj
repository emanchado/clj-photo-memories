(ns clj-photo-memories.date
  (:import java.util.Calendar))

(defn find-this-week-in-past-year [reference-date number-years-ago]
  (let [calendar (Calendar/getInstance)]
    (.setTime calendar reference-date)
    (.add calendar Calendar/YEAR (* -1 number-years-ago))
    (loop []
      (.add calendar Calendar/DATE -1)
      (if (not= (.get calendar Calendar/DAY_OF_WEEK)
                Calendar/MONDAY)
        (recur)))
    (let [start-date (.getTime calendar)]
      (.add calendar Calendar/DATE 7)
      [start-date
       (.getTime calendar)])))

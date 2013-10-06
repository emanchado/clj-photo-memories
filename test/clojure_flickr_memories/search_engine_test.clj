(ns clojure-flickr-memories.search-engine-test
  (:use clojure.test
        clojure-flickr-memories.search-engine))

(deftest extract-information-from-search-results
  (testing "Extracts zero results from an empty search result"
    (let [photo-list (photos-in-xml-result "<?xml version=\"1.0\" encoding=\"utf-8\" ?>
<rsp stat=\"ok\">
<photos page=\"1\" pages=\"0\" perpage=\"100\" total=\"0\" />
</rsp>")]
      (is (= (count photo-list) 0))))

  (testing "Extracts single result from a single-result XML"
    (let [photo-list (photos-in-xml-result "<?xml version=\"1.0\" encoding=\"utf-8\" ?>
<rsp stat=\"ok\">
<photos page=\"1\" pages=\"1\" perpage=\"100\" total=\"1\">
        <photo id=\"4231276405\" owner=\"24881879@N00\" secret=\"468409240f\" server=\"4007\" farm=\"5\" title=\"IMG_3407.JPG\" ispublic=\"1\" isfriend=\"0\" isfamily=\"0\" />
</photos>
</rsp>")]
      (is (= (count photo-list) 1))))

  (testing "Extracts information from the result XML"
    (let [photo-info (first (photos-in-xml-result "<?xml version=\"1.0\" encoding=\"utf-8\" ?>
<rsp stat=\"ok\">
<photos page=\"1\" pages=\"1\" perpage=\"100\" total=\"1\">
        <photo id=\"4231276405\" owner=\"24881879@N00\" secret=\"468409240f\" server=\"4007\" farm=\"5\" title=\"IMG_3407.JPG\" ispublic=\"1\" isfriend=\"0\" isfamily=\"0\">
            <description>Sand sculpture of John Lennon</description>
        </photo>
</photos>
</rsp>"))]
      (is (= (:id photo-info) "4231276405"))
      (is (= (:secret photo-info) "468409240f"))
      (is (= (:owner photo-info) "24881879@N00"))
      (is (= (:farm-id photo-info) "5"))
      (is (= (:server-id photo-info) "4007"))
      (is (= (:title photo-info) "IMG_3407.JPG"))
      (is (= (:description photo-info) "Sand sculpture of John Lennon")))))

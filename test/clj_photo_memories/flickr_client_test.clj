(ns clj-photo-memories.flickr-client-test
  (:use clojure.test
        clj-photo-memories.flickr-client))

(import '(java.lang IllegalArgumentException))

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
      (is (= photo-info {:id "4231276405",
                         :title "IMG_3407.JPG"
                         :description "Sand sculpture of John Lennon"
                         :page-url "http://www.flickr.com/photos/24881879@N00/4231276405"
                         :thumbnail-url "http://farm5.static.flickr.com/4007/4231276405_468409240f.jpg"})))))


(deftest extract-information-from-user-info-results
  (testing "Throws an exception if the user wasn't found"
    (is (thrown? IllegalArgumentException
                 (user-id-from-xml-result "<?xml version=\"1.0\" encoding=\"utf-8\" ?>
<rsp stat=\"fail\">
	<err code=\"1\" msg=\"User not found\" />
</rsp>"))))

  (testing "Returns the user id if found"
    (is (= (user-id-from-xml-result "<?xml version=\"1.0\" encoding=\"utf-8\" ?>
<rsp stat=\"ok\">
<user id=\"24881879@N00\">
	<username>Esteban Manchado</username>
</user>
</rsp>")))))

(deftest photo-url-utilities
  (testing "Can re-create an image URL from a photo information map"
    (is (= (photo-image-url "8603065596" "1e940ccd43" "9" "8404")
           "http://farm9.static.flickr.com/8404/8603065596_1e940ccd43.jpg")))

  (testing "Can re-create a page URL from a photo information map"
    (is (= (photo-page-url "8603065596" "24881879@N00")
           "http://www.flickr.com/photos/24881879@N00/8603065596"))))

(ns clj-photo-memories.trovebox-client-test
  (:require [clj-photo-memories.trovebox-client :refer :all]
            [clojure.test :refer :all]))

(deftest extract-information-from-search-results
  (testing "Extracts zero results from an empty search result"
    (let [photo-list (photos-in-json-result "{\"message\":\"Your search did not return any photos\",\"code\":200,\"result\":[]}")]
      (is (= (count photo-list) 0))))

  (testing "Extracts single result from a single-result JSON"
    (let [photo-list (photos-in-json-result "{
    \"code\": 200,
    \"message\": \"Successfully retrieved user's photos\",
    \"result\": [
        {
            \"active\": \"1\",
            \"actor\": \"emanchado@example.com\",
            \"albums\": [],
            \"appId\": \"openphoto-frontend\",
            \"currentPage\": 1,
            \"currentRows\": 2,
            \"dateSortByDay\": \"20130820113543\",
            \"dateTaken\": \"1377026656\",
            \"dateTakenDay\": \"20\",
            \"dateTakenMonth\": \"8\",
            \"dateTakenYear\": \"2013\",
            \"dateUploaded\": \"1411252289\",
            \"dateUploadedDay\": \"20\",
            \"dateUploadedMonth\": \"9\",
            \"dateUploadedYear\": \"2014\",
            \"description\": \"Streets of Bergen (in the folk museum)\",
            \"exifCameraMake\": \"Canon\",
            \"exifCameraModel\": \"Canon PowerShot A610\",
            \"exifExposureTime\": \"1/200\",
            \"exifFNumber\": \"4\",
            \"exifFocalLength\": \"7.3\",
            \"filenameOriginal\": \"IMG_6541.JPG\",
            \"groups\": \"\",
            \"hash\": \"ccf551216847fabda83dcad66f60db86a3fee583\",
            \"height\": \"1200\",
            \"host\": \"photos.example.com/photos\",
            \"id\": \"3\",
            \"key\": \"\",
            \"latitude\": null,
            \"license\": \"\",
            \"longitude\": null,
            \"owner\": \"emanchado@example.com\",
            \"pageSize\": 30,
            \"pathBase\": \"http://photos.example.com/photos/base/201308/IMG_6541-4490f3.jpg\",
            \"permission\": \"1\",
            \"rotation\": \"0\",
            \"size\": \"0\",
            \"status\": \"1\",
            \"tags\": [
                \"2013\",
                \"August\"
            ],
            \"title\": \"\",
            \"totalPages\": 1,
            \"totalRows\": 2,
            \"url\": \"http://photos.example.com/p/3\",
            \"views\": \"0\",
            \"width\": \"1600\"
        }
    ]}")]
      (is (= (count photo-list) 1))))

  (testing "Extracts information from the result JSON"
    (let [photo-info (first (photos-in-json-result "{
    \"code\": 200,
    \"message\": \"Successfully retrieved user's photos\",
    \"result\": [
        {
            \"active\": \"1\",
            \"actor\": \"emanchado@example.com\",
            \"albums\": [],
            \"appId\": \"openphoto-frontend\",
            \"currentPage\": 1,
            \"currentRows\": 2,
            \"dateSortByDay\": \"20130820113543\",
            \"dateTaken\": \"1377026656\",
            \"dateTakenDay\": \"20\",
            \"dateTakenMonth\": \"8\",
            \"dateTakenYear\": \"2013\",
            \"dateUploaded\": \"1411252289\",
            \"dateUploadedDay\": \"20\",
            \"dateUploadedMonth\": \"9\",
            \"dateUploadedYear\": \"2014\",
            \"description\": \"Streets of Bergen (in the folk museum)\",
            \"exifCameraMake\": \"Canon\",
            \"exifCameraModel\": \"Canon PowerShot A610\",
            \"exifExposureTime\": \"1/200\",
            \"exifFNumber\": \"4\",
            \"exifFocalLength\": \"7.3\",
            \"filenameOriginal\": \"IMG_6541.JPG\",
            \"groups\": \"\",
            \"hash\": \"ccf551216847fabda83dcad66f60db86a3fee583\",
            \"height\": \"1200\",
            \"host\": \"photos.example.com/photos\",
            \"id\": \"3\",
            \"key\": \"\",
            \"latitude\": null,
            \"license\": \"\",
            \"longitude\": null,
            \"owner\": \"emanchado@example.com\",
            \"pageSize\": 30,
            \"pathBase\": \"http://photos.example.com/photos/base/201308/IMG_6541-4490f3.jpg\",
            \"permission\": \"1\",
            \"path500x300\": \"http://photos.example.com/photos/custom/201308/2013-08-20-11.52.30-7a1b40_500x300.jpg\",
            \"photo500x300\": [
                \"http://photos.example.com/photos/custom/201308/2013-08-20-11.52.30-7a1b40_500x300.jpg\",
                500,
                281
            ],
            \"rotation\": \"0\",
            \"size\": \"0\",
            \"status\": \"1\",
            \"tags\": [
                \"2013\",
                \"August\"
            ],
            \"title\": \"Folkemuseum in Bergen\",
            \"totalPages\": 1,
            \"totalRows\": 2,
            \"url\": \"http://photos.example.com/p/3\",
            \"views\": \"0\",
            \"width\": \"1600\"
        }
    ]}"))]
      (is (= photo-info {:id "3",
                         :title "Folkemuseum in Bergen"
                         :description "Streets of Bergen (in the folk museum)"
                         :page-url "http://photos.example.com/p/3"
                         :thumbnail-url "http://photos.example.com/photos/custom/201308/2013-08-20-11.52.30-7a1b40_500x300.jpg"})))))

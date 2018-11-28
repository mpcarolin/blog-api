(ns blog-server.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [blog-server.handler :refer :all]))

(def not-nil? (complement nil?))
(def not-blank? (complement clojure.string/blank?))

(app (mock/request :get "/blog/content/5"))
(deftest test-app
  (testing "get-content"
    (let [response (app (mock/request :get "/blog/content/5"))]
      (is (= (:status response) 200))
      (is (not-blank? (:body response))))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

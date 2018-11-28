(ns blog-server.handler
  (:use ring.util.response)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [blog-server.config :as config]
            [data.db :as db]))

;; Config
(clojure.spec.alpha/check-asserts
  (get-in config/properties [:assertions :spec]))

;; Helper functions
(defn with-date [post-body]
  (assoc post-body :date (new java.util.Date)))

;; Api
(defroutes endpoints
  (POST "/blog" request
    (response (db/add-post (-> request :params with-date))))
  (GET "/blog/all" []
    (response (db/get-all-posts)))
  (GET "/blog/meta" []
    (response (db/get-all-posts-metadata)))
  (GET "/blog/content/:id" [id]
    (response (db/get-content (Integer/parseInt id)))))
  (route/not-found "Route or element not found")

(def app
  (-> endpoints
    (wrap-json-response)
    (wrap-keyword-params)
    (wrap-params)))

(defn -main [& args]
  (let [port (get-in config/properties [:app-server :port])]
    (run-jetty app {:port port})))

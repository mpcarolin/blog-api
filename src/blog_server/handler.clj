(ns blog-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [blog-server.config :as config]
            [data.db :as db]))

;; config
(clojure.spec.alpha/check-asserts (-> config/properties
                                      :assertions
                                      :spec))

;; Helper functions
(defn with-date [post-body] (assoc post-body :date (new java.util.Date)))

;; Api
(defroutes endpoints
  (GET "/blog/all" [] (db/get-all-posts))
  (GET "/blog/meta" [] (db/get-all-posts-metadata))
  (GET "/blog/content/:id" [id] (db/get-content (Integer/parseInt id)))
  (POST "/blog" request
    (println request)
    (let [new-id (db/add-post (-> request :params with-date))]
      {:id new-id}))
  (route/not-found "Route or element not found"))

(def app
  (-> endpoints
    (wrap-json-response {:pretty true})
    (wrap-json-body {:keywords? true})
    (wrap-keyword-params)
    (wrap-params)))

;; Test: try running with lein ring!
(defn -main [& args]
  (let [port (get-in config/properties [:app-server :port])]
    (run-jetty app {:port port})))

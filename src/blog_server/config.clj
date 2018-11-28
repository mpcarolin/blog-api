(ns blog-server.config)

;; Property handling
(def properties
  (clojure.edn/read-string (slurp "properties.edn")))


(defn- set-system-props
  ([props]
    (set-system-props props {:set-fn #(System/setProperty %1 %2)}))
  ([props {:keys [set-fn]}]
    (doseq [[key value] props] (set-fn key value))))

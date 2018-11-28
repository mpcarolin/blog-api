(ns data.db
    (:require [clojure.java.jdbc :as jdbc]
              [clojure.spec.alpha :as s]
              [clojure.spec.test.alpha :as stest]
              [blog-server.config :as config]
              [clojure.spec.gen.alpha :as gen]))

(def db-spec (:db-spec config/properties))

;; data specs
(defrecord Post [title description date content id])
(defrecord PostMeta [title description date id])

(s/def ::id int?)
(s/def ::title (s/and string? #(<= (count %1) 64)))
(s/def ::description (s/and string? #(<= (count %1) 256)))
(s/def ::content string?)
(s/def ::date inst?)
(s/def ::post (s/keys :req-un [::title ::description ::date ::content]
                      :req-opt [::id]))
(s/def ::db-row (s/keys :req-un [::title ::description ::date ::content ::id]))

;; private helpers
(defn- content-from-clob
  [clob]
  (let [length (.length clob)]
    (.getSubString clob 1 length)))

;; public methods for database access
(defn add-post
  "Adds row with data in ::post post into the blog table."
  [post]
  (let [valid-post (s/assert ::post post)
        results (jdbc/insert! db-spec :blog valid-post)
        id (first (vals (first results)))]
    (assert (= (count results) 1))
    {:id id}))

(defn get-all-posts [] (jdbc/query db-spec ["select * from blog"] {:row-fn map->Post}))
(defn get-all-posts-metadata [] (jdbc/query db-spec ["select id, title, description, date from blog"] {:row-fn map->PostMeta}))
(defn delete-post
  "Deletes row with primary key id from the blog table."
  [id]
  (let [valid-id (s/assert ::id id)]
    (jdbc/delete! db-spec :blog ["id = ?" valid-id])))

(defn get-content
  "Gets content for post with primary key id. Returned content is a string."
  [id]
  (let [valid-id (s/assert ::id id)
        content (jdbc/query db-spec ["select content from blog where id = ?" valid-id]
                                    {:row-fn (comp :content map->Post)
                                     :result-set-fn first})]
       {:content content}))

(defproject blog-server "0.1.0"
  :description "Backbone to the recurse-carefully blog."
  :url "mpcarolin.github.io"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [proto-repl "0.3.1"]
                 [compojure "1.6.1"]
                 [org.clojure/java.jdbc "0.6.0"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler blog-server.handler/app}
  :main blog-server.handler
  :profiles
  {:repl {:plugins [[cider/cider-nrepl "0.18.0"]]}
   :dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [org.clojure/test.check "0.9.0"]
                        [ring/ring-mock "0.3.2"]]}})

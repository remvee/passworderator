(defproject passworderator "0.1.0-SNAPSHOT"
  :description "Generate passwords"
  :url "https://passworderator.remworks.net"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [hiccup "1.0.5"]
                 [environ "1.0.0"]]

  :main passworderator.core
  :uberjar-name "passworderator.jar"
  :profiles {:uberjar {:aot :all}})

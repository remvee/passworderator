(ns passworderator.core
  (:require [environ.core :refer [env]]
            [passworderator.web :as web]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main []
  (run-jetty (var web/app)
             {:port (Long/parseLong (get env :port "8080"))
              :join? false}))

(ns passworderator.web
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [html5]]
            [passworderator.generator :as generator]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]))

(defn html-ok [html]
  {:status 200
   :headers {"Content-Type" "text/html;charset=UTF-8"}
   :body html})

(defn no-cache [resp]
  (update-in resp [:headers] assoc "Cache-Control" "no-cache"))

(def default-title "Passworderator")

(defn layout [body]
  (let [title (get (meta body) :title default-title)]
    (html5
     [:head
      [:title title]
      [:link {:href "screen.css", :rel "stylesheet"}]
      [:meta {:name "viewport", :content "width=device-width, initial-scale=1.0, minimal-ui"}]]
     [:body
      [:header [:h1 title]]
      body])))

(defn humanize-combinations [{:keys [combinations words]}]
  (let [n combinations
        day (* 1000 60 60 24)
        month (* day 30.5)
        year (* day 365)
        century (* year 100)
        millenium (* century 10)
        msg
        (str
         (cond
           (< n day)
           "less than a day"

           (< n month)
           "less than a month"

           (< n year)
           (str (Math/round (/ n month)) " months")

           (< n (* 2 century))
           (str (Math/round (/ n year)) " years")

           (< n (* 2 millenium))
           (str (Math/round (/ n century)) " centuries")

           :else
           (str (Math/round (/ n millenium)) " millenia"))

         " at 1000 guesses/sec with a list of " words " words")]
    (if (< n century)
      [:span.warning msg]
      msg)))

(defn render-index [{:keys [locale words max-word-length] :as opts}]
  [:main
   [:section.generated-password
    (for [w (generator/generate opts)]
      [:span w])]
   [:section.combinations
    (humanize-combinations (generator/combinations opts))]
   [:section.customize
    [:form {:method "GET"}
     [:label {:for "locale"} "Language"]
     [:select#locale
      {:name "locale"}
      (for [v generator/locales]
        [:option {:selected (= v locale)} v])]
     [:label {:for "words"} "Words"]
     [:input#words {:type "number", :name "words", :value words}]
     [:label {:for "max-word-length"} "Maximum word length"]
     [:input#max-word-length {:type "number", :name "max-word-length", :value max-word-length}]
     [:button {:type "submit"} "Other!"]]]
   [:section.explanation
    [:p
     "See also: "
     [:a {:href "http://xkcd.com/936/"} "XKCD - Password Strength"]]]])

(defroutes handler
  (GET "/" [locale words max-word-length]
       (let [opts {:locale (or locale "en")
                   :words (if words (Long/parseLong words) 4)
                   :max-word-length (if max-word-length (Long/parseLong max-word-length) 10)}]
         (-> (render-index opts) layout html-ok no-cache)))

  (resources "/" {:root "public"})
  (not-found "nothing here.."))

(def app
  (-> handler
      wrap-keyword-params
      wrap-params))

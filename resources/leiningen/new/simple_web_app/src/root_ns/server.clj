(ns {{root-ns}}.server
  (:gen-class)
  (:require
    [compojure.core :refer [routes GET]]
    [compojure.route :as route]
    [environ.core :refer [env]]
    [hiccup.element :refer [javascript-tag]]
    [hiccup.page :refer [html5 include-css include-js]]
    [org.httpkit.server :as httpkit]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.reload :refer [wrap-reload]]
    [ring.middleware.webjars :refer [wrap-webjars]]
    [ring.util.response :refer [response]]))

(defonce http-server (atom nil))

(defn render-home-page
  []
  (html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE-edge"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:title "{{name}} :: Home Page"]
     (include-css "css/app.css")
     (include-js "cljs/app.js")]
    [:body
     [:div#app [:h1 "Waiting for ClojureScript to load ..."]]
     (javascript-tag "{{sanitized}}.client.run();")]))

(def app-routes
  (routes
    (GET "/" [] (render-home-page))
    (route/not-found "not found")))

(def handler
  (as-> #'app-routes h
        (if (:dev? env) (wrap-reload h) h)
        (wrap-defaults h (assoc-in site-defaults [:security :anti-forgery] false))
        (wrap-webjars h)))

(defn stop-server!
  []
  (when-not (nil? @http-server)
    (println "http-kit server stopping ...")
    (httpkit/server-stop! @http-server)
    (reset! http-server nil)))

(defn start-server!
  []
  (let [config {:port                 8080
                :legacy-return-value? false}]
    (reset! http-server (httpkit/run-server #'handler config))
    (println "http-kit server status:" (httpkit/server-status @http-server) " port:" (httpkit/server-port @http-server))
    @http-server))

(defn -main
  [& args]
  (start-server!))

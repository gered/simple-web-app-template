(ns {{root-ns}}.client
  (:require
    [reagent.dom :as rd]))

(defn main-app-component
  []
  [:div
   [:h1 "Hello, world!"]
   [:p "This is my Clojure web app."]])

(defn reload
  []
  (rd/render [main-app-component] (.getElementById js/document "app")))

(defn ^:export run
  []
  (enable-console-print!)
  (reload))

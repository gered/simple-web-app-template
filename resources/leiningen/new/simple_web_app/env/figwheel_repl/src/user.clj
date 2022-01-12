(ns user
  (:require
    [figwheel.main.api :as fig]))

(println "** Run (start) to begin the Figwheel build and start a REPL.")
(println "** Afterwards you can run (cljs-repl) from other nREPL sessions to get a CLJS REPL elsewhere.")

; your main/primary/only(?) build as defined in figwheel configs
(def default-build-id "app")

(defn start
  "start the figwheel build process and (eventually) results in a figwheel repl"
  [& [build-id]]
  (fig/start (or build-id default-build-id)))

(defn cljs-repl
  [& [build-id]]
  "starts a cljs repl for an already running figwheel build"
  (fig/cljs-repl (or build-id default-build-id)))

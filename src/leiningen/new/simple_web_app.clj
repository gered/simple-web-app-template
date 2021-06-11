(ns leiningen.new.simple-web-app
  (:require
    [leiningen.new.templates :as t]
    [leiningen.core.main :as main]))

(def render (t/renderer "simple-web-app"))

(defn simple-web-app
  [name]
  (let [data {:name         name
              :sanitized    (t/sanitize name)
              :root-ns      (t/sanitize-ns name)
              :root-ns-path (t/name-to-path name)}]
    (main/info (str "Creating a new simple-web-app Clojure/ClojureScript web app project \"" name "\" ..."))
    (t/->files
      data
      ["env/dev/src/user.clj"              (render "env/dev/src/user.clj" data)]
      "env/prod/src"
      ["resources/public/css/app.css"      (render "resources/public/css/app.css" data)]
      ["src/{{root-ns-path}}/client.cljs"  (render "src/root_ns/client.cljs" data)]
      ["src/{{root-ns-path}}/server.clj"   (render "src/root_ns/server.clj" data)]
      [".gitignore"                        (render "gitignore" data)]
      ["project.clj"                       (render "project.clj" data)])))

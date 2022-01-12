(ns leiningen.new.simple-web-app
  (:require
    [leiningen.new.templates :as t]
    [leiningen.core.main :as main]))

(def render (t/renderer "simple_web_app"))

(defn simple-web-app
  [name]
  (let [data {:name         name
              :sanitized    (t/sanitize name)
              :root-ns      (t/sanitize-ns name)
              :root-ns-path (t/name-to-path name)}]
    (main/info (str "Creating new project via net.gered/simple-web-app called \"" name "\" ..."))
    (t/->files
      data
      "env/dev/resources"
      "env/dev/src"
      "env/release/resources"
      "env/release/src"
      "env/figwheel_repl/resources"
      ["env/figwheel_repl/src/user.clj"        (render "env/figwheel_repl/src/user.clj" data)]
      ["resources/logback.xml"                 (render "resources/logback.xml" data)]
      ["resources/public/css/app.css"          (render "resources/public/css/app.css" data)]
      ["src/{{root-ns-path}}/client.cljs"      (render "src/root_ns/client.cljs" data)]
      ["src/{{root-ns-path}}/server.clj"       (render "src/root_ns/server.clj" data)]
      ["test/{{root-ns-path}}/server_test.clj" (render "test/root_ns/server_test.clj" data)]
      [".gitignore"                            (render "gitignore" data)]
      ["app.cljs.edn"                          (render "app.cljs.edn" data)]
      ["figwheel-main.edn"                     (render "figwheel-main.edn" data)]
      ["config.edn"                            (render "config.edn" data)]
      ["project.clj"                           (render "project.clj" data)])))

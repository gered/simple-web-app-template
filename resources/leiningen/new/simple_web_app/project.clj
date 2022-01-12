(defproject {{name}} "0.1.0-SNAPSHOT"

  :description  "FIXME: write description"
  :url          "http://example.com/FIXME"
  :license      {:name "MIT License"
                 :url  "http://opensource.org/licenses/MIT"}

  :dependencies [[ch.qos.logback/logback-classic "1.2.7"]
                 [cprop "0.1.19"]
                 [hiccup "1.0.5"]
                 [http-kit "2.5.3"]
                 [javax.servlet/servlet-api "2.5"]
                 [metosin/reitit-core "0.5.15"]
                 [metosin/reitit-dev "0.5.15"]
                 [metosin/reitit-middleware "0.5.15"]
                 [metosin/reitit-schema "0.5.15"]
                 [metosin/reitit-ring "0.5.15"]
                 [metosin/ring-http-response "0.9.3"]
                 [mount "0.1.16"]
                 [net.gered/aging-session "0.2.0"]
                 [nrepl "0.9.0"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.logging "1.2.1"]
                 [org.webjars.npm/mini.css "3.0.1"]
                 [org.webjars/webjars-locator "0.42"]
                 [ring/ring-anti-forgery "1.3.0"]
                 [ring/ring-devel "1.9.4"]
                 [ring-webjars "0.2.0"]

                 [cljs-ajax "0.8.4"]
                 [cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]
                 [org.clojure/clojurescript "1.10.773"]
                 [metosin/reitit-frontend "0.5.15"]
                 [reagent "1.1.0"]]

  :plugins      [[lein-cljsbuild "1.1.8"]]

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :main         {{root-ns}}.server

  :repl-options {:init-ns {{root-ns}}.server}
  :target-path  "target/%s/"

  :profiles     {:dev             {:source-paths   ["env/dev/src"]
                                   :resource-paths ["env/dev/resources"]
                                   :dependencies   [[pjstadig/humane-test-output "0.11.0"]
                                                    [cider/piggieback "0.5.3"]
                                                    [com.bhauman/figwheel-main "0.2.15"]
                                                    [com.bhauman/rebel-readline-cljs "0.1.4"]]
                                   :injections     [(require 'pjstadig.humane-test-output)
                                                    (pjstadig.humane-test-output/activate!)]
                                   :repl-options   {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                                   :cljsbuild      {:builds {:app
                                                             {:source-paths ["src" "env/dev/src"]}}}}

                 :figwheel-repl   {:source-paths   ["env/figwheel_repl/src"]
                                   :resource-paths ["env/figwheel_repl/resources"]
                                   :repl-options   {:skip-default-init true
                                                    :init-ns           user
                                                    :port              5000}}

                 :release         {:source-paths   ["env/release/src"]
                                   :resource-paths ["env/release/resources"]
                                   :hooks          [leiningen.cljsbuild]
                                   :cljsbuild      {:jar    true
                                                    :builds {:app
                                                             {:compiler ^:replace {:source-paths  ["src" "env/release/src"]
                                                                                   :output-to     "resources/public/cljs/app.js"
                                                                                   :optimizations :advanced
                                                                                   :pretty-print  false}}}}}

                 :release/uberjar {:omit-source    true
                                   :aot            :all}

                 :uberjar         [:release :release/uberjar]}

  :cljsbuild    {:builds
                 {:app
                  {:source-paths ["src"]
                   :compiler     {:main          {{root-ns}}.client
                                  :output-to     "resources/public/cljs/app.js"
                                  :output-dir    "resources/public/cljs/target"
                                  :asset-path    "cljs/target"
                                  :source-map    true
                                  :optimizations :none
                                  :pretty-print  true}}}}

  :aliases      {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
                 "fig:build" ["trampoline" "run" "-m" "figwheel.main" "--build" "app" "--repl"]
                 "fig:clean" ["trampoline" "run" "-m" "figwheel.main" "--clean" "app"]
                 "fig:nrepl" ["with-profile" "+figwheel-repl" "repl"]}

  )

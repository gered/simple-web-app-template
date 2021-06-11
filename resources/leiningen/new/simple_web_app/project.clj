(defproject {{name}} "0.1.0-SNAPSHOT"

  :dependencies [[cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]
                 [compojure "1.6.2"]
                 [environ "1.2.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.866"]
                 [http-kit "2.5.3"]
                 [reagent "1.1.0"]
                 [ring "1.9.3"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-defaults "0.3.2" :exclusions [javax.servlet/servlet-api]]]

  :plugins       [[lein-cljsbuild "1.1.8"]
                  [lein-environ "1.2.0"]
                  [lein-figwheel "0.5.20"]]

  :main          {{root-ns}}.server

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :figwheel      {:css-dirs ["resources/public/css"]}

  :cljsbuild     {:builds
                  {:app
                   {:source-paths ["src"]
                    :figwheel     {:on-jsload {{root-ns}}.client/reload}
                    :compiler     {:main          {{root-ns}}.client
                                   :output-to     "resources/public/cljs/app.js"
                                   :output-dir    "resources/public/cljs/target"
                                   :asset-path    "cljs/target"
                                   :source-map    true
                                   :optimizations :none
                                   :pretty-print  true}}}}

  :profiles      {:dev     {:env          {:dev? true}
                            :source-paths ["env/dev/src"]
                            :dependencies [[figwheel-sidecar "0.5.20"]
                                           [nrepl "0.8.3"]
                                           [cider/piggieback "0.5.2"]]
                            :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                            :figwheel     {:nrepl-port 7000}
                            :cljsbuild    {:builds {:app
                                                    {:source-paths ["src" "env/dev/src"]}}}}

                  :uberjar {:source-paths ["env/prod/src"]
                            :aot          :all
                            :hooks        [leiningen.cljsbuild]
                            :omit-source  true
                            :cljsbuild    {:jar    true
                                           :builds {:app
                                                    {:compiler ^:replace {:source-paths ["src" "env/prod/src"]
                                                                          :output-to     "resources/public/cljs/app.js"
                                                                          :optimizations :advanced
                                                                          :pretty-print  false}}}}}}

  :aliases       {"uberjar" ["do" ["clean"] ["uberjar"]]}

  )

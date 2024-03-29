{{=<% %>=}}
(ns <%root-ns%>.server
  (:gen-class)
  (:require
    [clojure.tools.logging :as log]
    [aero.core :refer [read-config]]
    [aging-session.core :as aging-session]
    [hiccup.element :refer [javascript-tag]]
    [hiccup.page :refer [html5 include-css include-js]]
    [org.httpkit.server :as http-kit]
    [mount.core :as mount :refer [defstate]]
    [muuntaja.core :as m]
    [nrepl.server :as nrepl]
    [reitit.coercion.schema]
    [reitit.ring :as ring]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery *anti-forgery-token*]]
    [ring.middleware.cookies :refer [wrap-cookies]]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.flash :refer [wrap-flash]]
    [ring.middleware.reload :refer [wrap-reload]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.middleware.webjars :refer [wrap-webjars]]
    [ring.util.http-response :refer :all]
    [schema.core :as s]))

(declare handler)

;;
;; infrastructure components
;;

(defstate ^{:on-reload :noop} config
  :start
  (do
    (log/info "Loading config.edn")
    (read-config "config.edn")))

(defstate ^{:on-reload :noop} repl-server
  :start
  (let [{:keys [port bind]
         :or   {port 7000
                bind "127.0.0.1"}} (:nrepl config)
        server (nrepl/start-server :port port :bind bind)]
    (log/info (format "Starting nREPL server listening on %s:%d" bind port))
    server)
  :stop
  (when repl-server
    (log/info "Stopping nREPL server")
    (nrepl/stop-server repl-server)))

(defstate ^{:on-reload :noop} http-server
  :start
  (let [{:keys [port bind]
         :or   {port 8080
                bind "0.0.0.0"}} (:http config)
        server (http-kit/run-server
                 (as-> #'handler h
                       (if (:dev? config) (wrap-reload h) h))
                 {:port                 port
                  :ip                   bind
                  :server-header        nil
                  :legacy-return-value? false})]
    (log/info (format "Started HTTP server listening on %s:%d" bind port))
    server)
  :stop
  (when http-server
    (log/info "Stopping HTTP server")
    (http-kit/server-stop! http-server)
    nil))

(defstate ^{:on-reload :noop} session-store
  :start
  (let [session-timeout (get config :session-timeout (* 60 20))]
    (aging-session/aging-memory-store session-timeout))
  :stop
  (aging-session/stop session-store))


;;
;; main web handler and route stuff
;;

(defn render-page
  [& content]
  (html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:meta {:name "csrf-token" :content *anti-forgery-token*}]
     [:title "<%name%>"]
     (include-css "/assets/mini.css/dist/mini-default.css"
                  "/css/app.css")
     (include-js "/cljs/app.js")]
    [:body
     (or content
         [:div#app "Loading. Just a sec ..."])
     (javascript-tag "<%sanitized%>.client.main();")]))

(defn render-error
  ([e]
   (render-error
     (.getName (.getClass e))
     (.getMessage e)))
  ([title message]
   (render-page
     [:h2 "Error!"]
     [:div.card.fluid.error
      [:h3 title]
      [:p message]])))

; example exception handler that logs all unhandled exceptions thrown by your routes
(def exception-middleware
  (exception/create-exception-middleware
    (merge
      exception/default-handlers
      {::exception/default
       (fn [e {:keys [uri request-method remote-addr] :as request}]
         (log/error e (format "Unhandled exception during request - %s %s from %s"
                              request-method uri remote-addr))
         ; for "api" routes, we want non-html responses to (maybe?) make it easier for cljs ajax error handlers
         (if (get-in request [:reitit.core/match :data :api?])
           (exception/default-handler e request)
           (internal-server-error (render-error e))))})))

(defstate handler
  :start
  (ring/ring-handler
    (ring/router
      [["/"
        {:get
         (fn [request]
           (ok (render-page)))}]

       ["/api" {:api? true} ; :api? is our own custom flag that we use in the example exception-middleware (see above)

        ; example route
        ["/calculate"
         {:post {:parameters {:body {:a  s/Num
                                     :b  s/Num
                                     :op s/Str}}
                 :responses  {200 {:body {:result s/Num}}
                              400 {:body s/Str}}
                 :handler    (fn [{{{:keys [a b op]} :body} :parameters}]
                               (log/info "Performing math calculation: " a op b)
                               (ok
                                 {:result
                                  (float (case op
                                           "+" (+ a b)
                                           "-" (- a b)
                                           "*" (* a b)
                                           "/" (/ a b)
                                           (bad-request! {:what "Invalid operation"})))}))}}]]

       ]

      {:data {:coercion   reitit.coercion.schema/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware ; query-params & form-params
                           muuntaja/format-negotiate-middleware ; content-negotiation
                           muuntaja/format-response-middleware ; encoding response body
                           exception-middleware   ; exception handling
                           muuntaja/format-request-middleware ; decoding request body
                           coercion/coerce-response-middleware ; coercing response body
                           coercion/coerce-request-middleware ; coercing request parameters
                           multipart/multipart-middleware   ; multipart
                           wrap-cookies
                           [wrap-session {:store session-store}]
                           wrap-flash
                           wrap-anti-forgery]}})

    (ring/routes
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found          (constantly (not-found (html5 [:h2 "404 Not Found"])))
         :method-not-allowed (constantly (method-not-allowed (html5 [:h2 "405 Method Not Allowed"])))
         :not-acceptable     (constantly (not-acceptable (html5 [:h2 "406 Not Acceptable"])))}))))


;;

(defn -main
  [& args]
  (log/info "<%name%> is starting up ...")
  (mount/start-with-args args)
  (when (:dev? config)
    (log/warn "*** Running in development mode! ***"))
  (log/info "Ready!"))

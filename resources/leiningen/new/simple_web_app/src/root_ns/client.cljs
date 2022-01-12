{{=<% %>=}}
(ns ^:figwheel-hooks <%root-ns%>.client
  (:require
    [clojure.string :as string]
    [ajax.core :as ajax]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [reitit.core]
    [reitit.frontend :as rf]
    [reitit.frontend.easy :as rfe]
    [reitit.coercion.schema :as rcs]
    [schema.core :as s]))

; will hold the current reitit.core/Match for the page being viewed, or nil if no match
(defonce current-route (r/atom nil))


;;
;; main/root page reagent components
;;

(defn home-page
  [match]
  [:div.container
   [:h1 "Hello!"]
   [:p "Welcome to the <%name%> web app."]])

(defn about-page
  [match]
  [:div.container
   [:h1 "About"]
   [:p "There is nothing much to say right now ..."]])

; semi-complex example that does ajax things ...
(defn calculator
  []
  (let [error  (r/atom nil)
        result (r/atom nil)
        values (r/atom {:op "+"})]
    (fn []
      [:div
       (when @error
         [:div.card.fluid.error
          [:h3 "Error!"]
          [:p (pr-str @error)]])
       (when @result
         (let [{:keys [a b op]} @result]
           [:div.card.fluid.warning
            [:h3 "Calculation Result"]
            [:p a " " op " " b " = " (:result @result)]]))

       [:div [:label "A:"
              [:input {:type      "text"
                       :value     (:a @values)
                       :on-change #(swap! values assoc :a (-> % .-target .-value))}]]]
       [:div [:label "B:"
              [:input {:type      "text"
                       :value     (:b @values)
                       :on-change #(swap! values assoc :b (-> % .-target .-value))}]]]
       [:div [:label "Operation:"
              [:select {:on-change #(swap! values assoc :op (-> % .-target .-value))}
               [:option "+"]
               [:option "-"]
               [:option "*"]
               [:option "/"]
               [:option "foo"]]]]
       [:div
        [:button
         {:on-click
          #(ajax/POST
             "/api/calculate"
             {:params        (-> @values
                                 (select-keys [:a :b :op])
                                 (update :a (fn [a] (if-not (string/blank? a) (js/parseFloat a))))
                                 (update :b (fn [b] (if-not (string/blank? b) (js/parseFloat b)))))
              :handler       (fn [response]
                               (reset! error nil)
                               (reset! result
                                       (merge
                                         (select-keys @values [:a :b :op])
                                         response)))
              :error-handler (fn [{:keys [response]}]
                               (reset! result nil)
                               (reset! error response))})}
         "Calculate"]]])))

(defn calculator-page
  [match]
  [:div.container
   [:h1 "Calculator"]
   [calculator]])

(defn not-found-page
  [match]
  [:div.card.fluid.error
   [:h3 "Not Found"]
   [:p "The page you requested was not found."]])


;;
;; root page rendering component
;;

(defn page-root
  []
  [:div#root
   [:header.sticky
    [:a.logo {:href "#/"} "<%name%>"]
    [:a.button {:href "#/calculator"} "Calculator"]
    [:a.button {:href "#/about"} "About"]]
   [:main
    (if-let [view (get-in @current-route [:data :view])]
      [view @current-route]
      [not-found-page @current-route])]
   [:footer
    [:div "This is my footer"]]])


;;
;; reitit routes
;;

(def routes
  [["/"
    {:name ::home-page
     :view home-page}]

   ["/calculator"
    {:name ::calculator-page
     :view calculator-page}]

   ["/about"
    {:name ::about-page
     :view about-page}]])


;;

(defn ^:export ^:after-load main
  []
  (enable-console-print!)

  ; locate a <meta> tag with the csrf-token in it and if found, add a cljs-ajax request interceptor
  ; with the csrf-token, so that all our future ajax requests will include an http header with it
  (when-let [csrf-token-element (.querySelector js/document "meta[name=\"csrf-token\"]")]
    (let [csrf-token  (.-content csrf-token-element)
          interceptor (ajax/to-interceptor
                        {:name    "CSRF Interceptor"
                         :request #(assoc-in % [:headers "X-CSRF-Token"] csrf-token)})]
      (swap! ajax/default-interceptors #(cons interceptor %))))

  ; start the reitit router, reacting to HTML5 History and hashchange events
  (rfe/start!
    (rf/router routes {:data {:coercion rcs/coercion}})
    (fn [match history]
      (reset! current-route match))
    {:use-fragment true})

  (rdom/render
    [page-root]
    (.getElementById js/document "app")))

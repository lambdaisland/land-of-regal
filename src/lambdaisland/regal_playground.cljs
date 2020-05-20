(ns lambdaisland.regal-playground
  (:require [cljs.reader :as reader]
            [lambdaisland.regal :as regal]
            [lambdaisland.regal.generator :as generator]
            [lambdaisland.regal.parse :as parse]
            [reagent.core :as reagent]
            [reagent.dom :as reagent-dom]
            [clojure.pprint :as pprint]))


(defn pprint-str [form]
  (with-out-str
    (pprint/pprint form)))

(defn try-read [form]
  (try
    (reader/read-string form)
    (catch :default e)))

(defn regal-form [state]
  (try-read (:regal state)))

(defn regal-pattern [state]
  (when-let [form (regal-form state)]
    (regal/with-flavor (:flavor state)
      (regal/pattern form))))

(defn compiled-regex [state]
  (when-let [pattern (regal-pattern state)]
    (regal/compile pattern)))

(defn derive-pattern [state]
  (assoc state :pattern (or (regal-pattern state) "")))

(defn derive-result [state]
  (let [result (try
                 (re-find (compiled-regex state) (:input state))
                 (catch :default e
                   "Error!"))]
    (assoc state :result result)))

(defn generate-values [state]
  (if-let [form (regal-form state)]
    (assoc state
           :gen
           (generator/sample form))
    state))

(defonce state
  (let [form [:cat
              [:capture [:cat [:class [\A \Z]] [:+ [:class [\a \z]]]]]
              [:+ :whitespace]
              [:capture [:cat [:class [\A \Z]] [:+ [:class [\a \z]]]]]]]
    (reagent/atom (-> {:regal        (pprint-str form)
                       :flavor       :ecma
                       :parse-error? false
                       :input        "Lambda Island"
                       :examples {:url {:content "https://www.lambdaisland.com"
                                        :form [:a]} ;FIX
                                  :email {:content "johndoe@example.com"
                                          :form [:a]} ;FIX
                                  :cats {:content "Tom, Cheshire, Sylvester, Garfield and Hobes"
                                         :form [:a]} ;FIX
                                  :password {:content "ReallyHardP4s$"}}}
                      derive-pattern
                      derive-result
                      generate-values))))

(defn- !swap-example [example-type]
  (let [{:keys [content form]} (get-in @state [:examples example-type])]
    (swap! state
           (fn [state]
             (-> state
                 (assoc :input content))))
    (swap! state
           (fn [state]
             (-> state
                 (assoc :form form))))))

(defn- main-input []
  (let [input (:input @state)]
    [:input {:type "text" :value input :on-change
             (fn [e]
               (swap! state
                      (fn [state]
                        (let [text (.. e -target -value)]
                          (-> state
                              (assoc :input text)
                              derive-result)))))}]))

(defn- fill-with [type description]
  [:a {:href "#" :on-click #(!swap-example type)} description])

(defn- show-result []
  [:input {:type "text" :value (pr-str (:result @state))}])

(defn app []
  (let [{:keys [regal parse-error? flavor input pattern result gen]} @state]
    [:main.layout
     [:article
      [:p "Hi. The following is just a text input field, so you are free to
      change it."]
      [main-input]
      [:p "To aid in the manipulation of strings of text (the "
       [:i "lingua franca"]
       " of the digital sea) like the one above—and this one—, programmers
       felt the need to navigate them with precision. To do that, from the early
       days of Unix to this day, they have leveraged the power of "
       [:a {:href "https://en.wikipedia.org/wiki/Regular_expression"
            :target "_blank"}
        "regular expressions"]
       ". Regexes or regexps (as they are referenced among string traders), are
        used (and abused) to perform common operations on strings such as
        search, search and replace, information extraction and input
        validation."]
      [:input {:type "text"
               :value pattern
               :on-change
               (fn [e]
                 (swap! state
                        (fn [state]
                          (let [text (.. e -target -value)]
                            (try
                              (-> state
                                  (assoc
                                   :regal (pprint-str (parse/parse-pattern text))
                                   :pattern text)
                                  derive-result
                                  generate-values)
                              (catch :default e
                                (js/console.log e)
                                (assoc :regal (pprint-str [:error e])
                                       :patter text
                                       :result "")))))))}]
      [show-result]
      [:p "To get a taste of just how powerful they can be, try "
       [fill-with :url "breaking down an URL"] " or "
       [fill-with :email "an email address"] "; or to "
       [fill-with :cats
        "find the craziest cat in a list of crazy cats"] "; or even to "
       [fill-with :password
        "find the symbols, capital letters or numbers in a given password"] ",
        so that you can " [:strike "annoy"] " discipline your users."]
      [:p
       "Regexes are a powerful tool in any programmer tool belt,
       but they are hard to read, hard to maintain and have different
       implementation details, which brings a lot of confusion to the table."]
      [:p
       "Check out this list of " [:i "pearls"] " (pun intended) found in real
       world applications."]
      [:ul
       [:li "pearl 1"]
       [:li "pearl 2"]
       [:li "pearl 3"]]
      [:p
       "Scary much? Even if you like them (they’re a bit like spell craft, so
       they do have a charm) and should learn how to read and write them,
       maintaining such arcane incantations in the long run can be tedious. Not
       only you need to wrap your head around all the idiomatic idiosyncrasies
       every time (especially if moving between different implementations), but
       if you need to change something, it gets hard to track."]
      [:img.logo {:src "./images/02_crown@32x32@10x.png"}]
      [:h1.title "Regal"]
      [:p.subtitle "Royally reified regular expressions"]
      [:p
       "As Clojure people, when we have to deal with powerful dark arts (looking
       at you, HTML) with not so great markup formats, we know what to do: just
       cast them as pure Clojure data structures! The result of applying that
       sorcery to regexes is Regal."]]
     [:div.area.regal-form
      [:h2.area-title "Regal form"]
      [:textarea {:value regal
                  :on-change
                  (fn [e]
                    (swap! state
                           (fn [state]
                             (let [text (.. e -target -value)]
                               (if-let [form (try-read text)]
                                 (-> (assoc state
                                            :regal text
                                            :parse-error? false)
                                     derive-pattern
                                     derive-result
                                     generate-values)
                                 (assoc state
                                        :regal text
                                        :pattern ""
                                        :parse-error? true
                                        :result ""))))))}]
      (when parse-error?
        [:p "Parse error!"])]
     [:div.area.flavor "Flavor: "
      (for [f [:ecma :java8 :java9]]
        ^{:key (str f)}
        [:div
         [:input (cond-> {:type "radio"
                          :id (name f)
                          :value (name f)
                          :name "flavor"
                          :on-change (fn [_]
                                       (swap! state #(-> %
                                                         (assoc :flavor f)
                                                         derive-pattern
                                                         derive-result)))}
                   (= f flavor)
                   (assoc :checked true))]
         [:label {:for (name f)} (str f)]])]
     [:div.area.regex
      [:h2.area-title "Regex"]
      [:input {:type "text"
               :value pattern
               :on-change
               (fn [e]
                 (swap! state
                        (fn [state]
                          (let [text (.. e -target -value)]
                            (try
                              (-> state
                                  (assoc
                                   :regal (pprint-str (parse/parse-pattern text))
                                   :pattern text)
                                  derive-result
                                  generate-values)
                              (catch :default e
                                (js/console.log e)
                                (assoc :regal (pprint-str [:error e])
                                       :patter text
                                       :result "")))))))}]]
     [:div.area.input-string
      [:h2.area-title "Input string"]
      [:input {:type "text" :value input :on-change
               (fn [e]
                 (swap! state
                        (fn [state]
                          (let [text (.. e -target -value)]
                            (-> state
                                (assoc :input text)
                                derive-result)))))}]]
     [:div.area.result
      [:h2.area-title "Result"]
      [:input {:type "text" :value (pr-str result)}]]
     [:div.area.generator
      [:h2.area-title "Generator"]
      [:p "generated example"]
      (into
       [:ul]
       (for [s gen]
         [:ul s]))
      [:button
       {:on-click #(swap! state generate-values)}
       "New sample"]]]))

(reagent-dom/render
 [app]
 (js/document.getElementById "app"))

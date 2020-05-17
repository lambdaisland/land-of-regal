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
                       :input        "Lambda Island"}
                      derive-pattern
                      derive-result
                      generate-values))))


(defn app []
  (let [{:keys [regal parse-error? flavor input pattern result gen]} @state]
    [:main.layout
     [:div.area.plaintext
      [:p "cool description of regexes history and rationale here"]
      [:p "with links to repo and lambda island"]
      [:p "or some sort of cheat sheet"]]
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
                                     derive-result)
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
                                  derive-result)
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

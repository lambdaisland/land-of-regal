(ns lambdaisland.regal-playground
  (:require [cljs.reader :as reader]
            [lambdaisland.regal :as regal]
            [lambdaisland.regal.generator :as generator]
            [lambdaisland.regal.parse :as parse]
            [reagent.core :as reagent]
            [reagent.dom :as reagent-dom]
            [clojure.pprint :as pprint]))

(def regal-form
  [:cat
   [:capture
    [:cat
     [:class [\A \Z]]
     [:+ [:class [\a \z]]]]]
   [:+ :whitespace]
   [:capture
    [:cat
     [:class [\A \Z]]
     [:+ [:class [\a \z]]]]]])

(defn pprint-str [form]
  (with-out-str
    (pprint/pprint form)))

(defonce state (reagent/atom {:regal        (pprint-str regal-form)
                              :flavor       :ecma
                              :parse-error? false
                              :pattern      (regal/pattern regal-form)
                              :input        "Lambda Island"
                              :result       (re-find (regal/regex regal-form) "Lambda Island")}))

(defn try-read [form]
  (try
    (reader/read-string form)
    (catch :default e)))

(defn app []
  (let [{:keys [regal parse-error? flavor input pattern result]} @state]
    [:main.layout
     [:div.area.plaintext
      [:p "cool description of regexes history and rationale here"]
      [:p "with links to repo and lambda island"]
      [:p "or some sort of cheat sheet"]]
     [:div.area.regal-form
      [:h2 "Regal form"]
      (when parse-error?
        [:p "Parse error!"])
      [:textarea {:value regal
                  :on-change
                  (fn [e]
                    (swap! state
                           (fn [state]
                             (let [text (.. e -target -value)]
                               (if-let [form (try-read text)]
                                 (assoc state
                                        :regal text
                                        :parse-error? false
                                        :pattern (regal/pattern form))
                                 (assoc state
                                        :regal text
                                        :parse-error? true))))))}]]
     [:div.area.flavor "Flavor: " (str flavor)]
     [:div.area.regex
      [:h2 "Regex"]
      [:input {:type "text"
               :value pattern
               :on-change
               (fn [e]
                 (swap! state
                        (fn [state]
                          (let [text (.. e -target -value)]
                            (try
                              (assoc state
                                     :regal (pprint-str (parse/parse-pattern text))
                                     :pattern text)
                              (catch :default e
                                (js/console.log e)))))))}]]
     [:div.area.input-string
      [:h2 "Input string"]
      [:input {:type "text" :value input}]]
     [:div.area.result
      [:h2 "Result"]
      [:input {:type "text" :value (pr-str result)}]]]))

(reagent-dom/render
 [app]
 (js/document.getElementById "app"))

;; REGAL FORM:

;; [:cat
;;  [:capture
;;   [:cat
;;    [:class [\A \Z]]
;;    [:+ [:class [\a \z]]]]]
;;  [:+ :whitespace]
;;  [:capture
;;   [:cat
;;    [:class [\A \Z]]
;;    [:+ [:class [\a \z]]]]]]

;; FLAVOR: [*] Java 8   [*] Java 9    [*] JavaScript

;; REGEX: #"([A-Z][a-z]+)\s+([A-Z][a-z]+)"

;; INPUT STRING: Arne Brasseur

;; RESULT: ["Arne Brasseur" "Arne" "Brasseur"]

;; GENERATED: [Update]
;; '("Ys Lh"
;;   "Mi Bw"
;;   "Rfe  Xfog"
;;   "Njh   Gai"
;;   "Uw \n\r Yp"
;;   "Km　\t Uh"
;;   "Zbmsngx Ohwtpks"
;;   "Zliusncy\n   Uvboxry"
;;   "Dwmneiy\fZfajiq"
;;   "Iqvh \t     Sfixk")

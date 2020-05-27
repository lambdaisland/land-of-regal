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
    (catch :default e
      (js/console.error e))))

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
  (assoc state :pattern (or
                         (try
                           (regal-pattern state)
                           (catch :default e
                             (js/console.error e)))
                         "")))

(defn derive-result [state]
  (let [result (try
                 (re-seq (compiled-regex state) (:input state))
                 (catch :default e
                   (js/console.error e)
                   "Error!"))]
    (assoc state :result result)))

(defn generate-values [state]
  (try
    (if-let [form (regal-form state)]
      (assoc state
             :gen
             (doall (generator/sample form)))
      state)
    (catch :default e
      (js/console.error e)
      (assoc state :gen ()))))

(defonce state
  (let [form [:+ :word]]
    (reagent/atom (-> {:regal        (pprint-str form)
                       :flavor       :ecma
                       :parse-error? false
                       :input        "Royally Reified Regular Expressions"
                       :examples {:url {:content "https://www.lambdaisland.com"
                                        :form [:cat
                                               :start
                                               [:? [:capture [:cat [:capture [:+ [:not ":/?#"]]] ":"]]]
                                               [:? [:capture [:cat "//" [:capture [:* [:not "/?#"]]]]]]
                                               [:? [:capture [:* [:not "?#"]]]]
                                               [:? [:capture [:cat "?" [:capture [:* [:not "#"]]]]]]
                                               [:? [:capture [:cat "#" [:capture [:* :any]]]]]
                                               :end]}
                                  :email {:content "johndoe@example.com"
                                          :form [:cat
                                                 [:+ [:class :word ".%+-"]]
                                                 "@"
                                                 [:+ [:class ["A" "Z"] ["a" "z"] ["0" "9"] ".-"]]
                                                 "."
                                                 [:repeat [:class ["A" "Z"] ["a" "z"]] "2" "4"]]}
                                  :cats {:content "Tom, Cheshire, Sylvester, Garfield and Hobes"
                                         :form [:cat
                                                [:class ["A" "Z"]]
                                                [:+ [:class ["a" "z"]]]]}
                                  :password {:content "ReallyHardP4s$"
                                             :form [:class :non-word :digit ["A" "Z"]]}}}
                      derive-pattern
                      derive-result
                      generate-values))))

(defn- swap-example! [example-type]
  (let [{:keys [content form]} (get-in @state [:examples example-type])]
    (swap! state
           (fn [state]
             (-> state
                 (assoc :input content
                        :regal (pprint-str form))
                 derive-result
                 derive-pattern)))))

(defn- main-input []
  (let [input (:input @state)]
    [:<>
     [:label "Input"]
     [:div
      [:input {:type "text" :value input :on-change
               (fn [e]
                 (swap! state
                        (fn [state]
                          (let [text (.. e -target -value)]
                            (-> state
                                (assoc :input text)
                                derive-result)))))}]]]))

(defn- fill-with [type description]
  [:a {:href "#" :on-click (fn [^js e]
                             (.preventDefault e)
                             (swap-example! type))} description])

(defn- show-result []
  [:<>
   [:label "Result"]
   [:code (pprint-str (:result @state))]])

(defn cheatsheet []
  [:<>
   [:p
    "Strings and characters match literally. They are escaped, so "
    [:code "."]
    " matches a\nperiod, not any character, "
    [:code "^"]
    " matches a caret, etc."]
   [:h3
    "Keywords with special meaning"]
   [:ul
    [:li
     [:code ":any"]
     [:p "Match any character, like "
      [:code "."]
      ". Does not match newlines."]]
    [:li [:code ":start"]
     [:p "Match the start of the input."]]
    [:li [:code ":end"]
     [:p "Match the end of the input."]]
    [:li [:code ":digit"]
     [:p "Match any digit (" [:code "0-9"] ")."]]
    [:li [:code ":non-digit"]
     [:p "Match non-digits (not " [:code "0-9"] ")."]]
    [:li [:code ":word"]
     [:p "Match word characters (" [:code "A-Za-z0-9_"] ")."]]
    [:li
     [:code ":non-word"]
     [:p "Match non-word characters (not " [:code "A-Za-z0-9_"] ")."]]
    [:li [:code ":newline"] [:p "Match " [:code "\\n"] "."]]
    [:li [:code ":return"] [:p "Match " [:code "\\r"] "."]]
    [:li [:code ":tab"] [:p "Match " [:code "\\t"] "."]]
    [:li [:code ":form-feed"] [:p "Match " [:code "\\f"] "."]]
    [:li
     [:code ":line-break"]
     [:p
      "Match "
      [:code "\\n"]
      ", "
      [:code "\\r"]
      ", "
      [:code "\\r\\n"]
      ", or other unicode newline characters."]]
    [:li [:code ":alert"] [:p "Match " [:code "\\a"] " (U+0007)."]]
    [:li [:code ":escape"] [:p "Match " [:code "\\e"] " (U+001B)."]]
    [:li
     [:code ":whitespace"]
     [:p
      "Match any whitespace character. Uses "
      [:code "\\s"]
      " on JavaScript, and\na character range of whitespace characters on Java with equivalent semantics\nas JavaScript "
      [:code "\\s"]
      ", since "
      [:code "\\s"]
      " in Java only matches ASCII whitespace."]]
    [:li [:code ":non-whitespace"] [:p "Match non-whitespace."]]
    [:li
     [:code ":vertical-whitespace"]
     [:p "Match vertical whitespace, including newlines and vertical tabs "
      [:code "#\"\\n\\x0B\\f\\r\\x85\\u2028\\u2029\""] "."]]
    [:li
     [:code ":vertical-tab"] [:p
                              "Match a vertical tab "
                              [:code "\\v"]
                              " (U+000B)."]]
    [:li [:code ":null"] [:p "Match a NULL byte/char."]]]
   [:h3
    "Vectors with a keyword as the first element"]
   [:ul
    [:li
     [:code "[:cat forms...]"]
     [:p
      "Concatenation, match the given Regal expressions in order."]]
    [:li
     [:code "[:alt forms...]"]
     [:p
      "Alternatives, match one of the given options, like "
      [:code "(foo|bar|baz)"] "."]]
    [:li [:code "[:* form]"]
     [:p "Match the given form zero or more times."]]
    [:li [:code "[:+ form]"]
     [:p "Match the given form one or more times."]]
    [:li [:code "[:? form]"] [:p "Match the given form zero or one time."]]
    [:li
     [:code "[:class entries...]"]
     [:p "Match any of the given characters or ranges, with ranges given as two
     element vectors. E.g. "
      [:code "[:class [\\a \\z] [\\A \\Z] \"_\" \"-\"]"]
      " is equivalent to "
      [:code "[a-zA-Z_-]"] "."]]
    [:li
     [:code "[:not entries...]"]
     [:p "Like "
      [:code ":class"]
      ", but negates the result, equivalent to "
      [:code "[^...]"] "."]]
    [:li
     [:code "[:repeat form min max]"]
     [:p "Repeat a form a number of times, like "
      [:code "{2,5}"] "."]]
    [:li
     [:code "[:capture forms...]"]
     [:p
      "Capturing group with implicit concatenation of the given forms."]]
    [:li
     [:code "[:char number]"]
     [:p
      "A single character, denoted by its unicode codepoint."]]
    [:li
     [:code "[:ctrl char]"]
     [:p
      "A control character, e.g. "
      [:code "[:ctrl \\A]"]
      " => "
      [:code "^A"]
      " => "
      [:code "#\"\\cA\""] "."]]
    [:li
     [:code "[:lookahead ...]"]
     [:p
      "Match if followed by pattern, without consuming input."]]
    [:li
     [:code "[:negative-lookahead ...]"]
     [:p
      "Match if not followed by pattern."]]
    [:li [:code "[:lookbehind ...]"] [:p "Match if preceded by pattern."]]
    [:li
     [:code "[:negative-lookbehind ...]"]
     [:p "Match if not preceded by pattern."]]
    [:li
     [:code "[:atomic ...]"]
     [:p "Match without backtracking ("
      [:a
       {:shape "rect", :href "https://www.regular-expressions.info/atomic.html"}
       "atomic group"] ")."]]]])

(defn app []
  (let [{:keys [regal parse-error? flavor input pattern result gen]} @state]
    [:main.layout
     [:article
      [:div.copy-wrapper
       [:p "Greetings, wanderer. You have reached the gates
        of " [:strong "Regal"] ", a library for the programming languages
        Clojure and ClojureScript. This scroll you are reading is an interactive
        explainer. And this here below is a text field. Go ahead, change it!"]]
      [main-input]
      [:div.copy-wrapper
       [:p "Since the early days of computing, programmers have been
        manipulating pieces of text like the one above, or the one I’m writing
        now (and that you are reading). But text has a mind of its own."]

       [:p "To tame the wild sea of characters and strings, to control the
        languages of humans, early programmers created a language of their own,
        powerful and arcane, whose utterances would come to life, incessantly
        seeking patterns in the chaos."]

       [:p "These "
        [:a {:href   "https://en.wikipedia.org/wiki/Regular_expression"
             :target "_blank"}
         "regular expressions"]
        ", (regexes, regexps), appear in text editors and terminals, in sources
        and scripts, where they search and replace, extract and validate, to
        this very day."]

       [:p "Hush... Here comes one now. Still a youngster, of modest plumage."]]

      [:label "Regex"]
      [:div
       [:input {:type  "text"
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
                                 (js/console.error e)
                                 (assoc :regal (pprint-str [:error e])
                                        :patter text
                                        :result "")))))))}]]
      [:div.copy-wrapper.code

       [:code
        "(re-seq #\"" pattern "\", \"" input "\")"]]

      [show-result]
      [:div.copy-wrapper
       [:p "But they’re not nearly all as harmless as that wee regex. To get a
        taste of just how powerful they can be, try "
        [fill-with :url "breaking down an URL"] " or "
        [fill-with :email "an email address"] "; or to "
        [fill-with :cats
         "find all the cats in a list of crazy cats"] "; or to "
        [fill-with :password
         "find the symbols, capital letters or numbers in a given password."]]
       [:p "Not bad, right? Getting a good grip on regexes is a good investment
       in your career. But even seasoned programmers, or perhaps especially
       seasoned programmers, know that regex fatigue is real. Writing a
       hundred-character regex with nested capturing groups and a smattering of
       wildcards can be a challenge, but understanding that thing six months later
       to fix a bug is torture."]
       [:p "Languages are not static, they adapt and evolve, diverging into
       dialects, devolving to pidgins. And so it went with regular expressions.
       POSIX or Perl? JavaScript or Java? We have dozens of
       semi-mutually-intelligble dialects. Who is keeping track?"]
       [:p "When writing Clojure and ClojureScript this is felt accutely. Here
       we have two languages so alike, but their regular expressions are only
       and exactly what the platform offers. And Java and JavaScript regexes are
       only distant cousins at best."]]

      [:div.copy-wrapper
       [:img.logo
        {:src "images/02_crown@32x32@10x.png"}]]
      [:h1.title "Regal"]
      #_[:p.subtitle "Royally reified regular expressions"]
      [:p
       "As Clojure people, when we have to deal with powerful dark arts (looking
       at you, HTML) with not so great markup formats, we know what to do: just
       cast them as pure Clojure data structures! The result of applying that
       sorcery to regexes is Regal."]
      [:label.regal-form.interactive "Regal form"]
      [:div.regal-form.interactive
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
      [:div.copy-wrapper
       [:p "This regal form has fixed semantics. If you use it on Clojure then
       we emit a Java regex. If you use it on ClojureScript, you get a
       JavaScript regex. Sometimes these will differ, but they will match the
       exact same inputs."]]
      [:section.flavor
       [:label "Flavor"]
       [:div.flavors
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
           [:label {:for (name f)} ({:ecma  "JavaScript"
                                     :java8 "Java 8"
                                     :java9 "Java 9"} f)]])]]
      [:label "Resulting Regex"]
      [:div
       [:input {:type  "text"
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
                                 (js/console.error e)
                                 (assoc :regal (pprint-str [:error e])
                                        :patter text
                                        :result "")))))))}]]

      [:div.copy-wrapper
       [:p
        "There’s a lot you can do with Regal. We could give you a boring list of
        syntax to try, but the nice thing is that Regal can also parse regular
        expressions to Regal forms. So if you know regex you can teach yourself
        Regal easily. Try changing the regex and see how the regal form
        updates."]
       [:p "There may still be some regexes that the parser struggles with. If
       you find a regex that can be represented in Regal but that we are unable to parse then please "
        [:a {:href "https://github.com/lambdaisland/regal/issues"} "file an issue."]]
       [:p "(If you really want the boring list then have a look at the " [:a {:href "#syntax"} "syntax overview.)"]]

       [:p "Regal can not just match strings, it can also generate them by
       turning your regal forms into test.check compatible generators."]]

      [:label "Generator"]
      [:div
       (into
        [:ul.generator]
        (for [s gen]
          [:ul s]))
       [:button
        {:on-click #(swap! state generate-values)}
        "New sample"]]

      [:div.copy-wrapper
       [:p "Validating things, but also generating them, in an idiomatic and
      composable way? Sounds a lot like clojure.spec.alpha. Regal was designed
      with spec in mind, and plays with it nicely, as well as with Malli."]

       [:pre
        [:code "(require '[malli.core :as m]
         '[lambdaisland.regal.malli :as regal-malli])

(m/validate [:regal [:+ \"x\"]]
            \"xxx\"
            {:registry {:regal regal-malli/into-schema}})"]]

       [:h2#syntax "Syntax overview"]
       [cheatsheet]]]]))

(reagent-dom/render
 [app]
 (js/document.getElementById "app"))

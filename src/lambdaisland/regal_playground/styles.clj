(ns lambdaisland.regal-playground.styles
  (:require [garden.stylesheet :refer [at-font-face]]))


(def jet-brains-mono-regular
  (at-font-face
   {:font-family "JetBrainsMono Regular"
    :src "url('./fonts/JetBrainsMono-Regular.ttf')"}))

(def grid-areas "\"input  input input result    result\"
                 \"regal  regal regal result    result\"
                 \"regal  regal regal generator plaintext\"
                 \"flavor regex regex generator plaintext\"")

(def code [:code :textarea :input {:font-family "JetBrainsMono Regular"
                                   :font-weight 600}])
(def copy [:h1 :h2 :h3 :p :a {:font-family "ff-meta-serif-web-pro"
                              :font-weight 500
                              :line-height 1.6}])

(def ^{:garden {:output-to "resources/public/styles.css"}}
  main
  [jet-brains-mono-regular
   code
   copy
   [:article {:margin-top "10rem"}]
   [:.logo {:float :right}]
   [:.title {:font-size "1.5rem"
             :margin-top "5rem"
             :margin-bottom ".8rem"}]
   [:.subtitle {:font-size "1.3rem"
                :display "inline-block"
                :margin 0}]
   [:p {:font-size "1.125rem"
        :max-width 70ch}]
   [:.layout {:max-width "50vw"
              :margin "0 auto"}]
   ["input[type=\"text\"]" {:border :none
                            :font-size "1rem"
                            :width "100%"}]
   [:a {:text-decoration :none}]])

(ns lambdaisland.regal-playground.styles
  (:require [garden.stylesheet :refer [at-font-face at-media]]
            [garden.units :as u :refer [rem ch]]))

(def jet-brains-mono-regular
  (at-font-face
   {:font-family "JetBrainsMono Regular"
    :src "url('./fonts/JetBrainsMono-Regular.ttf')"}))

(def enable-grid
  (at-media {:min-width (u/px 768)}
            [:article {:display 'grid
                       :margin-top (rem 10)}]))

(def ^{:garden {:output-to "resources/public/styles.css"}}
  main
  [[:html {:font "1rem/1.6 \"ff-meta-serif-web-pro\", serif"
           :box-sizing 'border-box}]
   [:* {:box-sizing 'inherit}]
   jet-brains-mono-regular
   [:code :textarea :input {:font "600 1rem/1.6 \"JetBrainsMono Regular\", monospace"
                            :white-space 'pre-wrap}]
   [:main {:max-width 'max-content
           :margin "0 auto"}]
   [:article {:grid-template-columns "auto 1fr"
              :grid-column-gap (rem 1)
              :margin-top (rem 1)}
    ["> :not(label)" {:grid-column-start 2}]]
   enable-grid
   [:h1 :h2 :h3 :label {:font-variant :all-small-caps}]
   [:h1 {:font-size (rem 1.75)}]
   [:p {:max-width (ch 65)}]
   [:a {:text-decoration 'none}]
   [:label {:text-align 'right
            :padding "2px 0"
            :color 'darkslategray}]
   ["input[type=\"text\"]" {:border "1px solid  gainsboro"
                            :width (u/percent 100)}]
   [:ul {:list-style "inside circle"
         :padding-left 0}]
   [:.flavors {:display 'flex
               :margin-bottom (rem 1)}
    [:label {:color 'initial
             :font-variant 'normal
             :padding-right (rem 0.5)
             :padding-left (rem 0.25)}]]
   ["input[type=\"radio\"]" {:vertical-align :text-top}]
   [:.interactive {:background-color "#eee"
                   :padding (rem 1)
                   :box-sizing :border-box
                   :min-width :max-content}]
   [:.regal-form {:position 'sticky
                  :bottom 0
                  :height (u/percent 100)}
    [:textarea {:height (rem 9)
                :width (u/percent 100)
                :border 'none}]]
   [:.generator {:border "1px solid gainsboro"
                 :max-width (ch 65)}]
   [:li
    [:p {:padding-left (rem 1)
         :margin-top (rem 0.25)}]
    [:code {:white-space 'no-wrap}]]])


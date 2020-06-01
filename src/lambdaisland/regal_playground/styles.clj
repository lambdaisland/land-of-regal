(ns lambdaisland.regal-playground.styles
  (:require [garden.stylesheet :refer [at-font-face at-media]]
            [garden.units :as u :refer [rem ch]]))

(def jet-brains-mono-regular
  (at-font-face
   {:font-family "JetBrainsMono Regular"
    :src "url('./fonts/JetBrainsMono-Regular.ttf')"}))

(def enable-grid
  (at-media {:min-width (u/px 768)}
            [:html {:font-size (rem 1.125)}]
            [:article {:display 'grid
                       :margin-top (rem 10)
                       :border-top 'none}]
            [:code :textarea :input {:font-size (rem 1)}]
            [:label.regal-form {:bottom (rem 0.5)
                                :border-top-right-radius 0
                                :border-bottom-right-radius 0}]
            [:div.regal-form {:border-top-left-radius 0
                              :border-bottom-left-radius 0}]))

(def ^{:garden {:output-to "resources/public/styles.css"}}
  main
  [[:html {:font "1rem/1.6 \"ff-meta-serif-web-pro\", serif"
           :box-sizing 'border-box}]
   [:* {:box-sizing 'inherit}]
   jet-brains-mono-regular
   [:code :textarea :input {:font "600 .9rem/1.6 \"JetBrainsMono Regular\", monospace"
                            :white-space 'pre-wrap}]
   [:body {:margin 0}]
   [:main {:max-width 'max-content
           :margin "0 auto"}]
   [:article {:border-top "2px solid black"
              :grid-template-columns "auto 1fr"
              :grid-column-gap (rem 1)
              :margin (rem 2)}
    ["> :not(label)" {:grid-column-start 2}]]
   [:h1 :h2 :h3 :label {:font-variant :all-small-caps}]
   [:h1 {:font-size (rem 1.75)}]
   [:.copy-wrapper {:hyphens 'auto}]
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
   ["input[type=\"radio\"]" {:vertical-align :baseline}]
   ;; [:.interactive {:background-color "#eee"
   ;;                 :box-sizing :border-box
   ;;                 :min-width :max-content}]
   ;; [:.regal-form {:border ".5rem solid darkslategray"
   ;;                :position 'sticky
   ;;                :bottom 0
   ;;                :height (u/percent 100)}
   ;;  [:label {:padding "0 .5rem 0"}]]
   [:label.regal-form {:bottom (rem 9)
                       :font-weight 'bold}]
   [:.regal-form {:background-color 'darkslategray
                  :border ".5rem solid darkslategray"
                  :bottom (rem 0.5)
                  :color 'white
                  :height (rem 9)
                  :padding 0
                  :border-radius "2px"
                  :position 'sticky}
    [:textarea {:border 'none
                :height (u/percent 100)
                :margin 0
                :min-width 'max-content
                :width (u/percent 100)
                :padding 0}]]
   [:.generator {:border "1px solid gainsboro"
                 :max-width (ch 65)}]
   [:li
    [:p {:padding-left (rem 1)
         :margin-top (rem 0.25)}]
    [:code {:white-space 'no-wrap}]]
   [:button {:border "2px solid gainsboro"
             :padding ".2rem .5rem"}]
   enable-grid])


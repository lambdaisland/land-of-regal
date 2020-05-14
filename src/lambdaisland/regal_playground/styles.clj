(ns lambdaisland.regal-playground.styles)

(def grid-areas "\"flavor input result\"
                 \"regal  regal result\"
                 \"regal  regal plaintext\"
                 \"regex  regex plaintext\"")

(def ^{:garden {:output-to "resources/public/styles.css"}}
  main
  [[:body {:margin 0
           :height "100vh"}]
   [:#app {:height "100%"}]
   [:.layout {:display               :grid
              :grid-template-columns "1fr 2fr 3fr"
              :grid-template-rows    "1fr 50px 3fr 1fr"
              :grid-gap              "10px"
              :grid-template-areas   grid-areas
              :height "100%"}]
   [:.area {:padding "10px"}]
   [:.regal-form {:background-color :coral
                  :grid-area        "regal"}]
   [:.flavor {:grid-area "flavor"
              :background-color :teal}]
   [:.regex {:grid-area "regex"
             :background-color :lightyellow}]
   [:.input-string {:grid-area "input"
                    :background-color :lightgreen}]
   [:.result {:grid-area "result"
              :background-color :salmon}]
   [:.plaintext {:grid-area "plaintext"
                 :background-color :deepskyblue}]])

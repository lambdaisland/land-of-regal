(ns lambdaisland.regal-playground.macros
  (:require [clojure.java.io :as io]))

(defn inline-resource [s]
  (slurp (io/resource s)))

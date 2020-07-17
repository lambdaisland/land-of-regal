(ns lambdaisland.regal-playground.macros
  (:require [clojure.java.io :as io]))

(defmacro inline-resource [s]
  (slurp (io/resource s)))

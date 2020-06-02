(ns user
  (:require [shadow.cljs.devtools.api :as shadow]
            [shadow.cljs.devtools.server :as shadow-server]
            [garden-watcher.core :as gw]))

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn browse []
  ((jit clojure.java.browse/browse-url) "http://localhost:3875"))

(defn go []
  (shadow-server/start!)
  (shadow/watch :main)
  (gw/start-garden-watcher! '[lambdaisland.regal-playground.styles])
  (shadow/nrepl-select :main))

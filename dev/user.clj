(ns user
  (:require [shadow.cljs.devtools.api :as shadow]
            [shadow.cljs.devtools.server :as shadow-server]
            [garden-watcher.core :as gw]))

(defn start! []
  (shadow-server/start!)
  (shadow/watch :main)
  (gw/start-garden-watcher! '[lambdaisland.regal-playground.styles])
  (shadow/nrepl-select :main))

((nil . ((cider-preferred-build-tool . clojure-cli)
         (cider-custom-cljs-repl-init-form . "(do (require '[shadow.cljs.devtools.api :as shadow] '[shadow.cljs.devtools.server :as shadow-server]) (shadow-server/start!) (shadow/watch :main) (shadow/nrepl-select :main))")
         (cider-default-cljs-repl . custom))))

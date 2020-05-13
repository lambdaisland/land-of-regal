((nil . ((cider-clojure-cli-global-options . "-A:dev")
         (cider-preferred-build-tool . clojure-cli)
         (cider-custom-cljs-repl-init-form . "(user/start!)")
         (cider-default-cljs-repl . custom)
         (eval . (progn
                   (make-variable-buffer-local 'cider-jack-in-nrepl-middlewares)
                   (add-to-list 'cider-jack-in-nrepl-middlewares "shadow.cljs.devtools.server.nrepl/middleware"))))))

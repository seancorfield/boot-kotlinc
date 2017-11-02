(set-env! :resource-paths #{"src"})

(def version "0.1.1")

(task-options!
 pom {:project     'seancorfield/boot-kotlinc
      :version     version
      :description "Kotlin compiler task for Boot."
      :url         "https://github.com/seancorfield/boot-kotlinc"
      :scm         {:url "https://github.com/seancorfield/boot-kotlinc"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build []
  (comp (pom) (jar) (install)))

(deftask deploy
  []
  (comp (pom) (jar) (push)))

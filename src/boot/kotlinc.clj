(ns boot.kotlinc
  "Provide a Kotlin compiler task for Boot."
  {:boot/export-tasks true}
  (:require [boot.core :as core]))

(require '[clojure.string :as str])

;; This should really be part of the kotlinc task and the compiler invocation
;; should probably be inside a pod...
(core/merge-env!
 :dependencies
 '[[org.jetbrains.kotlin/kotlin-compiler "RELEASE" :scope "test"]
   [org.jetbrains.kotlin/kotlin-stdlib "RELEASE"]])

(core/deftask kotlinc
  "Compile Kotlin source files

  Adds compiled Kotlin files to the Boot filesystem so they can be part of
  an Clojure project -- either used in the REPL, or rolled up into an uber jar."
  [k source  PATH [str] "the Kotlin source path, defaults to src/kt"
   t test         bool  "Adds kotlin.test dependency and, if no -k --source, adds test/kt"
   v verbose      bool  "Be verbose!"]
  ;; In test mode, automatically depend upon kotlin.test:
  (when test
    (when verbose (println "Adding kotlin.test dependency..."))
    (let [before (core/get-env :fake-class-path)]
      (core/merge-env!
       :dependencies
       '[[org.jetbrains.kotlin/kotlin-test "RELEASE"]])
      ;; Fake class path is updated in a background thread so we need to
      ;; wait for it to sync up before we calculate what we pass to the
      ;; Kotlin compiler below!
      (Thread/sleep 10)
      (loop [after (core/get-env :fake-class-path)]
        (when (= before after)
          (println "Waiting for :fake-class-path to sync...")
          (Thread/sleep 10)
          (recur (core/get-env :fake-class-path))))))
  ;; The Kotlin compiler tries to read files on its classpath so we want to
  ;; restrict what it sees to just dependencies from Maven/Clojars etc.
  ;; In addition, we do not want it to see itself on the classpath!
  (let [cp (->> (str/split (core/get-env :fake-class-path) #":")
                (filter (partial re-find #"m2/repo"))
                (remove (partial re-find #"kotlin-compiler"))
                (str/join ":"))
        output (core/tmp-dir!)]
    (core/with-pre-wrap [fs]
      ;; Compile output to the temporary directory, suppress standard library
      ;; lookup (that's why it's on the classpath!), use the classpath we are
      ;; using (well, the Maven/Clojars portion of it).
      (let [options (into-array String (cond-> ["-d" (.getPath output)
                                                "-no-stdlib"
                                                "-cp" cp]
                                         verbose
                                         (conj "-verbose")
                                         true
                                         (into (or source ["src/kt"]))
                                         (and test (not source))
                                         (conj "test/kt")))]
        (when verbose
          (println "Invoking Kotlin compiler with" (str/join " " options)))
        (org.jetbrains.kotlin.cli.jvm.K2JVMCompiler/main options)
        (-> fs (core/add-resource output) core/commit!)))))

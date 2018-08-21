(defproject fnproject-clojure-test "0.1.0-SNAPSHOT"
  :description "Fn project example application in Clojure"
  :url "http://github.com/erdos/fnproject-clojure-test"
  :main ^:skip-aot fnproject-clojure-test.core
  :profiles {:uberjar {:aot :all
                       :uberjar-name "fnproject-app.jar"}}
  :dependencies [[org.clojure/clojure "1.8.0"]])

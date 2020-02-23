(defproject invoice-pdf-generator-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
  															[clj-pdf "2.4.0"]]
  :main ^:skip-aot invoice-pdf-generator-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

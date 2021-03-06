(defproject clj-chess-sumarry "0.1"
  :description "Chess summary generator."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [djy "0.1.4"]
                 [net.mikera/imagez "0.10.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [clj-chess "0.3.1"]]
  :main clj-chess-sumarry.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

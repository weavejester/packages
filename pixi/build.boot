(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.2"  :scope "test"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "4.1.1")
(def +version+ (str +lib-version+ "-0"))

(task-options!
 pom  {:project     'weavejester/pixi
       :version     +version+
       :description "2D webGL renderer with canvas fallback"
       :url         "http://www.pixijs.com"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"MIT" "http://opensource.org/licenses/MIT"}})

(deftask package []
  (comp
    (download :url (format "https://github.com/GoodBoyDigital/pixi.js/archive/v%s.zip" +lib-version+)
              :checksum "13f2debcf8c7bd20cb52d0ea23130b2d"
              :unzip true)
    (sift :move {#"^pixi\.js-([\d\.]*)/bin/pixi\.js$" "cljsjs/pixi/development/pixi.inc.js"
                 #"^pixi\.js-([\d\.]*)/bin/pixi\.min\.js$" "cljsjs/pixi/production/pixi.min.inc.js"})
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.pixi")
    (pom)
    (jar)))

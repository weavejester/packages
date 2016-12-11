(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.2"  :scope "test"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "4.2.3")
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
              :checksum "6ae987cbdd0d85d56d123b19f18f36c8"
              :unzip true)
    (sift :move {#"^pixi\.js-([\d\.]*)/dist/pixi\.js$" "cljsjs/pixi/development/pixi.inc.js"
                 #"^pixi\.js-([\d\.]*)/dist/pixi\.min\.js$" "cljsjs/pixi/production/pixi.min.inc.js"})
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.pixi")
    (pom)
    (jar)))

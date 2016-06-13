(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.1"  :scope "test"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
         '[boot.core :as boot]
         '[boot.tmpdir :as tmpd]
         '[clojure.java.io :as io]
         '[boot.util :refer [sh *sh-dir*]])

(def +lib-version+ "4.0.0")
(def +commit+ "5c778008b8f4941bf410a8ece1d426267ef59e0d")
(def +version+ (str +lib-version+ "-" (subs +commit+ 0 7) "-0"))
(def +lib-folder+ (str "pixi.js-" +commit+))

(task-options!
 pom  {:project     'weavejester/pixi
       :version     +version+
       :description "2D webGL renderer with canvas fallback"
       :url         "http://www.pixijs.com"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"MIT" "http://opensource.org/licenses/MIT"}})

(deftask download-zip []
  (download
   :url (format "https://github.com/GoodBoyDigital/pixi.js/archive/%s.zip" +commit+)
   :checksum "bf0e4d9aca81e9a36d4316c68dfa242d"
   :unzip true))

(deftask build []
  (let [tmp (boot/tmp-dir!)]
    (with-pre-wrap
      fileset
      (doseq [f (boot/input-files fileset)]
        (let [target (io/file tmp (tmpd/path f))]
          (io/make-parents target)
          (io/copy (tmpd/file f) target)))
      (binding [*sh-dir* (str (io/file tmp +lib-folder+))]
        ((sh "npm" "install"))
        ((sh "npm" "run" "build")))
      (-> fileset (boot/add-resource tmp) boot/commit!))))

(deftask package []
  (comp
   (download-zip)
   (build)
   (sift :move {#"^pixi\.js-.*/bin/pixi\.js$" "cljsjs/pixi/development/pixi.inc.js"
                #"^pixi\.js-.*/bin/pixi\.min\.js$" "cljsjs/pixi/production/pixi.min.inc.js"})
   (sift :include #{#"^cljsjs"})
   (deps-cljs :name "cljsjs.pixi")
   (pom)
   (jar)))

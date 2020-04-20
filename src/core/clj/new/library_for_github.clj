(ns clj.new.library-for-github
  (:require
   [clojure.string :as str]
   [clojure.java.io :as jio]
   [clojure.java.shell :as jsh]
   [clojure.tools.cli :as cli]
   [clj.new.templates :as t]
   [user.tools.deps.util.shell :refer [*sh-dir* dosh]]
   [library-for-github.github.alpha :as github]
   ))


;; * CLI


(def cli-options
  [["-d" "--target-dir TARGET_DIR" "Target directory"
    :id :dir
    :default-fn (fn [{:keys [github-repository]}] (str (doto (jio/file (System/getProperty "user.dir") (name (keyword github-repository))) (jio/make-parents))))]
   ["-r" "--github-repository REPO" "Github repository symbol"
    :validate-fn [string? (complement str/blank?) #(number? (str/index-of % "/"))]]
   ["-a" "--basic-auth BASIC_AUTH" "Basic authentication token"
    :validate [#(number? (str/index-of % ":")) "Basic-auth string must be a \"user:password\""]]
   [nil "--clojars-username USERNAME" "Clojars username"]
   [nil "--clojars-password PASSWORD" "Clojars password"]
   [nil "--slack-secure SLACK_SECURE" "Slack notifications token"]])


;; * fns


(defn strip-quotes
  [s]
  (nth (re-matches #"\s*\"([^\"]*)\"\s*" s) 1))


;; * sh


(defn sh-exit
  "Returns a map of jsh/sh return"
  [{:keys [exit out] :as sh-return}]
  (println out)
  (when-not (zero? exit)
    (throw (ex-info "Non-zero exit." sh-return)))
  sh-return)


;; ** ssh


(defn ssh-keygen
  [target key-title]
  (jio/make-parents target)
  (sh-exit (jsh/sh "ssh-keygen" "-t" "ecdsa" "-b" "521" "-N" "\"\"" "-C" key-title "-f" (str target) :in "y")))


;; ** travis





;; * Render


(def render (t/renderer "library-for-github"))


(defn project-data
  [name]
  (let [data (t/project-data name)]
    (if (str/index-of (:name data) ".")
      (let [main-ns (t/multi-segment (t/sanitize-ns (:name data)))]
        (assoc data
          :namespace main-ns
          :original-namespace (:namespace data)
          :nested-dirs (t/name-to-path main-ns)
          :original-nested-dirs (:nested-dirs data)))
      data)))


(defn configure-ci
  [{:keys
    [dir
     basic-auth
     github-repository
     clojars-username
     clojars-password
     slack-secure]
    :as options}]
  (let [[owner repo]  (str/split github-repository #"/" 2)
        ssh-key-title (str "DEPLOY KEY: https://travis-ci.com/" owner "/" repo)]
    ;; validate parsed-opts
    (jio/make-parents (jio/file dir ".ci"))
    (ssh-keygen (jio/file dir ".ci/deploy-key") ssh-key-title)
    (github/create-repo
      {:basic-auth  basic-auth
       :form-params {"name" repo}})
    (github/post-deploy-key
      {:basic-auth   basic-auth
       :github/owner owner
       :github/repo  repo
       :form-params  {"title"     ssh-key-title
                      "key"       (slurp (jio/file dir ".ci/deploy-key.pub"))
                      "read_only" false}})
    (binding [*sh-dir* dir]
      (when (string? clojars-username)
        (dosh "travis" "env" "--pro" "-r" github-repository "set" "CLOJARS_USERNAME" clojars-username))
      (when (string? clojars-password)
        (dosh "travis" "env" "--pro" "-r" github-repository "set" "CLOJARS_PASSWORD" clojars-password))
      (let [[_ encrypted-id] (re-find #"\$encrypted_([^\s]+?)_key" (:out (sh-exit (jsh/sh "travis" "encrypt-file" "--pro" "-r" github-repository (str (jio/file dir ".ci/deploy-key")) (str (jio/file dir ".ci/deploy-key.enc"))))))
            slack-secure     (when (string? slack-secure)
                               (strip-quotes (:out (sh-exit (jsh/sh "travis" "encrypt" "--pro" "-r" github-repository slack-secure)))))]
        {:encrypted-id encrypted-id
         :slack-secure slack-secure}))))


(defn library-for-github
  "Create a clojure library template for github"
  [name & args]
  (let [data (project-data name)]
    (t/->files
      data
      ;;
      [".ci/settings.xml" (render ".ci/settings.xml" data)]
      [".gitignore" (render ".gitignore" data)]
      [".dir-locals.el" (render ".dir-locals.el" data)]
      ;;
      ["deps.edn" (render "deps.edn" data)]
      "src/core"
      "src/test"
      ["src/test/user.clj" (render "user.clj" data)])
    (let [{:keys [options] :as parsed} (cli/parse-opts args cli-options)
          data'                        (merge data (configure-ci options))]
      (println "CI data:" (pr-str data'))
      (t/->files
        data'
        [".travis.yml" (render ".travis.yml" data')]))))

(ns clj.new.library-for-github-test
  (:require
   [clojure.test :as test :refer [deftest is are testing]]
   [clj-new.helpers]
   [clj.new.templates :as t]
   [clj.new.library-for-github :refer :all]
   ))


(def data-1 (project-data "group-id/abc.xyz"))


(deftest main
  (is (= (:namespace data-1) "abc.xyz"))
  (is (= (:nested-dirs data-1) "abc/xyz"))
  (is (= (:original-namespace data-1) "group-id.abc.xyz"))
  (is (= (:original-nested-dirs data-1) "group_id/abc/xyz"))
  )


(comment
  (render ".travis.yml" {})


  (binding [t/*environment* {:clojars-username "ajchemist" :notifications-slack "secure"}]
    (t/project-data "owner/repo.1"))


  (library-for-github
    "owner/repo.1"
    {:clojars-username "ajchemist" :notifications-slack "secure"})


  (ssh-keygen (jio/file (System/getProperty "java.io.tmpdir") ".ci/deploy-key") "DEPLOY KEY: ")


  (sh-exit
    (jsh/sh "travis" "encrypt" "--pro" "-r" "ajchemist/template" "asdf:asdf"))


  (with-out-str (dosh "travis" "encrypt" "--pro" "-r" "ajchemist/template" "asdf:asdf"))


  (helpers/create
    {:name     "ajchemist/library-sample-1"
     :template "library-for-github"
     :args     ["-?" "-f" "-o" "/tmp/library-sample-1"]})


  (clj.new.library-for-github/library-for-github
    "ajchemist/library-sample-1"
    ["-?" "-f"])
  )

(ns library-for-github.github.alpha
  (:require
   [clj-http.client :as http]
   ))


(defn wrap-meta-response
  [handler]
  (letfn [(f [{:keys [body] :as response}]
            (if (instance? clojure.lang.IMeta body)
              (with-meta body (dissoc response :body))
              response))]
    (fn
      ([request]
       (f (handler request)))
      ([request respond raise]
       (handler request #(respond (f %)) raise)))))


(def ^{:arglists '([request] [request respond raise])}
  request
  (-> http/request
    (wrap-meta-response)))


(defn client
  [{:keys [url] :as req}]
  (try
    (request req)
    (catch Exception e
      (cond
        ;; (re-find #"Bad Request: message is not modified" (:body (ex-data e)))
        ;; (println "Github request failed:" url (pr-str req))

        :else
        (do
          (println "Github request failed:" url (pr-str req))
          (throw e))))))


(defn list-user-repos
  [{:keys [:github/username] :as req}]
  (client
    (assoc req
      :url (str "https://api.github.com/users/" username "/repos")
      :method :get
      :as :json-strict-string-keys
      )))


(defn create-repo
  [req]
  {:pre [(string? (get-in req [:form-params "name"]))]}
  (println "Creating repository:" (get-in req [:form-params "name"]))
  (client
    (assoc req
      :url          "https://api.github.com/user/repos"
      :method       :post
      :content-type :json
      :as           :json-string-keys)))


(defn delete-repo
  [{:keys [:github/owner :github/repo] :as req}]
  (client
    (assoc req
      :url          (str "https://api.github.com/repos/" owner "/" repo)
      :method       :delete
      :content-type :json
      :as           :json-string-keys)))


(defn list-deploy-keys
  [{:keys [:github/owner :github/repo] :as req}]
  (client
    (assoc req
      :url    (str "https://api.github.com/repos/" owner "/" repo "/keys")
      :method :get
      :as     :json-string-keys)))


(defn post-deploy-key
  [{:keys [:github/owner :github/repo] :as req}]
  (println "Deploying key:" (str owner "/" repo))
  (client
    (assoc req
      :url (str "https://api.github.com/repos/" owner "/" repo "/keys")
      :method :post
      :content-type :json
      :as :json-string-keys)))


(comment
  (client
    {:url    "https://api.github.com/zen"
     :method :get})


  (list-user-repos
    {:github/username "aJchemist"})

  (list-deploy-keys
    {:basic-auth   basic-auth
     :github/owner "ajchemist"
     :github/repo  "user.jsoup"})
  )

# Usage


``` shell
clj -Sdeps '{:deps {seancorfield/clj-new {:mvn/version "1.0.199"}}}' -m clj-new.create \
    https://github.com/ajchemist/library-for-github@db62f72045344a77c0ba27ecc6bfab71b7fc1cda \
    $project_name \
    -? \
    -f \
    -- \
    -r $owner/$repo \
    -a $basic_auth \
    --clojars-username $username \
    --clojars-password $password \
    --slack-secure $token
```

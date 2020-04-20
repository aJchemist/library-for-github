# Usage


``` shell
clj -Sdeps '{:deps {seancorfield/clj-new {:mvn/version "1.0.199"}}}' -m clj-new.create \
    https://github.com/ajchemist/library-for-github@864474f1b6c4469131835fa6c12bc7ffc03f39dd \
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

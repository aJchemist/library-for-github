# Usage


``` shell
clj -Sdeps '{:deps {seancorfield/clj-new {:mvn/version "1.0.199"}}}' -m clj-new.create \
    https://github.com/ajchemist/library-for-github@a209a182a92545f084507b5b82489ee826f5c746 \
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

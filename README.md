# Usage


``` shell
clj -Sdeps '{:deps {seancorfield/clj-new {:mvn/version "1.0.199"}}}' -m clj-new.create \
    https://github.com/ajchemist/library-for-github@df274fd5e07b52b3961cf671f28e6631ac3ee489 \
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

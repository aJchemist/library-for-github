# Usage


``` shell
clj -Sdeps '{:deps {seancorfield/clj-new {:mvn/version "1.0.199"}}}' -m clj-new.create \
    https://github.com/ajchemist/library-for-github@05e21d46924f10ab204b53743d61cac2dfc82e3d \
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

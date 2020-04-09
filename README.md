## Prerequisites

- Java 8 or 11 (JDK)
- [clj (Mac/Linux)](https://clojure.org/guides/deps_and_cli) or [deps (Windows)](https://github.com/borkdude/deps.clj)

## Run It
```shell script
$ deps -A:dev
```

## Test It
```shell script
$ deps -A:tst
```
See the results at /figwheel-extra-main/auto-testing

## Build For Production
```shell script
$ rm -rf target/public
$ deps -A:pro
```
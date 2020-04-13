## About

This is the attempt to build a new UI for [navalgrid.com](http://www.navalgrid.com/) in ClojureScript to provide a fully interactive map.

## Prerequisites

- Java 8 or 11 (JDK)
- [clj (Mac/Linux)](https://clojure.org/guides/deps_and_cli) or [deps (Windows)](https://github.com/borkdude/deps.clj)

## Run It
```shell script
$ deps -A:dev
```
See test results at `/figwheel-extra-main/auto-testing`

## Build For Production
```shell script
$ rm -rf target/public
$ deps -A:pro
```

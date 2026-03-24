# cljs-navalgrid

You need Java and node.js. 

After pulling, run:
```bash
npm install
```

## Generate a POM
```bash
shadow-cljs pom
```
Using the POM, the project can be fully recognized by IDEA


## Development
```bash
npx shadow-cljs watch frontend browser-test
```
This will watch both, `:frontend` and `:test` build-ids in order to reload the browser and run tests every time a file is changed.

## REPL
The watch process will have compiled the given code to `public/js`. The generated .js must be loaded in a browser in order for the REPL to be available.

`Run Configurations` → `+` → `Clojure REPL` → `Remote`
- nREPL
- Use port from file with localhost
  - Use standard port file

Switch to CLJS by calling
```clj
(shadow/repl :frontend)
```
To drop back to CLJ call
```clj
:cljs/quit
```

### Terminal
```bash
npx shadow-cljs node-repl
```

### Browser
```bash
npx shadow-cljs browser-repl
```

## Testing

The watch in [Development](#development) will already run tests. Alternatively you can do any of the following.

### Via REPL
You can load a test-file in the REPL and call `(run-tests)`.

### Via Command Line (node.js)
Compile and run all tests (any files with namespace ending in `-test`):
```bash
npx shadow-cljs compile test
node out/node-tests.js
```
- When there are failures, the output will show which assertion failed and why
- If all tests pass, the exit-code is zero

## Release

```bash
shadow-cljs release app
```
# cljs-navalgrid

## Simple REPL

### Terminal
```bash
npx shadow-cljs node-repl
```

### Browser
```bash
npx shadow-cljs browser-repl
```

## Integrade with IntelliJ IDEA and Cursive

### Generate a POM
```bash
shadow-cljs pom
```

### Integrated REPL
```bash
npx shadow-cljs server
```
Run Configurations → `+` → Clojure REPL → Remote
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

## Testing

### REPL
You can load a test-file in the REPL and call `(run-tests)`.

### Command Line (node.js)
Compile and run all tests (any files with namespace ending in `-test`):
```clj
npx shadow-cljs compile test
node out/node-tests.js
```
- When there are failures, the output will show which assertion failed and why
- If all tests pass, the exit-code is zero

## Development
```bash
npx shadow-cljs watch frontend test --config-merge '{:autorun true}'
```
This will watch both, `:frontend` and `:test` build-ids in order to reload the browser and run tests every time a file is changed.

Or, if you'd rather run the tests in a browser:
```bash
npx shadow-cljs watch frontend browser-test
```

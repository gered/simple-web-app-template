# Simple Clojure/ClojureScript Web App Leiningen Template

A Leiningen template for creating new Clojure/ClojureScript web app projects using a simplified / stripped-down base.

Too often I find myself wanting to start a new web application project with just a really simple, light-weight base. In
these cases, I almost always don't want a bunch of extra junk cluttering up the project tree. A lot of the time this is
just to help me write a very quick/simple test application for something I'm working on (e.g. a little web app to serve
as a simple test or demo project for a library I'm working on). 

A lot of the other Leiningen templates out there for creating web applications include a lot of this type of extra junk
out of the box, so I created this template as an alternative. This template primarily exists for my own use, so some
stuff is more oriented towards my own particular preferences regarding the set-up and organization of a Clojure project.

Includes out-of-the-box support for:

* Ring/Compojure
* http-kit
* Reagent
* Figwheel

## Usage

```
$ lein new simple-web-app [your-project-name-here]
```

Then you can spin up figwheel and run the server-side component by running the following each in their own separate CLI
instances:

```
$ lein figwheel
$ lein run
```

Out of the box the web app will be running at http://localhost:8080/

A ClojureScript REPL is also configured by Figwheel on port 7000. Run `(cljs-repl)` once connected.

An uber JAR for deployment can be created with:

```
$ lein uberjar
```

## License

Copyright © 2021 Gered King

Distributed under the the MIT License. See LICENSE for more details.

# Leiningen Template: Simple Clojure / ClojureScript Web App

A Leiningen template intended for creating new Clojure / ClojureScript web app projects utilizing [reitit](https://github.com/metosin/reitit)
and [Figwheel Main](https://figwheel.org/). 

This template primarily exists for my own personal use, so some stuff is definitely more oriented towards my own
particular preferences regarding setup and organization of a Clojure project.

## Usage

```text
$ lein new net.gered/simple-web-app [your-project-name-here]
```

The resulting project starts up via a `main` function and during startup expects to be able to read an EDN
configuration file located in the current working directory called `config.edn`.

### Server-Side

The project's server-side component can be run simply by:

```text
$ lein run
```

A nREPL server will be started which can be connected to on port 7000 (configured via the aforementioned `config.edn`).

Once running, the web app's root URL should be browsable at http://localhost:8080/ (again, the port can be configured
via the aforementioned `config.edn`).

### Client-Side

During development, the client-side ClojureScript can be compiled via Figwheel through the `fig:build` alias configured
in `project.clj`:

```text
$ lein fig:build
```

This will compile the ClojureScript and start up Figwheel to automatically watch for and recompile any ClojureScript
code changes. Once running, if you navigate your browser to your running web app, Figwheel should connect and your
browser will also now automatically be pushed any ClojureScript code changes live.

Figwheel Main unfortunately has somewhat lacking support for setting up a ClojureScript nREPL, especially so when your
project is Leiningen based and you use an IDE such as Cursive. This is in stark contrast to the old lein-figwheel which
had very good support for this. To work around this limitation in Figwheel Main, your new projects created with this
template will has an alias `fig:nrepl` configured in `project.clj` which will set up an nREPL through Leiningen that is
specifically for Figwheel usage.

Simply run:

```text
$ lein fig:nrepl
```

Once started up, connect to the nREPL and manually run `(start)`. This will start up the Figwheel build (in exactly the 
same manner as the `fig:build` alias does), but also give you a ClojureScript REPL in your connect nREPL session.

Note that a [lein-cljsbuild](https://github.com/emezeske/lein-cljsbuild) configuration is included _purely_ because
it has superior integration with Leiningen for automatically performing release ClojureScript compilation when 
generating uber JARs. This lein-cljsbuild configuration will be automatically utilized at such time, no special action 
is needed, so it can be entirely ignored during development!

## Other Templates!

I have a few other "simple" Leiningen project templates that are along the same vein as this one:

* [Simple Clojure App](https://github.com/gered/simple-app-template) - Very simple starter base for non-web projects.
* [Simple Clojure Web Service](https://github.com/gered/simple-web-service-template) - For web services to be consumed by other apps.
* [Simple Clojure Web Site](https://github.com/gered/simple-web-site-template) - For web sites using only server-side rendered HTML (**no** ClojureScript!).

## License

Copyright © 2022 Gered King

Distributed under the the MIT License. See LICENSE for more details.
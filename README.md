# clj-flickr-memories

This is a port of https://github.com/emanchado/Flickr-Memories,
written mainly to learn a bit more Clojure, and to experiment a bit
more with RoboHydra 0.4 in a real setting.


## Installation

First, make sure you have JDK and Leiningen installed. Then, clone
this repo.

Once you have the code, you'll have to copy `config.edn-sample` to
`config.edn` and customise it.


## Usage

You can call clj-flickr-memories from inside the repo directory like:

    lein run <Flickr-URL-name>
    lein run <Flickr-URL-name> <reference-date>
    lein run <Flickr-URL-name> <reference-date> <email-address>

Where the Flickr URL name is the bit after `http://flickr.com/photos/`
in your photo stream URL, the reference date is an optional date in
the format YYYY-MM-DD, and the e-mail address is the address to send
the list of photos to. If the latter is not given, the HTML will be
printed on the standard output.

clj-flickr-memories will then start going back in time, one year on
every step, starting from the reference date. In each step in will
calculate the week (starting on Monday!) containing the pivot date
(ie. reference date one year ago; reference date two years ago, etc.)
and checking if there are any images on Flickr that where taken that
week. If it finds anything, it will output a web page with the
results. If not, it will go one more year back in time, until it finds
images or until it gives up after 5 years.


## Testing

You can run the unit tests with lein, by typing:

    lein test

There's also a [RoboHydra](http://robohydra.org)-based fake Flickr API
server in the `robohydra/` folder. To use it, you'll need to:

1. Make a DNS alias (eg. in `/etc/hosts` if you use Linux)
`farm1.localhost` to point to your own machine.
2. Start the fake Flickr API server with the command
`./node_modules/.bin/robohydra robohydra/flickr-api.conf`.
3. Run `clj-flickr-memories` with the `-s` and `-u` flags like so:
`lein run -u http://localhost:3000/services/rest -s
localhost:3000/static <flickr-user>`

By default the server will return zero results when searching for
photos, but you can check
http://localhost:3000/robohydra-admin/scenarios and activate whatever
scenarios are available to see how clj-flickr-memories reacts to
different server responses.

The different scenarios are in
`robohydra/plugins/flickr-api/index.js`, and the static files are in
`robohydra/static`.


## License

Copyright © 2013 Esteban Manchado Velázquez

Distributed under the Eclipse Public License, the same as Clojure.

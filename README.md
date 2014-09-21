# clj-photo-memories

This is a port of https://github.com/emanchado/Flickr-Memories,
written mainly to learn a bit more Clojure, and to experiment a bit
more with RoboHydra 0.4 in a real setting. It was later extended to
support other services (at the moment, Trovebox).


## Installation

First, make sure you have JDK and Leiningen installed. Then, clone
this repo.

Once you have the code, you'll have to copy `config.edn-sample` to
`config.edn` and customise it.


## Usage

You can call clj-photo-memories from inside the repo directory like:

    lein run <username>
    lein run <username> <reference-date>
    lein run <username> <reference-date> <email-address>

The username is, in the case of Flickr, the URL bit after
`http://flickr.com/photos/` in your photo stream URL. In the case of
Trovebox it would be the e-mail address used to login, although it's
not in use right now.

The reference date is an optional date in the format YYYY-MM-DD
(defaults to the current date), and the e-mail address is the address
to send the list of photos to. If the latter is not given, the HTML
will be printed on the standard output.

clj-photo-memories will then jump back in time five years from the
reference date and start approaching the current date one year at a
time. For every step it will calculate the week (starting on Monday!)
containing the pivot date (ie. reference date five years ago;
reference date four years ago, etc.) and checking if there are any
images taken that week. If it finds anything, it will output a web
page with the results. If not, it will jump forward one more year
until it finds images or until it reaches the current date.


## Testing

You can run the unit tests with lein, by typing:

    lein test

There are also [RoboHydra](http://robohydra.org)-based fake Flickr and
Trovebox API servers in the `robohydra/` folder. To use them, you'll
need to make a DNS alias (eg. in `/etc/hosts` if you use Linux)
`farm1.localhost` to point to your own machine. Then you can run the
tests with `./robohydra/run-tests.sh`.

If you want to run the tests by hand, you'll have to start RoboHydra
with the command `./node_modules/.bin/robohydra
robohydra/mock-apis.conf`, choose a scenario (see
http://localhost:3000/robohydra-admin/scenarios for the list) and then
run `clj-photo-memories` with the `-t`, `-s` and `-u` flags like so:
`lein run -t flickr -u http://localhost:3000/services/rest -s
localhost:3000/static <username>`.

The scenarios are implemented in
`robohydra/plugins/flickr-api/index.js` and
`robohydra/plugins/trovebox-api/index.js`, and the static files are in
`robohydra/static`.


## License

Copyright © 2013-2014 Esteban Manchado Velázquez

Distributed under the Eclipse Public License, the same as Clojure.

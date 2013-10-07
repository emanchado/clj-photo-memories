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


## License

Copyright © 2013 Esteban Manchado Velázquez

Distributed under the Eclipse Public License, the same as Clojure.

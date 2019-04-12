# clojure-toggl-exporter

A small tool which creates a CSV out of time entries in toggl.
It is built in Clojure, which means that it can be made into a jar and
can run on any machine that has java installed.

## Usage

In the `config.yml` you need to enter your toggl token, the date
range, project id, filename and directory first.

To build the jar file out of this project you need to install [Leiningen](https://leiningen.org/)
Afterwards run this command in the root of the repository.

    lein uberjar

Lastly you can use the program like this.

    $ java -jar clojure-toggl-exporter-0.1.0-standalone.jar [args]

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

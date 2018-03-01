This is a hobby project to run my own http server for displaying content,
handling custom requests, and more!

This server runs on Google App Engine here: https://ziproject-gcloud.appspot.com

Its home page takes you to https://ziproject-gcloud.appspot.com/#/home,
which displays some fun html gadgets.

Custom requests handler:
/demo -> nothing really.
/datastore -> displays the information of files stored in GCS.

To run local dev server:
    appengine:start
To stop local dev server:
    appengine:stop
Note that this intellij appengine plugin is somewhat fragile. Calling
appengine:start on an already started server essentially breaks it
completely and makes it unresponsive to appengine:stop, so you might
as well just use a new port number for dev server, and the old port
will be reclaimed when you restart the computer I guess.

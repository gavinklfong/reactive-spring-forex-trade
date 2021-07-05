#!/bin/sh

json-server --watch ./mock-data.json &
java -jar ./app.jar
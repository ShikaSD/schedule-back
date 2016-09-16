#!/bin/bash

password=$(cat /opt/play/cert/password)

/opt/play/current/bin/root -Dconfig.file=/opt/play/current/conf/prod.conf -Dhttps.port=443 -Dhttp.port=disabled -Dplay.server.https.keyStore.path=/opt/play/cert/keystore.jks -Dplay.server.https.keyStore.password=${password}
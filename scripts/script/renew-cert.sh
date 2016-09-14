#!/bin/bash

cd /opt/play/cert/

sudo letsencrypt renew --agree-tos

sudo rm -rf keystore.jks
sudo openssl pkcs12 -export -out keystore.pkcs12 -in /etc/letsencrypt/live/schedule.northeurope.cloudapp.azure.com/fullchain.pem -inkey /etc/letsencrypt/live/schedule.northeurope.cloudapp.azure.com/privkey.pem -password pass:letsencrypt
sudo keytool -importkeystore -srckeystore keystore.pkcs12 -srcstoretype PKCS12 -destkeystore keystore.jks -srcstorepass letsencrypt -deststorepass letsencrypt
sudo rm -rf keystore.pkcs12

sudo /opt/play/script/stop.sh
sudo /opt/play/script/start.sh
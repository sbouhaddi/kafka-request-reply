#!/bin/bash

# Générer la clé privée du client
openssl genrsa -out kafka.client.key 2048

# Générer une demande de signature de certificat (CSR) pour le client
openssl req -new -key kafka.client.key -out kafka.client.csr -subj "/CN=client/OU=kafka/O=example/L=Paris/C=FR"

# Signer le certificat client avec notre CA
openssl x509 -req -CA ca.crt -CAkey ca.key -in kafka.client.csr -out kafka.client.crt -days 365 -CAcreateserial

# Créer un keystore client au format PKCS12
openssl pkcs12 -export -in kafka.client.crt -inkey kafka.client.key -out kafka.client.keystore.p12 -name client -CAfile ca.crt -caname root -password pass:keystorepass

# Créer un truststore client
keytool -import -file ca.crt -alias ca -keystore kafka.client.truststore.jks -storepass truststorepass -noprompt

#!/bin/bash

# Générer une clé privée pour le CA
openssl genrsa -out ca.key 2048

# Générer un certificat auto-signé pour le CA
openssl req -new -x509 -key ca.key -out ca.crt -days 365 -subj "/CN=localhost"

# Générer une clé privée pour le broker Kafka
openssl genrsa -out kafka.key 2048

# Générer une demande de signature de certificat (CSR) pour le broker Kafka
openssl req -new -key kafka.key -out kafka.csr -subj "/CN=localhost"

# Signer le CSR avec le CA
openssl x509 -req -in kafka.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out kafka.crt -days 365

# Créer le keystore PKCS12 pour Kafka
openssl pkcs12 -export -in kafka.crt -inkey kafka.key -name localhost -out kafka.server.keystore.p12 -passout pass:password123

# Créer le truststore pour Kafka
keytool -import -file ca.crt -alias CARoot -keystore kafka.server.truststore.jks -storepass password123 -noprompt

# Créer les fichiers de credentials
echo "password123" > keystore_creds
echo "password123" > key_creds
echo "password123" > truststore_creds

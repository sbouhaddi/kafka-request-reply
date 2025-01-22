# Kafka SSL Configuration

[![Build and Test](https://github.com/sbouhaddi/kafka-request-reply/actions/workflows/gradle.yml/badge.svg)](https://github.com/sbouhaddi/kafka-request-reply/actions/workflows/gradle.yml)

Ce projet contient une configuration Docker Compose pour déployer Kafka avec SSL.

## Prérequis

- Docker
- Docker Compose
- OpenSSL
- Java keytool (inclus dans le JDK)

## Configuration

1. Générer les certificats SSL :
```bash
cd ssl
chmod +x generate-certs.sh
./generate-certs.sh
```

2. Démarrer les conteneurs :
```bash
docker-compose up -d
```

Les services suivants seront disponibles :
- Zookeeper : port 2181
- Kafka : port 9093 (SSL)

## Configuration SSL

Les certificats et keystores sont générés dans le dossier `ssl/`. Les mots de passe par défaut sont "password123".

- `kafka.server.keystore.jks` : Keystore du broker Kafka
- `kafka.server.truststore.jks` : Truststore du broker Kafka
- Les fichiers de credentials sont automatiquement générés

## Test de la connexion

Pour tester la connexion SSL, vous devrez configurer vos clients Kafka avec les certificats appropriés.

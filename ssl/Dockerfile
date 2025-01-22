FROM openjdk:11-jdk

RUN apt-get update && apt-get install -y openssl

WORKDIR /certs

COPY generate-certs.sh /certs/
RUN chmod +x /certs/generate-certs.sh

# Créer un utilisateur non-root
RUN useradd -m -u 1000 certuser
RUN chown -R certuser:certuser /certs

USER certuser

CMD ["./generate-certs.sh"]

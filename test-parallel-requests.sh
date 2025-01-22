#!/bin/bash

# Configuration
TOTAL_REQUESTS=1000
MAX_PARALLEL=500
ENDPOINT="http://localhost:8080/api/request-response"
START_TIME=$(date +%s)
echo "Start time: $(date)"

# Fichiers de résultats permanents
RESULTS_DIR="./test-results"
mkdir -p $RESULTS_DIR
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
LOG_FILE="$RESULTS_DIR/results_${TIMESTAMP}.log"

# Fonction pour extraire le payload de la réponse JSON
extract_payload() {
    local json=$1
    echo "$json" | grep -o '"payload":"[^"]*"' | sed 's/"payload":"\(.*\)"/\1/'
}

# Fonction pour envoyer une requête et vérifier la réponse
send_request() {
    local id=$1
    local message="Test message $id"
    local expected_response="Processed: $message"
    local start_time=$(date +%s)
    
    # Envoyer la requête et capturer la réponse
    response=$(curl -s -X POST -H "Content-Type: text/plain" -d "$message" $ENDPOINT)
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    
    if [ $? -eq 0 ]; then
        # Extraire le payload de la réponse JSON
        received_payload=$(extract_payload "$response")
        
        if [ "$received_payload" = "$expected_response" ]; then
            echo "[SUCCESS] Request $id - Duration: ${duration}s - Response: $response" >> "$LOG_FILE"
        else
            echo "[ERROR] Request $id - Duration: ${duration}s - Mismatch. Expected: $expected_response, Got: $received_payload" >> "$LOG_FILE"
        fi
    else
        echo "[ERROR] Request $id - Duration: ${duration}s - Curl error" >> "$LOG_FILE"
    fi
    
    # Afficher la progression
    echo "Completed request $id / $TOTAL_REQUESTS"
}

echo "Starting parallel requests test..."
echo "Results will be saved in: $LOG_FILE"
echo "Test started at: $(date)" > "$LOG_FILE"
echo "Total requests to send: $TOTAL_REQUESTS" >> "$LOG_FILE"
echo "Maximum parallel requests: $MAX_PARALLEL" >> "$LOG_FILE"
echo "----------------------------------------" >> "$LOG_FILE"

# Envoyer les requêtes en parallèle
for i in $(seq 1 $TOTAL_REQUESTS); do
    # Attendre si nous avons atteint le nombre maximum de processus en parallèle
    while [ $(jobs -r | wc -l) -ge $MAX_PARALLEL ]; do
        sleep 0.1
    done
    
    # Lancer la requête en arrière-plan
    send_request $i &
    
    # Afficher un message tous les 10 requêtes
    if [ $((i % 10)) -eq 0 ]; then
        echo "Started $i / $TOTAL_REQUESTS requests..."
    fi
done

echo "Waiting for all requests to complete..."

# Attendre que toutes les requêtes soient terminées
wait

# Calculer les résultats
END_TIME=$(date +%s)
echo "End time: $(date)"
DURATION=$((END_TIME - START_TIME))

# Calculer les minutes et secondes
MINUTES=$((DURATION / 60))
SECONDS=$((DURATION % 60))

# Compter les succès et les erreurs
SUCCESS_COUNT=$(grep -c "\[SUCCESS\]" "$LOG_FILE")
ERROR_COUNT=$(grep -c "\[ERROR\]" "$LOG_FILE")
REQUESTS_PER_SECOND=$((TOTAL_REQUESTS / (DURATION == 0 ? 1 : DURATION)))

# Ajouter le résumé au fichier de log et l'afficher
{
    echo -e "\n----------------------------------------"
    echo "Test Summary"
    echo "----------------------------------------"
    echo "Test completed at: $(date)"
    echo "Total duration: $MINUTES minutes and $SECONDS seconds"
    echo "Total requests: $TOTAL_REQUESTS"
    echo "Successful requests: $SUCCESS_COUNT"
    echo "Failed requests: $ERROR_COUNT"
    echo "Requests per second: $REQUESTS_PER_SECOND"
    echo "----------------------------------------"
} | tee -a "$LOG_FILE"

# Afficher les dernières erreurs s'il y en a
if [ $ERROR_COUNT -gt 0 ]; then
    echo -e "\nLast 5 errors:"
    grep "\[ERROR\]" "$LOG_FILE" | tail -n 5
fi

echo -e "\nComplete test results have been saved to: $LOG_FILE"

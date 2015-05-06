#!/bin/bash
SLEEP_TIME=60

echo "Running $1#$2..."
mvn -Dtest="$1#$2" test 1> $2.txt	
cat $2.txt | grep -v INFO > /tmp/fjavaout.txt
mv /tmp/fjavaout.txt $2.txt
echo 'Pause between tests...'
sleep $SLEEP_TIME

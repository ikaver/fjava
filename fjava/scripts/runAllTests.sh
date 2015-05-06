#!/bin/bash
SLEEP_TIME=60

sh scripts/runSingleTest.sh $1 "testFJava"
sh scripts/runSingleTest.sh $1 "testJavaForkJoin"
sh scripts/runSingleTest.sh $1 "testSequential"
rm -r results || true
mkdir results || true
if [ "$COLLECT_STATS" = "true" ]
then
    mv testFJava.txt results/wstats.txt
else
    mv testFJava.txt results/nostats.txt
fi
mv testJavaForkJoin.txt results/testJavaForkJoin.txt
mv testSequential.txt results/testSequential.txt
sh scripts/collectResults.sh $1 results

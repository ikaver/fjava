#!/bin/bash

SLEEP_TIME=15

BASE_DIR=$2

if [ "$COLLECT_STATS" = "true" ] 
then
	cat $BASE_DIR/wstats.txt $BASE_DIR/testJavaForkJoin.txt $BASE_DIR/testSequential.txt \
        | grep StatsTracker | cut -d " " -f 2,3,4 \
        | python  graphs/statsgraph.py $BASE_DIR .\* > $1.txt
else
	cat $BASE_DIR/nostats.txt $BASE_DIR/testJavaForkJoin.txt $BASE_DIR/testSequential.txt \
        | grep $1. -A 1 | awk 'NR % 3 != 0' |  python graphs/timegraph.py $BASE_DIR
fi
echo 'Sleeping for computer to chill...'
sleep $SLEEP_TIME
echo "Done with $1"

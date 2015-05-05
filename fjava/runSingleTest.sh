
SLEEP_TIME=60

echo 'Running FJava...'
mvn -Dtest="$1#testFJava" test 1> out.txt 2> log.txt	
echo 'Pause between tests...'
sleep $SLEEP_TIME
echo 'Running Java Fork Join...'
mvn -Dtest="$1#testJavaForkJoin" test >> out.txt		
echo 'Pause between tests...'
sleep $SLEEP_TIME
echo 'Running sequential...'
mvn -Dtest="$1#testSequential" test >> out.txt	
echo 'Processing output...'
cat out.txt | grep -v INFO > /tmp/out.txt
mv /tmp/out.txt out.txt
echo 'Creating graphs...'
if [ "$COLLECT_STATS" = "true" ] 
then
	cat log.txt | grep StatsTracker | cut -d " " -f 2,3,4 | python  graphs/statsgraph.py $1 .\* > $1.txt
else
	cat out.txt | grep $1. -A 1 | awk 'NR % 3 != 0' |  python graphs/timegraph.py $1
fi
echo 'Sleeping for computer to chill...'
sleep $SLEEP_TIME
echo "Done with $1"

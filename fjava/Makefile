all: build

build: clean
	mvn package -DskipTests

test: clean
	mvn package

testExpensiveMap: clean
	mvn -Dtest=testExpensiveMap test

testFib: clean
	echo 'Running FJava...'
	mvn -Dtest='TestFibonacci#testFJava' test 1> out.txt 2> log.txt	
	echo 'Pause between tests...'
	sleep 60
	echo 'Running Java Fork Join...'
	mvn -Dtest='TestFibonacci#testJavaForkJoin' test >> out.txt		
	echo 'Pause between tests...'
	sleep 60
	echo 'Running sequential...'
	mvn -Dtest='TestFibonacci#testSequential' test >> out.txt	
	echo 'Processing output...'
	cat out.txt | grep -v INFO > /tmp/out.txt
	mv /tmp/out.txt out.txt
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d " " -f 2,3,4 | python  graphs/statsgraph.py testFib .\* > testFib.txt
endif
	cat out.txt | grep TestFibonacci. -A 1 | awk 'NR % 3 != 0' |  python graphs/timegraph.py testFib
	echo 'Sleeping for computer to chill...'
	sleep 60

testKaratsuba: clean
	mvn -Dtest=testKaratsuba test

testMap: clean
	mvn -Dtest=TestMap test > out.txt	
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testMap .\* > testMap.txt
endif
	cat out.txt | grep TestMap -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testMap 


testMatrixMult: clean
	mvn -Dtest=TestMatrixMultiplication test > out.txt
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testMatrixMult .\* > testMatrixMult.txt
endif
	cat out.txt | grep TestMatrixMultiplication -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testMatrixMult


testPrimes: clean
	mvn -Dtest=TestPrimes test > out.txt
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testPrimes .\* > testPrimes.txt
endif
	cat out.txt | grep TestPrimes -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testPrimes


testQuickSort: clean
	mvn -Dtest=TestQuickSort test > out.txt
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testQuickSort .\* > testQuickSort.txt
endif
	cat out.txt | grep TestQuickSort -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testQuickSort


allTestsStatistics: clean testFib testMap testMatrixMult 
	  
clean:
	mvn clean

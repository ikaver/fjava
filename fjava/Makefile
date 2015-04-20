all: build

build: clean
	mvn package -DskipTests

test: clean
	mvn package

testExpensiveMap: clean
	mvn -Dtest=testExpensiveMap test

testFib: clean
	mvn -Dtest=TestFibonacci test > out.txt	
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testFib .\* > testFib.txt
else
	cat out.txt | grep TestFibonacci -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testFib 
endif

testKaratsuba: clean
	mvn -Dtest=testKaratsuba test

testMap: clean
	mvn -Dtest=TestMap test > out.txt	
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testMap .\* > testMap.txt
else
	cat out.txt | grep TestMap -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testMap 
endif

testMatrixMult: clean
	mvn -Dtest=TestMatrixMultiplication test > out.txt
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testMatrixMult .\* > testMatrixMult.txt
else
	cat out.txt | grep TestMatrixMultiplication -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testMatrixMult
endif

testPrimes: clean
	mvn -Dtest=TestPrimes test > out.txt
	echo 'Creating graphs...'
ifeq ($(COLLECT_STATS), true)
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py testPrimes .\* > testPrimes.txt
else
	cat out.txt | grep TestPrimes -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py testPrimes
endif

testQuickSort: clean
	mvn -Dtest=testQuickSort test

allTestsStatistics: clean testFib testMap testMatrixMult 
	  
clean:
	mvn clean

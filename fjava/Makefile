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
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/statsgraph.py fib .\* > fibstats.txt
	cat out.txt | grep TestFibonacci -A1 | tail -n+3 | awk 'NR % 3 == 0 || NR % 3 == 2' | python graphs/timegraph.py fib 

testKaratsuba: clean
	mvn -Dtest=testKaratsuba test

testMap: clean
	mvn -Dtest=TestMap test

testMatrixMult: clean
	mvn -Dtest=TestMatrixMultiplication test

testPrimes: clean
	mvn -Dtest=testPrimes test

testQuickSort: clean
	mvn -Dtest=testQuickSort test



clean:
	mvn clean




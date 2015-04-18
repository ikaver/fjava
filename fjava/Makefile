all: build

build: clean
	mvn package -DskipTests

test: clean
	mvn package

testExpensiveMap: clean
	mvn -Dtest=testExpensiveMap test

testFib: clean
	mvn -Dtest=TestFibonacci test	
	echo 'Creating graphs...'
	cat log.txt | grep StatsTracker | cut -d "-" -f 2,3 | python  graphs/graph.py

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




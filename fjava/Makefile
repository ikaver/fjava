all: build

build: clean
	mvn package -DskipTests
 
clean:
	mvn clean

test: clean
	mvn package

testExpensiveMap: clean
	echo 'Running expensive map tests...'
	sh runSingleTest.sh testExpensiveMap testExpensiveMap

testFib: clean
	echo 'Running Fibonacci tests...'
	sh runSingleTest.sh TestFibonacci testFib

testKaratsuba: clean
	echo 'Running karatsuba...'
	sh runSingleTest.sh TestKaratsuba testKaratsuba 

testMap: clean
	echo 'Running map tests...'
	sh runSingleTest.sh TestMap testMap 

testMatrixMult: clean
	echo 'Running matrix multiplication tests...'
	sh runSingleTest.sh TestMatrixMultiplication testMatrixMult

testPrimes: clean
	echo 'Running primes tests...'
	sh runSingleTest.sh TestPrimes testPrimes

testQuickSort: clean
	echo 'Running quicksort tests...'
	sh runSingleTest.sh TestQuickSort testQuickSort

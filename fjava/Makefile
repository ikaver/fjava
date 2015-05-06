all: build

build: clean
	mvn package -DskipTests
 
clean:
	mvn clean

test: clean
	mvn package

TestExpensiveMap: clean
	echo 'Running expensive map tests...'
	sh scripts/runAllTests.sh testExpensiveMap

TestFibonacci: clean
	echo 'Running Fibonacci tests...'
	sh scripts/runAllTests.sh TestFibonacci 

TestKaratsuba: clean
	echo 'Running karatsuba...'
	sh scripts/runAllTests.sh TestKaratsuba 

TestMap: clean
	echo 'Running map tests...'
	sh scripts/runAllTests.sh TestMap

TestMatrixMultiplication: clean
	echo 'Running matrix multiplication tests...'
	sh scripts/runAllTests.sh TestMatrixMultiplication

TestPrimes: clean
	echo 'Running primes tests...'
	sh scripts/runAllTests.sh TestPrimes 

TestQuickSort: clean
	echo 'Running quicksort tests...'
	sh scripts/runAllTests.sh TestQuickSort

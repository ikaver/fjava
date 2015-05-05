all: build

build: clean
	mvn package -DskipTests
 
clean:
	mvn clean

test: clean
	mvn package

TestExpensiveMap: clean
	echo 'Running expensive map tests...'
	sh runSingleTest.sh testExpensiveMap

TestFibonacci: clean
	echo 'Running Fibonacci tests...'
	sh runSingleTest.sh TestFibonacci

TestKaratsuba: clean
	echo 'Running karatsuba...'
	sh runSingleTest.sh TestKaratsuba 

TestMap: clean
	echo 'Running map tests...'
	sh runSingleTest.sh TestMap

TestMatrixMultiplication: clean
	echo 'Running matrix multiplication tests...'
	sh runSingleTest.sh TestMatrixMultiplication

TestPrimes: clean
	echo 'Running primes tests...'
	sh runSingleTest.sh TestPrimes 

TestQuickSort: clean
	echo 'Running quicksort tests...'
	sh runSingleTest.sh TestQuickSort

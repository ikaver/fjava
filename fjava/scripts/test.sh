#!/bin/bash

if [ "$#" -ne 3 ]; then
    echo "Usage: test.sh MIN_POOL_SIZE MAX_POOL_SIZE POOL_SKIP"
    exit 1
fi

RESULTS_DIR=results

run_fjava_test() {
    #Run FJava test with specific pool size, algorithm, test type, and threshold.
    #Saves in 
    
    POOL_SIZE_ARG=$1
    ALGORITHM_ARG=$2
    TEST_TYPE=$3
    THRESHOLD=$4

    echo "Starting test with pool size $1 , algorithm $2 , test type $3 , threshold $4..."

    TEST_DIR=$RESULTS_DIR/$TEST_TYPE/$TEST_TYPE-$ALGORITHM_ARG-$POOL_SIZE_ARG-T$THRESHOLD
    mkdir -p $RESULTS_DIR/$TEST_TYPE || true
    mkdir -p $TEST_DIR || true
    
    export ALGORITHM=$ALGORITHM_ARG
    export POOL_SIZE=$POOL_SIZE_ARG
    export COLLECT_STATS="false"
    THRESHOLD_SUFFIX="_THRESHOLD"
    THRESHOLD_VAR_NAME=$TEST_TYPE$THRESHOLD_SUFFIX
    export $THRESHOLD_VAR_NAME=$THRESHOLD
    sh scripts/runSingleTest.sh $3 testFJava
    mv testFJava.txt $TEST_DIR/nostats.txt   
    
    export COLLECT_STATS="true"
    sh scripts/runSingleTest.sh $3 testFJava
    mv testFJava.txt $TEST_DIR/wstats.txt

    #mv $TEST_TYPE/* $TEST_DIR/ 
    #rm -r $TEST_TYPE
    #sleep 25
}

run_basic_test() {
    POOL_SIZE_ARG=$1
    ALGORITHM_ARG=$2
    TEST_TYPE=$3
    THRESHOLD=$4
    BASELINE=$5

    echo "Starting basic test with pool size $1 , algorithm $2 , test type $3 , threshold $4, baseline $5..."

    TEST_DIR=$RESULTS_DIR/$TEST_TYPE/$TEST_TYPE-$ALGORITHM_ARG-$POOL_SIZE_ARG-T$THRESHOLD
    export ALGORITHM=$ALGORITHM_ARG
    export POOL_SIZE=$POOL_SIZE_ARG
    export COLLECT_STATS="false"
    THRESHOLD_SUFFIX="_THRESHOLD"
    THRESHOLD_VAR_NAME=$TEST_TYPE$THRESHOLD_SUFFIX
    export $THRESHOLD_VAR_NAME=$THRESHOLD
    
    sh scripts/runSingleTest.sh $3 $BASELINE
    echo "COPYING $BASELINE.txt TO $TEST_DIR"
    cp $BASELINE.txt $TEST_DIR
}

copy_to_test_dir() {
    POOL_SIZE_ARG=$1
    ALGORITHM_ARG=$2
    TEST_TYPE=$3
    THRESHOLD=$4
    BASELINE=$5

    echo "Copying with pool size $1 , algorithm $2 , test type $3 , threshold $4, baseline $5..."

    TEST_DIR=$RESULTS_DIR/$TEST_TYPE/$TEST_TYPE-$ALGORITHM_ARG-$POOL_SIZE_ARG-T$THRESHOLD
    cp $BASELINE $TEST_DIR 
}

collect_results() {
    POOL_SIZE_ARG=$1
    ALGORITHM_ARG=$2
    TEST_TYPE=$3
    THRESHOLD=$4


    echo "Collecting results  with pool size $1 , algorithm $2 , test type $3 , threshold $4"

    TEST_DIR=$RESULTS_DIR/$TEST_TYPE/$TEST_TYPE-$ALGORITHM_ARG-$POOL_SIZE_ARG-T$THRESHOLD

    export COLLECT_STATS="false"
    sh scripts/collectResults.sh $3 $TEST_DIR    
    export COLLECT_STATS="true"
    sh scripts/collectResults.sh $3 $TEST_DIR
}

rm -r $RESULTS_DIR || true
mkdir -p $RESULTS_DIR

MIN_POOL=$1
MAX_POOL=$2
POOL_SKIP=$3

for TEST_STR in "TestFibonacci"; do #"TestMatrixMultiplication" "TestPrimes" "TestQuickSort" "TestKaratsuba"; do
   case "$TEST_STR" in
    "TestFibonacci") 
        THRESHOLDS=( 13 )
        ;;
    "TestMatrixMultiplication")
         THRESHOLDS=( 32 64 )
        ;;
    "TestPrimes")
         THRESHOLDS=( 100 500 1000 2000 5000 10000 250000 500000 )
        ;;
    "TestQuickSort")
        THRESHOLDS=( 40 400 4000 )
        ;;
    "TestKaratsuba")
        THRESHOLDS=( 50 100 1000 )
        ;;
    esac
    for THRESHOLD in "${THRESHOLDS[@]}"; do
        for ((POOL_SIZE=$MIN_POOL; POOL_SIZE<=$MAX_POOL; POOL_SIZE+=$POOL_SKIP))
        {
            for ALGORITHM_STR in "SID" "RID" "CONCURRENT"; do
                run_fjava_test $POOL_SIZE $ALGORITHM_STR $TEST_STR $THRESHOLD
            done
            run_basic_test $POOL_SIZE "NONE" $TEST_STR $THRESHOLD "testJavaForkJoin"
            for ALGORITHM_STR in "SID" "RID" "CONCURRENT"; do
                copy_to_test_dir $POOL_SIZE $ALGORITHM_STR $TEST_STR $THRESHOLD "testJavaForkJoin.txt"
            done
        }   
    done
    run_basic_test $POOL_SIZE "NONE" $TEST_STR $THRESHOLD "testSequential"
    for THRESHOLD in "${THRESHOLDS[@]}"; do
        for ((POOL_SIZE=$MIN_POOL; POOL_SIZE<=$MAX_POOL; POOL_SIZE+=$POOL_SKIP))
        {
            for ALGORITHM_STR in "SID" "RID" "CONCURRENT"; do
                copy_to_test_dir $POOL_SIZE $ALGORITHM_STR $TEST_STR $THRESHOLD "testSequential.txt"
                collect_results $POOL_SIZE $ALGORITHM_STR $TEST_STR $THRESHOLD
            done
        }
    done
done


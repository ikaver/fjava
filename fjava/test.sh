#!/bin/bash

if [ "$#" -ne 3 ]; then
    echo "Usage: test.sh MIN_POOL_SIZE MAX_POOL_SIZE POOL_SKIP"
    exit 1
fi

RESULTS_DIR=results

run_test() {
    POOL_SIZE_ARG=$1
    ALGORITHM_ARG=$2
    TEST_TYPE=$3
    THRESHOLD=$4

    echo "Starting test with pool size $1 , algorithm $2 , test type $3 , threshold $4..."

    TEST_DIR=$RESULTS_DIR/$TEST_TYPE/$TEST_TYPE-$ALGORITHM_ARG-$POOL_SIZE_ARG
    mkdir -p $RESULTS_DIR/$TEST_TYPE || true
    mkdir -p $TEST_DIR || true
    
    export ALGORITHM=$ALGORITHM_ARG
    export POOL_SIZE=$POOL_SIZE_ARG
    export COLLECT_STATS="false"
    THRESHOLD_SUFFIX="_THRESHOLD"
    THRESHOLD_VAR_NAME=$TEST_TYPE$THRESHOLD_SUFFIX
    export $THRESHOLD_VAR_NAME=$THRESHOLD
    make $3
    mv log.txt $TEST_DIR/log-nostats.txt
    mv out.txt $TEST_DIR/out-nostats.txt   
    
    export COLLECT_STATS="true"
    make $3
    mv log.txt $TEST_DIR/log-wstats.txt
    mv out.txt $TEST_DIR/out-wstats.txt
    mv $TEST_TYPE.txt $TEST_DIR/$TEST_TYPE.txt

    mv $TEST_TYPE/* $TEST_DIR/ 
    rm -r $TEST_TYPE
    sleep 25
}

rm -r $RESULTS_DIR || true
mkdir -p $RESULTS_DIR

MIN_POOL=$1
MAX_POOL=$2
POOL_SKIP=$3

for TEST_STR in "TestFibonacci" "TestMatrixMultiplication" "TestPrimes" "TestQuickSort" "TestKaratsuba"; do
   case "$TEST_STR" in
    "TestFibonacci") 
        THRESHOLDS=( 13 )
        ;;
    "TestMatrixMultiplication")
         THRESHOLDS=( 32 64 )
        ;;
    "TestPrimes")
         THRESHOLDS=( 100 1000 10000 )
        ;;
    "TestQuickSort")
        THRESHOLDS=( 40 400 4000 )
        ;;
    "TestKaratsuba")
        THRESHOLDS=( 50 100 1000 )
        ;;
    esac
    for THRESHOLD in "${THRESHOLDS[@]}"; do
        for ((i=$MIN_POOL; i<=$MAX_POOL; i+=$POOL_SKIP))
        {
            for ALGORITHM_STR in "SID" "RID" "CONCURRENT"; do
                run_test $i $ALGORITHM_STR $TEST_STR $THRESHOLD
            done
        }
    done
done


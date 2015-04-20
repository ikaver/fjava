#!/bin/bash

RESULTS_DIR=results

run_test() {
    POOL_SIZE_ARG=$1
    ALGORITHM_ARG=$2
    TEST_TYPE=$3

    TEST_DIR=$RESULTS_DIR/$TEST_TYPE/$TEST_TYPE-$ALGORITHM_ARG-$POOL_SIZE_ARG
    mkdir $RESULTS_DIR/$TEST_TYPE || true
    mkdir $TEST_DIR || true
    
    export COLLECT_STATS="true"
    export ALGORITHM=$ALGORITHM_ARG
    export POOL_SIZE=$POOL_SIZE_ARG
    make $3

    mv log.txt $TEST_DIR/log-wstats.txt
    mv out.txt $TEST_DIR/out-wstats.txt
    mv $TEST_TYPE.txt $TEST_DIR/$TEST_TYPE.txt
    export COLLECT_STATS="false"
    make $3
    mv log.txt $TEST_DIR/log-nostats.txt
    mv out.txt $TEST_DIR/out-nostats.txt
    mv $TEST_TYPE/* $TEST_DIR/ 
    rm -r $TEST_TYPE
}

rm -r $RESULTS_DIR || true
mkdir $RESULTS_DIR

MIN_POOL=2
MAX_POOL=4

for TEST_STR in "testFib" "testMatrixMult" "testPrimes" "testMap"; do
    for ((i=$MIN_POOL; i<=$MAX_POOL; i+=1))
    {
        for ALGORITHM_STR in "SID" "RID"; do
            run_test $i $ALGORITHM_STR $TEST_STR
        done
    }
done


#!/usr/bin/env bash

for day in day*
do
  echo $day
  SECONDS=0
  ( cd $day && ./run.sh | grep Part )
  echo Took $SECONDS secs.
done


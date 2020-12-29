#!/bin/bash

# if the script doesnt receive 3 commands, it ends
if [ $# -ne 3 ];
	then
		echo ERROR: Number of arguments is different than 3.
		exit 1
fi

# initializing vars
# removes last / from Dirs
inputDir="${1%/}"
outputDir="${2%/}"
maxNumThreads=$3
# number of threads goes from 1 to maxNumThreads
numThreads=1

# verifies all the 3 args and ends if at least 1 of them is invalid
if [ ! $maxNumThreads -gt 0 ] || [ ! -d $outputDir ] || [ ! -d $inputDir ];
	then
		echo ERROR: The arguments given are invalid.
		exit 1
fi

# iterates all .txt files in the given directory
for input in $inputDir/*.txt
do
	# presents info on each iteration
	echo InputFile="$input" NumThreads="$numThreads"

	# crops input var to create outputfile and assign it to a variable
	cutInputs="${input//"$inputDir/"}"
	cutTxt="${cutInputs//.txt}"
	outputFile="$outputDir"/"$cutTxt"-"$numThreads".txt

	# execute TecnicoFS for the given data
	./tecnicofs "$input" "$outputFile" "$numThreads" | tail -n 1

	# increments numThreads by 1 if it's not yet equal to maxNumThreads
	if [ $numThreads -lt $maxNumThreads ];
		then
			numThreads=`expr $numThreads + 1`
	fi
done
exit 0

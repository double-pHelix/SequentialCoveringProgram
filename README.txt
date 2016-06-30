How to setup Sequential Covering Algorithm

The project is written in Java and contains a few java Classes.

To run the project follow the instructions below.

How to compile program
	run "javac SequentialCovering.java"

How to run the program
	
	java SequentialCovering [LOCATION OF DATASET] [TARGET CLASS] [PERCENTAGE OF DATASET FOR TRAINING] [(optional) LOCATION OF HTML OUTPUT]
	
	e.g. 
	java SequentialCovering "C:/Data/mushroom.arff" e 30
	java SequentialCovering "C:/Data/mushroom.arff" p 30 "C:/Data"
	
	Defaults: if no value given for [LOCATION OF HTML OUTPUT], default will be the current working directory.
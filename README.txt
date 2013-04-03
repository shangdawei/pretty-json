PrettyJson

Copyright (c) Vladimir Dzhuvinov, 2011 - 2013


README


Pretty JSON formatter supporting standard and compact styling. It can be
integrated into a Java application or run as a simple utility from the command 
line that reads the JSON input from a file or STDIN and outputs the formatted 
JSON to another file or to STDOUT.


Command line usage:

	java -jar PrettyJson.jar [-compact] json-input-file [json-output-file]

or

	java -jar PrettyJson.jar [-compact] < json-std-in > json-std-out


The "-compact" command line option turns compact style formatting on.


Requirements:

	* Java 1.5 or later
	
	* net.minidev.json and net.minidev.json.parser from the JSON Smart 
	  library [http://code.google.com/p/json-smart/]


[EOF]

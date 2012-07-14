PrettyJson

Copyright (c) Vladimir Dzhuvinov, 2011 - 2012


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

All required package dependencies are included in the PrettyJson.jar file.



Package content:

	README.txt                This file.
	
	LICENSE.txt               The software license.
	
	CHANGELOG.txt             The change log.
	
	PrettyJson.jar            JAR file for running PrettyJson as a command
	                          line utility. Includes the JSON.simple toolkit
				  dependency.
	
	pretty_json-{version}.jar JAR file for embedding PrettyJson into Java
	                          applications. Doesn't include the JSON.simple
				  toolkit dependency.
	
	javadoc/                  The Java Docs for this package.
	
	build.xml                 The Apache Ant build file.
	
	lib/                      The package dependencies and their licenses.
	
	src/                      The source code for this package.



For complete PrettyJson documentation and updates visit:
	
	http://software.dzhuvinov.com/pretty-json.html



[EOF]

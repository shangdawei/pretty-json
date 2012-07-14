package com.thetransactioncompany.json.pretty;


import java.io.*;
import java.text.*;
import java.util.*;

import net.minidev.json.*;
import net.minidev.json.parser.*;


/**
 * Pretty JSON formatter. Supports two formatting styles:
 *
 * <ul>
 *     <li>{@link Style#DEFAULT} Regular pretty JSON formatting.
 *     <li>{@link Style#COMPACT} Reduces the number of vertical lines by 
 *         opening new JSON arrays and objects on the same line.
 * </ul>
 *
 * <p>Smart feature: prints the items in a JSON array on a single line if their
 * types are non-complex and their count is equal or less than three.
 *
 * <p>Example {@link Style#DEFAULT} formatting:
 *
 * <pre>
 * {
 *   "name"       : "Alice Wonderland",
 *   "givenName"  : "Alice",
 *   "surname"    : "Wonderland,
 *   "age"        : 21,
 *   "isVIP"      : true,
 *   "email"      : null,
 *   "friends"    : [ "Bob", "Clair", "Dan" ],
 *   "dictionary" : {
 *                    "one"   : "eins",
 *                    "two"   : "zwei",
 *                    "three" : "drei"
 *                  }
 * }
 * </pre>
 *
 * <p>Example {@link Style#COMPACT} formatting:
 *
 * <pre>
 * { "name"       : "Alice Wonderland",
 *   "givenName"  : "Alice",
 *   "surname"    : "Wonderland,
 *   "age"        : 21,
 *   "isVIP"      : true,
 *   "email"      : null,
 *   "friends"    : [ "Bob", "Clair", "Dan" ],
 *   "dictionary" : { "one"   : "eins",
 *                    "two"   : "zwei",
 *                    "three" : "drei" } }
 * </pre>
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2012-07-14)
 */
public class PrettyJson {


	/**
	 * Enumeration of the formatting styles.
	 */
	public static enum Style {
	
	
		/**
		 * Default formatting style.
		 */
		DEFAULT,


		/**
		 * Compact formatting style.
		 */
		COMPACT;
	}
	
	
	/**
	 * Reusable parse instance.
	 */
	private static JSONParser parser = new JSONParser();
	
	
	/**
	 * Special container factory for constructing JSON objects in a way
	 * that preserves their original member order.
	 */
	private static final ContainerFactory containerFactory = new ContainerFactory() {
		
		public List<Object> createArrayContainer() {
			return new LinkedList<Object>();
		}

		public Map<java.lang.String,java.lang.Object> createObjectContainer() {
			return new LinkedHashMap<java.lang.String,java.lang.Object>();
		}       
	};


	
	
	/**
	 * The chosen formatting style.
	 */
	private Style style;
	
	
	/**
	 * Creates a new pretty JSON formatter with the specified style. The
	 * currently supported styles are {@link Style#DEFAULT} and 
	 * {@link Style#COMPACT}.
	 *
	 * @param style The formatting style. Must not be {@code null}.
	 */
	public PrettyJson (final Style style) {
	
		if (style == null)
			throw new IllegalArgumentException("The style must not be null");
	
		this.style = style;
	}
	
	
	/**
	 * Creates a new pretty JSON formatter set to {@link Style#DEFAULT}.
	 */
	public PrettyJson () {
	
		this(Style.DEFAULT);
	}
	
	
	/**
	 * Sets the formatting style.
	 *
	 * @param style The formatting style. Must not be {@code null}.
	 */
	public void setStyle (final Style style) {
	
		if (style == null)
			throw new IllegalArgumentException("The style must not be null");
		
		this.style = style;
	}
	
	
	/**
	 * Gets the formatting style.
	 *
	 * @return The formatting style.
	 */
	public Style getStyle () {
	
		return style;
	}
	

	/**
	 * Repeats the specified character.
	 *
	 * @param c     The character to repeat.
	 * @param times The number of times to repeat.
	 *
	 * @return The repeated character as a string.
	 */
	public static String repeat(char c, int times){
	
		StringBuffer b = new StringBuffer();

		for(int i=0;i < times;i++)
			b.append(c);
	
		return b.toString();
	}


	/**
	 * Formats a JSON boolean.
	 * 
	 * @param b The JSON boolean value.
	 *
	 * @return The formatted boolean.
	 */
	public String format(boolean b) {
	
		if (b)
			return "true";
		else
			return "false";
	}
	
	
	/**
	 * Formats a JSON number.
	 *
	 * @param n The JSON number value.
	 *
	 * @return The formatted number.
	 */
	public String format(Number n) {
	
		return n.toString();
	}
	
	
	/**
	 * Formats a JSON string. Special characters are escaped.
	 *
	 * @param s The JSON string value.
	 *
	 * @return The formatted string.
	 */
	public String format(String s) {
	
		return "\"" + JSONObject.escape(s) + "\"";
	}
	
	
	/**
	 * Formats a JSON {@code null}.
	 *
	 * @return The formatted {@code null}.
	 */
	public String formatNull() {
	
		return "null";
	}
	
	
	/**
	 * Formats a JSON array.
	 *
	 * @param a      The JSON array, represented as a 
	 *               {@code java.util.List}.
	 * @param indent The indentation level (number of characters). 
	 *
	 * @return The formatted array.
	 */
	@SuppressWarnings("unchecked")
	public String format(final List<Object> a, final int indent) {
	
		String s = "[ ";
		
		if (a.isEmpty())
			return s + "]";
		
		// Check whether the JSON array has any complex
		// types, e.g. JSON object or other arrays
		boolean hasComplexItems = false;
		
		Iterator<Object> it = a.iterator();
		
		while(it.hasNext()) {
		
			Object item = it.next();
			
			if (    item != null             &&
			     ! (item instanceof Boolean) &&
			     ! (item instanceof Number)  &&
			     ! (item instanceof String)     ) {
			     
				hasComplexItems = true;
				break;
			}	
		}
		
		// Line break each item if one of the items
		// is a complex datatype or the total count is 3+
		boolean breakItems;
		
		if (hasComplexItems || a.size() > 3)
			breakItems = true;
		else
			breakItems = false;
		
		// Do the actual formatting now
		it = a.iterator();
		
		final int subIndent = indent + 2;
		
		boolean first = true;
		
		while (it.hasNext()) {
		
			Object item = it.next();
			
			// First array item?
			if (first) {
			
				first = false; // change flag
				
				if (style == Style.DEFAULT && breakItems) {
					s += "\n" + repeat(' ', indent + 2);
				}
				else if (style == Style.COMPACT || ! breakItems) {
					// proceed immediately
				}
			}
			else {
				if (breakItems)
					s += repeat(' ', indent + 2);
			}
			
			
			if (item instanceof Boolean)
				s += format((Boolean)item);
			
			else if (item instanceof Number)
				s += format((Number)item);
			
			else if (item instanceof String)
				s += format((String)item);
				
			else if (item instanceof List)
				s += format((List)item, subIndent);
				
			else if (item instanceof Map)
				s += format((Map)item, subIndent);
			
			else if (item == null)
				s += formatNull();
		
			if (it.hasNext()) {
				s += ", ";
				if (breakItems)
					s += "\n";
			}
		}
		
		if (style == Style.DEFAULT) {
			if (breakItems)
				return s + "\n" + repeat(' ', indent) + "]";
			else
				return s + " ]";
		}
		else {
			// STYLE_COMPACT
			return s + " ]";
		}
	}
	
	
	/**
	 * Formats a JSON array at zero indentation.
	 *
	 * @param a The JSON array, represented as a 
	 *          {@code java.util.List}.
	 *
	 * @return The formatted array.
	 */
	public String format(final List<Object> a) {
		
		return format(a, 0);
	}
	
	
	/**
	 * Formats a JSON object.
	 *
	 * @param o      The JSON object, represented as a 
	 *               {@code java.util.Map}.
	 * @param indent The indentation level (number of characters). 
	 *
	 * @return The formatted object.
	 */
	@SuppressWarnings("unchecked")
	public String format(final Map<String,Object> o, final int indent) {
	
		String s = "{ ";
		
		if (o.isEmpty())
			return s + "}";
		
		// Check the max key length so that all
		// object values are nicely lined up
		int maxKeyLen = 0;
		
		Iterator <String> keyIter = o.keySet().iterator();
		
		while (keyIter.hasNext()) {
		
			String key = keyIter.next();
			
			if (key.length() > maxKeyLen)
				maxKeyLen = key.length();
		}
		
		// Finally, format each entry
		Iterator <Map.Entry<String,Object>> it = o.entrySet().iterator();
		
		final int subIndent = indent + maxKeyLen + 7;
		
		boolean first = true;
		
		while (it.hasNext()) {
		
			Map.Entry<String,Object> pair = it.next();
			
			String key = pair.getKey();
			
			// First item?
			if (first) {
			
				first = false; // change flag
				
				if (style == Style.DEFAULT) {
					s += "\n" + repeat(' ', indent + 2);
				}
				else if (style == Style.COMPACT) {
					// proceed immediately
				}
			}
			else {
				// indent on new line for both styles
				s += repeat(' ', indent + 2);
			}
			
			s += "\"" + JSONObject.escape(key) + "\"";
			
			s += repeat(' ', maxKeyLen - key.length()) + " : ";
			
			Object value = pair.getValue();
			
			if (value instanceof Boolean)
				s += format((Boolean)value);
			
			else if (value instanceof Number)
				s += format((Number)value);
			
			else if (value instanceof String)
				s += format((String)value);
				
			else if (value instanceof List)
				s += format((List)value, subIndent);
				
			else if (value instanceof Map)
				s += format((Map)value, subIndent);
			
			else if (value == null)
				s += formatNull();
		
			if (it.hasNext()) {
				s += ",";
				s += "\n";
			}
		}
	
		if (style == Style.DEFAULT) {
			return s + "\n" + repeat(' ', indent) + "}";
		}
		else {
			// STYLE_COMPACT
			return s + " }";
		}
	}
	
	
	/**
	 * Formats a JSON object at zero indentation.
	 *
	 * @param o The JSON object, represented as a 
	 *          {@code java.util.Map}.
	 *
	 * @return The formatted object.
	 */
	public String format(final Map<String,Object> o) {
	
		return format(o, 0);
	}
	
	
	/**
	 * Parses and formats the specified JSON input.
	 *
	 * @param jsonTxt The JSON string.
	 *
	 * @return The pretty formatted JSON string.
	 *
	 * @throws java.text.ParseException On parse exception.
	 */
	@SuppressWarnings("unchecked")
	public String parseAndFormat(final String jsonTxt)
		throws java.text.ParseException {
	
		Object o = null;
		
		try {
			o = parser.parse(jsonTxt, containerFactory);
			
		} catch (net.minidev.json.parser.ParseException e) {
		
			throw new java.text.ParseException(e.getMessage(), e.getPosition());
		}
		
		if (o == null)
			return formatNull();
		else if (o instanceof Boolean)
			return format((Boolean)o);
		else if (o instanceof Number)
			return format((Number)o);
		else if (o instanceof String)
			return format((String)o);
		else if (o instanceof List)
			return format((List)o);
		else if (o instanceof Map)
			return format((Map)o);
		else
			return ""; // shouldn't happen
	}
	
	
	/**
	 * Reads the standard input into a string.
	 *
	 * @return The standard input as a string.
	 *
	 * @throws IOException If an I/O exception was encountered.
	 */
	private static String readStdIn()
		throws IOException {

        	StringBuilder contents = new StringBuilder();

        	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			String line = null;
				
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		}
		finally {
			input.close();
		}
		
                return contents.toString();
	}
	
	
	/**
	 * Reads the content of the specified file into a string.
	 *
	 * @return The file content as a string.
	 *
	 * @throws IOException If an I/O exception was encountered.
	 */
	private static String readFile(final File file)
		throws IOException {
	
		StringBuilder contents = new StringBuilder();
		
		BufferedReader input = new BufferedReader(new FileReader(file));

		try {
			String line = null;

			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		}
		finally {
			input.close();
		}
		
		return contents.toString();
	}
	
	
	/**
	 * Writes out a file.
	 *
	 * @param file    The file.
	 * @param content The file content as a string.
	 *
	 * @throws IOException If an I/O exception was encountered.
	 */
	private static void writeFile(final File file, final String content)
		throws IOException {
	
		FileWriter fstream = new FileWriter(file);
		
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write(content);
		
		out.close();
	}
	
	
	/**
	 * Prints a usage message about the PrettyJson console application to 
	 * STDOUT.
	 */
	protected static void printUsage() {
	
		System.out.println("Usage: java -jar PrettyJson.jar [-compact] json-input-file [json-output-file]");
		System.out.println("   or: java -jar PrettyJson.jar [-compact] < json-std-in > json-std-out");
	}
	
	
	/**
	 * Parses a JSON file and outputs it in pretty format.
	 *
	 * <p>The raw JSON input can be read from a file or from STDIN. The
	 * pretty formatted JSON can be written to a file or to STDOUT. The
	 * optional {@code -compact} command line argument turns 
	 * {@link Style#COMPACT} formatting on.
	 *
	 * <pre>
	 * Usage: java -jar PrettyJson.jar [-compact] json-input-file [json-output-file]
	 *    or: java -jar PrettyJson.jar [-compact] < json-std-in > json-std-out
	 * </pre>
	 *
	 * @param args The command line arguments.
	 */
	public static void main (final String[] args) {
	
		// Parse command line arguments
		
		File inFile = null;
		File outFile = null;
		boolean compactStyleOn = false;
		
		for (String arg: args) {
			
			if (arg.equals("-compact")) {
				compactStyleOn = true;
			}
			else if (inFile == null) {
				inFile = new File(arg);
			}
			else if (outFile == null) {
				outFile = new File(arg);
			}
			else {
				printUsage();
				System.exit(1);
			}
		}
		
		String jsonInput = null;
		
	
		// Read the JSON input, either from the cmd line arg 1 or from STDIN
		
		try {
			if (inFile != null) {
			
				jsonInput = readFile(inFile);
			}
			else if (System.in.available() > 0) {
				
				jsonInput = readStdIn();
			}
			else {
				printUsage();
				System.exit(1);
			}
			
		} catch (IOException e) {
			
			System.err.println("Error reading JSON input: " + e.getMessage());
			System.exit(1);
		}
		
		
		// Create pretty JSON formatter
		
		PrettyJson formatter = null;
		
		if (compactStyleOn)
			formatter = new PrettyJson(Style.COMPACT);
		else
			formatter = new PrettyJson(Style.DEFAULT);
		
		
		// Do format
		
		String jsonOutput = null;
		
		try {
			jsonOutput = formatter.parseAndFormat(jsonInput);
			
		} catch (java.text.ParseException e) {
		
			System.err.println("Error parsing JSON input: " + e.getMessage());
			System.exit(1);
		}
		
		if (outFile != null) {
		
			// print to file
			try {
				writeFile(outFile, jsonOutput + "\n");
			
			} catch (IOException e) {
			
				System.err.println("Error writing JSON output: " + e.getMessage());
				System.exit(1);
			}
		}
		else {
			// print to STD OUT
			System.out.println(jsonOutput);
		}
	}
}

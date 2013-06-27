package com.analysis;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlManager {
	// Constants
	static final String File = "file";
	static final String Function = "function";
	static final String PregReplace = "preg_replace";
	static final String StrReplace = "str_replace";
	static final String En2Ar = "en2ar";
	static final String Ar2En = "ar2en";
	static final String Search = "search";
	static final String Replace = "replace";
	static final String  __construct ="__construct";
	// To store preg replace en2ar translations
	private static Map<String, String> pregReplaceEn2ArMap = new HashMap<String, String>();
	// To store str replace en2ar translations
	private static Map<String, String> strReplaceEn2ArMap = new HashMap<String, String>();
	// To store preg replace ar2en translations
	private static Map<String, String> pregReplaceAr2EnMap = new HashMap<String, String>();
	// To store str replace ar2en translations
	private static Map<String, String> strReplaceAr2EnMap = new HashMap<String, String>();
	// To store meanings
		private static Map<String, String> meaningsMap = new HashMap<String, String>();
		// To store derivations
				private static Map<String, String> derivationsMap = new HashMap<String, String>();


	/**
	 * To convert file to byte of array
	 * 
	 * @param file
	 * @return the passed file as byte array
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(java.io.File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new IOException("File is too larg " + file.getName());
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * To parse translation xml
	 * 
	 * @param inputStream
	 */
	public static void parseTranslation(InputStream inputStream) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			String file = document.getDocumentElement().getAttribute(File);
			NodeList listGroup = document.getDocumentElement().getChildNodes();
			for (int index = 0; index < listGroup.getLength(); index++) {
				Node group = listGroup.item(index);
				if (group.getNodeType() == Node.ELEMENT_NODE) {
					Element groupElment = (Element) group;
					if (groupElment.getNodeName().equalsIgnoreCase(PregReplace)) {
						String function = groupElment.getAttribute(Function);
						if (function.equalsIgnoreCase(En2Ar)) {
							NodeList nodeList = groupElment.getChildNodes();
							fillMap(pregReplaceEn2ArMap, nodeList);
						} else {
							NodeList nodeList = groupElment.getChildNodes();
							fillMap(pregReplaceAr2EnMap, nodeList);
						}
					} else if (groupElment.getNodeName().equalsIgnoreCase(
							StrReplace)) {
						String function = groupElment.getAttribute(Function);
						if (function.equalsIgnoreCase(En2Ar)) {
							NodeList nodeList = groupElment.getChildNodes();
							fillMap(strReplaceEn2ArMap, nodeList);
						} else {
							NodeList nodeList = groupElment.getChildNodes();
							fillMap(strReplaceAr2EnMap, nodeList);
						}
					}
				}
			}
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	
	/**
	 * To parse meaning xml
	 * 
	 * @param inputStream
	 */
	public static void parseMeanings(InputStream inputStream) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			String file = document.getDocumentElement().getAttribute(File);
			NodeList listGroup = document.getDocumentElement().getChildNodes();
			for (int index = 0; index < listGroup.getLength(); index++) {
				Node group = listGroup.item(index);
				if (group.getNodeType() == Node.ELEMENT_NODE) {
					Element groupElment = (Element) group;
					if (groupElment.getNodeName().equalsIgnoreCase(PregReplace)) {
						String function = groupElment.getAttribute(Function);
						if (function.equalsIgnoreCase(__construct)) {
							NodeList nodeList = groupElment.getChildNodes();
							fillMap(meaningsMap, nodeList);
						}
					}
				}
			}
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	
	/**
	 * To parse derivations xml
	 * 
	 * @param inputStream
	 */
	public static void parseDerivations(InputStream inputStream) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			String file = document.getDocumentElement().getAttribute(File);
			NodeList listGroup = document.getDocumentElement().getChildNodes();
			for (int index = 0; index < listGroup.getLength(); index++) {
				Node group = listGroup.item(index);
				if (group.getNodeType() == Node.ELEMENT_NODE) {
					Element groupElment = (Element) group;
					if (groupElment.getNodeName().equalsIgnoreCase(PregReplace)) {
						String function = groupElment.getAttribute(Function);
						if (function.equalsIgnoreCase(__construct)) {
							NodeList nodeList = groupElment.getChildNodes();
							fillMap(derivationsMap, nodeList);
						}
					}
				}
			}
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}


	/**
	 * To get translation for specific word
	 * 
	 * @param word
	 * @return
	 */
	public static String getTranslation(String word) {
		String firstSideTranslation = "";
		String lastSideTranslation = "";
		String translation = "";
		String word1 = "";
		// check if empty word
		if (word == null || word.equals("") || word.length() == 0) {
			return null;
		}
		// process begin of word
		boolean match = false;
		int firstIndex = 0;
		// process first two characters
		if (word.length() >= 2) {
			String firstSide = word.substring(0, 2);
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("/", ""));
				Matcher matcher = pattern.matcher(firstSide);
				if (matcher.matches()) {
					firstSideTranslation = pregReplaceEn2ArMap.get(key);
					match = true;
					firstIndex = 2;
					break;
				}
			}
		}
		// process first one character
		if (!match && word.length() >= 1) {
			String firstSide = word.substring(0, 1);
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("/", ""));
				Matcher matcher = pattern.matcher(firstSide);
				if (matcher.matches()) {
					firstSideTranslation = pregReplaceEn2ArMap.get(key);
					firstIndex = 1;
					break;
				}
			}
		}
		// process end of word
		word1 = word.substring(firstIndex, word.length());
		match = false;
		int lastIndex = 0;
		// process last four characters
		if (word1.length() >= 4) {
			String lastSide = word1.substring(word1.length() - 4, word1
					.length());
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("$", ""));
				Matcher matcher = pattern.matcher(lastSide);
				if (matcher.matches()) {
					lastSideTranslation = pregReplaceEn2ArMap.get(key);
					lastIndex = 4;
					match = true;
					break;
				}
			}
		}
		// process last two characters
		if (!match && word1.length() >= 2) {
			String lastSide = word1.substring(word1.length() - 2, word1
					.length());
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("$", ""));
				Matcher matcher = pattern.matcher(lastSide);
				if (matcher.matches()) {
					lastSideTranslation = pregReplaceEn2ArMap.get(key);
					lastIndex = 2;
					match = true;
					break;
				}
			}
		}
		// process last one character
		if (!match && word1.length() >= 2) {
			String lastSide = word1.substring(word1.length() - 1, word1
					.length());
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("$", ""));
				Matcher matcher = pattern.matcher(lastSide);
				if (matcher.matches()) {
					lastSideTranslation = pregReplaceEn2ArMap.get(key);
					lastIndex = 1;
					match = true;
					break;
				}
			}
		}
		// process remaining word
		String remainWord = word.substring(firstIndex, word.length()
				- lastIndex);
		translation = firstSideTranslation;
		int index = 0;
		while (index < remainWord.length()) {
			if (remainWord.length() >= 4) {
				String str = strReplaceEn2ArMap.get(remainWord.substring(0, 4));
				if (str != null) {
					translation += str;
					remainWord = remainWord.substring(4, remainWord.length());
					continue;
				}
			}
			if (remainWord.length() >= 3) {
				String str = strReplaceEn2ArMap.get(remainWord.substring(0, 3));
				if (str != null) {
					translation += str;
					remainWord = remainWord.substring(3, remainWord.length());
					continue;
				}
			}
			if (remainWord.length() >= 2) {
				String str = strReplaceEn2ArMap.get(remainWord.substring(0, 2));
				if (str != null) {
					translation += str;
					remainWord = remainWord.substring(2, remainWord.length());
					continue;
				}
			}
			if (remainWord.length() >= 1) {
				String str = strReplaceEn2ArMap.get(remainWord.substring(0, 1));
				if (str != null) {
					translation += str;
					remainWord = remainWord.substring(1, remainWord.length());
					continue;
				}
			}

		}
		translation += lastSideTranslation;
		return translation;
	}
	
	
	/**
	 * To fill translations in map
	 * 
	 * @param map
	 * @param nodeList
	 */
	public static void fillMap(Map<String, String> map, NodeList nodeList) {
		for (int index1 = 0; index1 < nodeList.getLength(); index1++) {
			Node node = nodeList.item(index1);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nodEelment = (Element) node;
				String search = null;
				String replace = null;
				NodeList list = nodEelment.getChildNodes();
				for (int index2 = 0; index2 < list.getLength(); index2++) {
					Node node1 = list.item(index2);
					if (node1.getNodeType() == Node.ELEMENT_NODE) {
						if (node1.getNodeName().equalsIgnoreCase(Search)) {
							search = node1.getTextContent();
						} else if (node1.getNodeName()
								.equalsIgnoreCase(Replace)) {
							replace = node1.getTextContent();
						}
					}
				}
				map.put(search, replace);
				search = null;
				replace = null;
			}
		}
	}

	public static Map<String, String> getPregReplaceEn2ArMap() {
		return pregReplaceEn2ArMap;
	}

	public static void setPregReplaceEn2ArMap(
			Map<String, String> pregReplaceEn2ArMap) {
		XmlManager.pregReplaceEn2ArMap = pregReplaceEn2ArMap;
	}

	public static Map<String, String> getStrReplaceEn2ArMap() {
		return strReplaceEn2ArMap;
	}

	public static void setStrReplaceEn2ArMap(Map<String, String> strReplaceEn2ArMap) {
		XmlManager.strReplaceEn2ArMap = strReplaceEn2ArMap;
	}

	public static Map<String, String> getPregReplaceAr2EnMap() {
		return pregReplaceAr2EnMap;
	}

	public static void setPregReplaceAr2EnMap(
			Map<String, String> pregReplaceAr2EnMap) {
		XmlManager.pregReplaceAr2EnMap = pregReplaceAr2EnMap;
	}

	public static Map<String, String> getStrReplaceAr2EnMap() {
		return strReplaceAr2EnMap;
	}

	public static void setStrReplaceAr2EnMap(Map<String, String> strReplaceAr2EnMap) {
		XmlManager.strReplaceAr2EnMap = strReplaceAr2EnMap;
	}

	public static Map<String, String> getMeaningsMap() {
		return meaningsMap;
	}

	public static void setMeaningsMap(Map<String, String> meaningsMap) {
		XmlManager.meaningsMap = meaningsMap;
	}

	public static Map<String, String> getDerivationsMap() {
		return derivationsMap;
	}

	public static void setDerivationsMap(Map<String, String> derivationsMap) {
		XmlManager.derivationsMap = derivationsMap;
	}
	
}

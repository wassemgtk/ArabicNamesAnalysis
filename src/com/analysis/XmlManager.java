package com.analysis;

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
	private final String File = "file";
	private final String Function = "function";
	private final String PregReplace = "preg_replace";
	private final String StrReplace = "str_replace";
	private final String En2Ar = "en2ar";
	private final String Ar2En = "ar2en";
	private final String Search = "search";
	private final String Replace = "replace";
	private final String __construct = "__construct";
	// To store preg replace en2ar translations
	private Map<String, String> pregReplaceEn2ArMap = new HashMap<String, String>();
	// To store str replace en2ar translations
	private Map<String, String> strReplaceEn2ArMap = new HashMap<String, String>();
	// To store preg replace ar2en translations
	private Map<String, String> pregReplaceAr2EnMap = new HashMap<String, String>();
	// To store str replace ar2en translations
	private Map<String, String> strReplaceAr2EnMap = new HashMap<String, String>();
	// To store meanings
	private Map<String, String> meaningsMap = new HashMap<String, String>();
	// To store derivations
	private Map<String, String> derivationsMap = new HashMap<String, String>();
	// To store names and their genders
	private Map<String, String> namesMap = new HashMap<String, String>();

	public XmlManager() {
	}

	/**
	 * To parse translation xml
	 *
	 * @param inputStream
	 * @throws XmlParseException
	 */
	public void parseTranslation(InputStream inputStream) throws XmlParseException {
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
		} catch (Exception exception) {
			throw new XmlParseException("Failed to parse translation XML", exception);
		}
	}

	/**
	 * To parse meaning xml
	 *
	 * @param inputStream
	 * @throws XmlParseException
	 */
	public void parseMeanings(InputStream inputStream) throws XmlParseException {
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
		} catch (Exception exception) {
			throw new XmlParseException("Failed to parse meaning XML", exception);
		}
	}

	/**
	 * To parse derivations xml
	 *
	 * @param inputStream
	 * @throws XmlParseException
	 */
	public void parseDerivations(InputStream inputStream) throws XmlParseException {
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
		} catch (Exception exception) {
			throw new XmlParseException("Failed to parse derivations XML", exception);
		}
	}

	/**
	 * To parse names xml
	 *
	 * @param inputStream
	 * @throws XmlParseException
	 */
	public void parseNames(InputStream inputStream) throws XmlParseException {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			NodeList listGroup = document.getDocumentElement().getChildNodes();
			for (int index = 0; index < listGroup.getLength(); index++) {
				Node group = listGroup.item(index);
				if (group.getNodeType() == Node.ELEMENT_NODE) {
					Element groupElment = (Element) group;
					if (groupElment.getNodeName().equalsIgnoreCase("name")) {
						String gender = groupElment.getAttribute("gender");
						String name = groupElment.getTextContent();
						namesMap.put(name, gender);
					}
				}
			}
		} catch (Exception exception) {
			throw new XmlParseException("Failed to parse names XML", exception);
		}
	}

	/**
	 * To get translation for a specific word, handling various cases.
	 *
	 * @param word The word to be translated.
	 * @return The translated word, or null if the input is empty.
	 */
	public String getTranslation(String word) {
		if (word == null || word.isEmpty()) {
			return null;
		}

		// Process the beginning of the word to find a matching prefix
		String firstSideTranslation = "";
		int firstIndex = 0;

		// Check for a two-character prefix
		if (word.length() >= 2) {
			String firstSide = word.substring(0, 2);
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("/", ""));
				Matcher matcher = pattern.matcher(firstSide);
				if (matcher.matches()) {
					firstSideTranslation = pregReplaceEn2ArMap.get(key);
					firstIndex = 2;
					break;
				}
			}
		}

		// If no two-character prefix is found, check for a one-character prefix
		if (firstIndex == 0 && word.length() >= 1) {
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

		// Process the end of the word to find a matching suffix
		String lastSideTranslation = "";
		int lastIndex = 0;
		String remainingWord = word.substring(firstIndex);

		// Check for a four-character suffix
		if (remainingWord.length() >= 4) {
			String lastSide = remainingWord.substring(remainingWord.length() - 4);
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("$", ""));
				Matcher matcher = pattern.matcher(lastSide);
				if (matcher.matches()) {
					lastSideTranslation = pregReplaceEn2ArMap.get(key);
					lastIndex = 4;
					break;
				}
			}
		}

		// If no four-character suffix is found, check for a two-character suffix
		if (lastIndex == 0 && remainingWord.length() >= 2) {
			String lastSide = remainingWord.substring(remainingWord.length() - 2);
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("$", ""));
				Matcher matcher = pattern.matcher(lastSide);
				if (matcher.matches()) {
					lastSideTranslation = pregReplaceEn2ArMap.get(key);
					lastIndex = 2;
					break;
				}
			}
		}

		// If no two-character suffix is found, check for a one-character suffix
		if (lastIndex == 0 && remainingWord.length() >= 1) {
			String lastSide = remainingWord.substring(remainingWord.length() - 1);
			for (String key : pregReplaceEn2ArMap.keySet()) {
				Pattern pattern = Pattern.compile(key.replace("$", ""));
				Matcher matcher = pattern.matcher(lastSide);
				if (matcher.matches()) {
					lastSideTranslation = pregReplaceEn2ArMap.get(key);
					lastIndex = 1;
					break;
				}
			}
		}

		// Process the middle of the word
		String middleWord = remainingWord.substring(0, remainingWord.length() - lastIndex);
		String middleTranslation = "";
		int index = 0;
		while (index < middleWord.length()) {
			boolean matchFound = false;
			// Check for 4-character matches
			if (middleWord.length() - index >= 4) {
				String sub = middleWord.substring(index, index + 4);
				if (strReplaceEn2ArMap.containsKey(sub)) {
					middleTranslation += strReplaceEn2ArMap.get(sub);
					index += 4;
					matchFound = true;
				}
			}
			// Check for 3-character matches
			if (!matchFound && middleWord.length() - index >= 3) {
				String sub = middleWord.substring(index, index + 3);
				if (strReplaceEn2ArMap.containsKey(sub)) {
					middleTranslation += strReplaceEn2ArMap.get(sub);
					index += 3;
					matchFound = true;
				}
			}
			// Check for 2-character matches
			if (!matchFound && middleWord.length() - index >= 2) {
				String sub = middleWord.substring(index, index + 2);
				if (strReplaceEn2ArMap.containsKey(sub)) {
					middleTranslation += strReplaceEn2ArMap.get(sub);
					index += 2;
					matchFound = true;
				}
			}
			// Check for 1-character matches
			if (!matchFound && middleWord.length() - index >= 1) {
				String sub = middleWord.substring(index, index + 1);
				if (strReplaceEn2ArMap.containsKey(sub)) {
					middleTranslation += strReplaceEn2ArMap.get(sub);
					index += 1;
				}
			}
		}

		return firstSideTranslation + middleTranslation + lastSideTranslation;
	}


	/**
	 * To fill translations in map
	 *
	 * @param map
	 * @param nodeList
	 */
	public void fillMap(Map<String, String> map, NodeList nodeList) {
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

	public Map<String, String> getPregReplaceEn2ArMap() {
		return pregReplaceEn2ArMap;
	}

	public Map<String, String> getStrReplaceEn2ArMap() {
		return strReplaceEn2ArMap;
	}

	public Map<String, String> getPregReplaceAr2EnMap() {
		return pregReplaceAr2EnMap;
	}

	public Map<String, String> getStrReplaceAr2EnMap() {
		return strReplaceAr2EnMap;
	}

	public Map<String, String> getMeaningsMap() {
		return meaningsMap;
	}

	public Map<String, String> getDerivationsMap() {
		return derivationsMap;
	}

	public Map<String, String> getNamesMap() {
		return namesMap;
	}
}

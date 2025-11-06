package com.analysis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlManager {
    // Constants
    private static final String FILE_ATTR = "file";
    private static final String FUNCTION_ATTR = "function";
    private static final String PREG_REPLACE = "preg_replace";
    private static final String STR_REPLACE = "str_replace";
    private static final String EN2AR = "en2ar";
    private static final String AR2EN = "ar2en";
    private static final String SEARCH = "search";
    private static final String REPLACE = "replace";
    private static final String CONSTRUCT = "__construct";

    // Maps to store parsed data
    private final Map<String, String> pregReplaceEn2ArMap = new HashMap<>();
    private final Map<String, String> strReplaceEn2ArMap = new HashMap<>();
    private final Map<String, String> pregReplaceAr2EnMap = new HashMap<>();
    private final Map<String, String> strReplaceAr2EnMap = new HashMap<>();
    private final Map<String, String> meaningsMap = new HashMap<>();
    private final Map<String, String> derivationsMap = new HashMap<>();
    private final Map<String, String> namesMap = new HashMap<>();
    private final Map<String, String> originsMap = new HashMap<>();

    public XmlManager() {
    }

    /**
     * Parses translation XML.
     *
     * @param inputStream the input stream of the XML file
     * @throws XmlParseException if parsing fails
     */
    public void parseTranslation(InputStream inputStream) throws XmlParseException {
        try {
            Document doc = parseXml(inputStream);
            NodeList groups = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < groups.getLength(); i++) {
                Node group = groups.item(i);
                if (group.getNodeType() == Node.ELEMENT_NODE) {
                    Element groupElement = (Element) group;
                    String nodeName = groupElement.getNodeName();
                    String function = groupElement.getAttribute(FUNCTION_ATTR);
                    if (PREG_REPLACE.equalsIgnoreCase(nodeName)) {
                        fillMap(EN2AR.equalsIgnoreCase(function) ? pregReplaceEn2ArMap : pregReplaceAr2EnMap, groupElement.getChildNodes());
                    } else if (STR_REPLACE.equalsIgnoreCase(nodeName)) {
                        fillMap(EN2AR.equalsIgnoreCase(function) ? strReplaceEn2ArMap : strReplaceAr2EnMap, groupElement.getChildNodes());
                    }
                }
            }
        } catch (Exception e) {
            throw new XmlParseException("Failed to parse translation XML", e);
        }
    }

    /**
     * Parses meaning XML.
     *
     * @param inputStream the input stream of the XML file
     * @throws XmlParseException if parsing fails
     */
    public void parseMeanings(InputStream inputStream) throws XmlParseException {
        parseXmlWithFunction(inputStream, meaningsMap, "Failed to parse meaning XML");
    }

    /**
     * Parses derivations XML.
     *
     * @param inputStream the input stream of the XML file
     * @throws XmlParseException if parsing fails
     */
    public void parseDerivations(InputStream inputStream) throws XmlParseException {
        parseXmlWithFunction(inputStream, derivationsMap, "Failed to parse derivations XML");
    }

    /**
     * Parses origins XML.
     *
     * @param inputStream the input stream of the XML file
     * @throws XmlParseException if parsing fails
     */
    public void parseOrigins(InputStream inputStream) throws XmlParseException {
        parseXmlWithFunction(inputStream, originsMap, "Failed to parse origins XML");
    }

    /**
     * Parses names XML.
     *
     * @param inputStream the input stream of the XML file
     * @throws XmlParseException if parsing fails
     */
    public void parseNames(InputStream inputStream) throws XmlParseException {
        try {
            Document doc = parseXml(inputStream);
            NodeList nameNodes = doc.getElementsByTagName("name");
            for (int i = 0; i < nameNodes.getLength(); i++) {
                Element nameElement = (Element) nameNodes.item(i);
                String gender = nameElement.getAttribute("gender");
                String name = nameElement.getTextContent();
                namesMap.put(name, gender);
            }
        } catch (Exception e) {
            throw new XmlParseException("Failed to parse names XML", e);
        }
    }

    /**
     * Gets translation for a specific word.
     *
     * @param word the word to be translated
     * @return the translated word, or null if the input is empty
     */
    public String getTranslation(String word) {
        if (word == null || word.isEmpty()) {
            return null;
        }

        String firstSideTranslation = getPrefix(word, pregReplaceEn2ArMap);
        int firstIndex = firstSideTranslation.isEmpty() ? 0 : (word.length() >= 2 && pregReplaceEn2ArMap.containsKey(word.substring(0, 2))) ? 2 : 1;

        String remainingWord = word.substring(firstIndex);
        String lastSideTranslation = getSuffix(remainingWord, pregReplaceEn2ArMap);
        int lastIndex = lastSideTranslation.isEmpty() ? 0 : (remainingWord.length() >= 4 && pregReplaceEn2ArMap.containsKey(remainingWord.substring(remainingWord.length() - 4))) ? 4 : (remainingWord.length() >= 2 && pregReplaceEn2ArMap.containsKey(remainingWord.substring(remainingWord.length() - 2))) ? 2 : 1;

        String middleWord = remainingWord.substring(0, remainingWord.length() - lastIndex);
        String middleTranslation = translateMiddle(middleWord, strReplaceEn2ArMap);

        return firstSideTranslation + middleTranslation + lastSideTranslation;
    }

    private Document parseXml(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }

    private void parseXmlWithFunction(InputStream inputStream, Map<String, String> map, String errorMessage) throws XmlParseException {
        try {
            Document doc = parseXml(inputStream);
            NodeList groups = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < groups.getLength(); i++) {
                Node group = groups.item(i);
                if (group.getNodeType() == Node.ELEMENT_NODE) {
                    Element groupElement = (Element) group;
                    if (CONSTRUCT.equalsIgnoreCase(groupElement.getAttribute(FUNCTION_ATTR))) {
                        fillMap(map, groupElement.getChildNodes());
                    }
                }
            }
        } catch (Exception e) {
            throw new XmlParseException(errorMessage, e);
        }
    }

    private String getPrefix(String word, Map<String, String> map) {
        if (word.length() >= 2) {
            String sub = word.substring(0, 2);
            for (String key : map.keySet()) {
                if (Pattern.matches(key.replace("/", ""), sub)) {
                    return map.get(key);
                }
            }
        }
        if (word.length() >= 1) {
            String sub = word.substring(0, 1);
            for (String key : map.keySet()) {
                if (Pattern.matches(key.replace("/", ""), sub)) {
                    return map.get(key);
                }
            }
        }
        return "";
    }

    private String getSuffix(String word, Map<String, String> map) {
        if (word.length() >= 4) {
            String sub = word.substring(word.length() - 4);
            for (String key : map.keySet()) {
                if (Pattern.matches(key.replace("$", ""), sub)) {
                    return map.get(key);
                }
            }
        }
        if (word.length() >= 2) {
            String sub = word.substring(word.length() - 2);
            for (String key : map.keySet()) {
                if (Pattern.matches(key.replace("$", ""), sub)) {
                    return map.get(key);
                }
            }
        }
        if (word.length() >= 1) {
            String sub = word.substring(word.length() - 1);
            for (String key : map.keySet()) {
                if (Pattern.matches(key.replace("$", ""), sub)) {
                    return map.get(key);
                }
            }
        }
        return "";
    }

    private String translateMiddle(String word, Map<String, String> map) {
        StringBuilder translated = new StringBuilder();
        int i = 0;
        while (i < word.length()) {
            boolean matched = false;
            for (int len = 4; len >= 1 && !matched; len--) {
                if (i + len <= word.length()) {
                    String sub = word.substring(i, i + len);
                    if (map.containsKey(sub)) {
                        translated.append(map.get(sub));
                        i += len;
                        matched = true;
                    }
                }
            }
            if (!matched) {
                translated.append(word.charAt(i));
                i++;
            }
        }
        return translated.toString();
    }

    public void fillMap(Map<String, String> map, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String search = getChildText(element, SEARCH);
                String replace = getChildText(element, REPLACE);
                if (search != null && replace != null) {
                    map.put(search, replace);
                }
            }
        }
    }

    private String getChildText(Element parent, String childName) {
        NodeList children = parent.getElementsByTagName(childName);
        if (children.getLength() > 0) {
            return children.item(0).getTextContent();
        }
        return null;
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

    public Map<String, String> getOriginsMap() {
        return originsMap;
    }
}

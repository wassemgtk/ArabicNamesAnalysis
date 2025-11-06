package com.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnalysisManager {

    private final XmlManager xmlManager;

    public AnalysisManager() {
        this.xmlManager = new XmlManager();
        loadAllXmlData();
    }

    private void loadAllXmlData() {
        try {
            loadResource("/names.xml", xmlManager::parseNames);
            loadResource("/Transliteration.xml", xmlManager::parseTranslation);
            loadResource("/ArQuery.xml", xmlManager::parseDerivations);
            loadResource("/name.xml", xmlManager::parseMeanings);
            loadResource("/origins.xml", xmlManager::parseOrigins);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void loadResource(String resourceName, XmlLoader loader) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                loader.load(inputStream);
            }
        } catch (IOException | XmlParseException e) {
            throw new RuntimeException("Failed to load resource: " + resourceName, e);
        }
    }

    @FunctionalInterface
    interface XmlLoader {
        void load(InputStream inputStream) throws XmlParseException;
    }

    /**
     * return if the name is belong to male or female
     *
     * @param name the name to check
     * @return true if the name is female, false otherwise
     */
    boolean isFemale(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        String gender = xmlManager.getNamesMap().get(name);
        if (gender != null) {
            return "female".equalsIgnoreCase(gender);
        }

        char last = name.charAt(name.length() - 1);
        char beforeLast = name.length() > 1 ? name.charAt(name.length() - 2) : ' ';

        return last == 'ة' || last == 'ه' || last == 'ى' || last == 'ا'
                || (last == 'ء' && beforeLast == 'ا');
    }

    /**
     * getting the opposite language of the name
     *
     * @param name the name to translate
     * @return the translated name
     * @throws AnalysisException if there is an error during processing
     */
    public String languageOpposition(String name) throws AnalysisException {
        return xmlManager.getTranslation(name);
    }

    /**
     * processing the name derivations
     *
     * @param name the name to process
     * @return a string of derivations
     * @throws AnalysisException if there is an error during processing
     */
    public String processDerivations(String name) throws AnalysisException {
        return xmlManager.getDerivationsMap().keySet().stream()
                .map(key -> {
                    Pattern pattern = Pattern.compile(key.replaceAll("/", ""));
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.matches()) {
                        return name + " " + matcher.replaceFirst(xmlManager.getDerivationsMap().get(key));
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.joining(" , "));
    }

    /**
     * get the name meaning
     *
     * @param name the name to get the meaning of
     * @return the meaning of the name
     * @throws AnalysisException if there is an error during processing
     */
    public String processMeaning(String name) throws AnalysisException {
        return xmlManager.getMeaningsMap().get(name);
    }

    /**
     * get the name origin
     *
     * @param name the name to get the origin of
     * @return the origin of the name
     * @throws AnalysisException if there is an error during processing
     */
    public String processOrigin(String name) throws AnalysisException {
        return xmlManager.getOriginsMap().get(name);
    }
}

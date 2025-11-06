package com.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysisManager {

    private XmlManager xmlManager;

    public AnalysisManager() {
        this.xmlManager = new XmlManager();
        try {
            InputStream namesInputStream = getClass().getResourceAsStream(
                    "/names.xml");
            if (namesInputStream != null) {
                xmlManager.parseNames(namesInputStream);
            }
        } catch (XmlParseException e) {
            // Log the error or handle it gracefully
            e.printStackTrace();
        }
    }

    /**
     * return if the name is belong to male or female
     *
     * @param name
     * @return
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

        if (last == 'ة' || last == 'ه' || last == 'ى' || last == 'ا'
                || (last == 'ء' && beforeLast == 'ا')) {
            return true;
        }
        return false;
    }

    /**
     * getting the opposite language of the name
     *
     * @param name
     * @return
     * @throws AnalysisException
     */
    public String languageOpposition(String name) throws AnalysisException {
        try {
            InputStream translationInputStream = getClass().getResourceAsStream(
                    "/Transliteration.xml");
            if (translationInputStream == null) {
                throw new AnalysisException("Transliteration.xml not found", null);
            }
            xmlManager.parseTranslation(translationInputStream);
            return xmlManager.getTranslation(name);
        } catch (XmlParseException | IOException e) {
            throw new AnalysisException("Failed to process language opposition", e);
        }
    }

    /**
     * processing the name derivations
     *
     * @param name
     * @return
     * @throws AnalysisException
     */
    public String processDerivations(String name) throws AnalysisException {
        try {
            InputStream derivationsInputStream = getClass().getResourceAsStream(
                    "/ArQuery.xml");
            if (derivationsInputStream == null) {
                throw new AnalysisException("ArQuery.xml not found", null);
            }
            xmlManager.parseDerivations(derivationsInputStream);
            StringBuffer derivations = new StringBuffer();
            for (String key : xmlManager.getDerivationsMap().keySet()) {
                Pattern pattern = Pattern.compile(key.replaceAll("/", ""));
                Matcher matcher = pattern.matcher(name);
                if (matcher.matches()) {
                    derivations.append(name + " " + matcher.replaceFirst(xmlManager.getDerivationsMap().get(key))).append(" , ");
                }
            }
            return derivations.toString();
        } catch (XmlParseException | IOException e) {
            throw new AnalysisException("Failed to process derivations", e);
        }
    }

    /**
     * get the name meaning
     *
     * @param name
     * @return
     * @throws AnalysisException
     */
    public String processMeaning(String name) throws AnalysisException {
        try {
            InputStream meaningInputStream = getClass().getResourceAsStream(
                    "/name.xml");
            if (meaningInputStream == null) {
                throw new AnalysisException("name.xml not found", null);
            }
            xmlManager.parseMeanings(meaningInputStream);
            return xmlManager.getMeaningsMap().get(name);
        } catch (XmlParseException | IOException e) {
            throw new AnalysisException("Failed to process meaning", e);
        }
    }
}

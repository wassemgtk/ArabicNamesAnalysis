package com.analysis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysisManager {

	/**
	 * return if the name is belong to male or female
	 * 
	 * @param name
	 * @return
	 */
	boolean isFemale(String name) {
		boolean isFemale = false;
		char last = name.substring(name.length() - 1,
				name.length()).charAt(0);
		char beforeLast = name.substring(name.length() - 2,
				name.length() - 1).charAt(0);

		if (last == 'ة' || last == 'ه' || last == 'ى' || last == 'ا'
				|| (last == 'ء' && beforeLast == 'ا')) {

			isFemale = true;
		}
		return isFemale;
	}

	/**
	 * getting the opposite language of the name
	 * 
	 * @param name
	 * @return
	 * @throws IOException 
	 */
	public String languageOpposition(String name) throws IOException {
		File file = new File(
				"Transliteration.xml");
		// Convert file to byte of array
		byte[] bytesOfArray =XmlManager.getBytesFromFile(file);
		ByteArrayInputStream translationInputStream = new ByteArrayInputStream(
				bytesOfArray);
		// parse the ArQuery xml file 
		XmlManager.parseTranslation(translationInputStream);
		String translation = XmlManager.getTranslation(name);
		return translation;
		
	}

	/**
	 * processing the name derivations
	 * 
	 * @param name
	 * @return
	 * @throws IOException 
	 */
	public String processDerivations(String name) throws IOException {
		File file = new File(
				"ArQuery.xml");
		// Convert file to byte of array
		byte[] bytesOfArray =XmlManager.getBytesFromFile(file);
		ByteArrayInputStream derivationsInputStream = new ByteArrayInputStream(
				bytesOfArray);
		// parse the ArQuery xml file 
		XmlManager.parseDerivations(derivationsInputStream);
		// get the name derivations
		StringBuffer derivations = new StringBuffer();
		for(String key:XmlManager.getDerivationsMap().keySet()){
			Pattern pattern = Pattern.compile(key.replaceAll("/", ""));
			Matcher matcher = pattern.matcher(name);
			if(matcher.matches()){
				derivations.append(name + " " + matcher.replaceFirst(XmlManager.getDerivationsMap().get(key))).append(" , ");
			}
		}
		
		return derivations.toString();
	}

	/**
	 * get the name meaning
	 * 
	 * @param name
	 * @return
	 * @throws IOException 
	 */
	public String processMeaning(String name) throws IOException {
		
		File file = new File(
				"name.xml");
		// Convert file to byte of array
		byte[] bytesOfArray =XmlManager.getBytesFromFile(file);
		ByteArrayInputStream meaningInputStream = new ByteArrayInputStream(
				bytesOfArray);
		// parse the name xml file 
		XmlManager.parseMeanings(meaningInputStream);
		// get the name meaning 
		String meaning = XmlManager.getMeaningsMap().get(name);

		return meaning;
	}
}

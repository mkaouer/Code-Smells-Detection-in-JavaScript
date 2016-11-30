/*
 * Copyright 1999-2009 Carnegie Mellon University.  
 * Copyright 2009 PC-NG Inc.  
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.linguist.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import edu.cmu.sphinx.linguist.acoustic.Context;
import edu.cmu.sphinx.linguist.acoustic.Unit;
import edu.cmu.sphinx.util.props.ConfigurationManagerUtils;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4String;

/**
 * Maps the phones from one phoneset to another to use dictionary from the one
 * acoustic mode with another one. The mapping file is specified with a mapList
 * property. The contents should look like
 * 
 * <pre>
 * AX AH
 * IX IH
 * </pre>
 */
public class MappingDictionary extends FastDictionary implements Dictionary {

	@S4String(mandatory = true, defaultValue = "")
	public static final String PROP_MAP_FILE = "mapFile";

	private URL mappingFile;
	private Map<String,String> mapping = new HashMap<String,String>();
				
	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		super.newProperties(ps);
	   
		mappingFile = ConfigurationManagerUtils.getResource(PROP_MAP_FILE, ps);
	}

   /*
    * (non-Javadoc)
    *
    * @see edu.cmu.sphinx.linguist.dictionary.Dictionary#allocate()
    */
	@Override
    public void allocate() throws IOException {
    		super.allocate();
    		if (mappingFile.getFile() != "") 
    				loadMapping (mappingFile.openStream());
    }
	/**
	 * Gets a context independent unit. There should only be one instance of any
	 * CI unit
	 * 
	 * @param name
	 *            the name of the unit
	 * @param isFiller
	 *            if true, the unit is a filler unit
	 * @return the unit
	 */
	protected Unit getCIUnit(String name, boolean isFiller) {
		if (mapping.containsKey(name)) {
			name = mapping.get(name);
		}
		return unitManager.getUnit(name, isFiller, Context.EMPTY_CONTEXT);
	}

	protected void loadMapping(InputStream inputStream) throws IOException {
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            if (st.countTokens() != 2) {
            	throw new IOException ("Wrong file format");
            }
			mapping.put(st.nextToken(), st.nextToken());
		}
		br.close();
		isr.close();
		inputStream.close();
	}
}

package com.robertolosanno.cdt_maven;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class ReadRDF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFileName="./cdt_rauseo.owl";
		
		// create an empty model
//		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		//InputStream in = FileManager.get().open( inputFileName );
		Model model= FileManager.get().loadModel(inputFileName);
//		if (in == null){
//			throw new IllegalArgumentException("File: " + inputFileName + " not found");
//		}
	//
//		// read the RDF/XML file
//		model.read(in, null);
//		// write it to standard out
//		model.write(System.out);
		
		String NS = "http://example.org/data/test#";
		Resource title2 = model.getResource( NS + "title2" );
		Property contains = model.getProperty( NS + "contains" );
		System.out.println( "title2.contains = " + title2.getProperty( contains ).getObject() );
	}

}


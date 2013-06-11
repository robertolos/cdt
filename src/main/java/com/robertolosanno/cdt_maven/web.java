package com.robertolosanno.cdt_maven;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.XSD;

public class web {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    String ns = "http://www.mxro.de/eis#&#8221";
	        OntModel m = ModelFactory.createOntologyModel(
	        // create without inferecing, OWL Full – With inferecing would be OWL_MEM_MICRO_RULE_INF
	        OntModelSpec.OWL_MEM );
	        m.setNsPrefix("eis", ns);
	        // m.read(“”) public ontology to be used in the model

	        OntClass webResource = m.createClass(ns+"webResource");

	        DatatypeProperty hasAddress = m.createDatatypeProperty(ns + "hasAddress");

	        hasAddress.addDomain(webResource);
	        hasAddress.addRange(XSD.anyURI);

	        ObjectProperty relatesTo = m.createObjectProperty(ns + "relatesTo");

	        relatesTo.addDomain(webResource);
	        relatesTo.addRange(webResource);

	        Individual resGoogleDoc = m.createIndividual(ns+"googleDoc1", webResource);
	        resGoogleDoc.setPropertyValue(hasAddress, m.createLiteral("http://docs.google.com/&#8221"));

	        Individual resWikiPage = m.createIndividual(ns+"wikiPage1", webResource);
	        resWikiPage.setPropertyValue(hasAddress, m.createLiteral("http://en.wikipeida.org/&#8221"));

	        resWikiPage.setPropertyValue(relatesTo, resGoogleDoc);

	        m.write(System.out);
	        
	}

}

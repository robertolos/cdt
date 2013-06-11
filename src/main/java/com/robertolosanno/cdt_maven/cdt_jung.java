package com.robertolosanno.cdt_maven;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class cdt_jung extends JFrame {

	private static final long serialVersionUID = -2707712944901661771L;
	protected Tree<String> tree = new Tree<String>("root#root");

	protected static int vertexName = 0;
	Hashtable<String, Object> vertexlist = new Hashtable<String, Object>();
	
	Graph<Integer, String> g;

	public static final String SOURCE_URL = "http://www.semanticweb.org/roberto/ontologies/2013/5/cdt_2";

	protected static final String SOURCE_FILE = "cdt_2b.owl";

	public static final String NS = SOURCE_URL + "#"; // the namespace of the ontology

	public static Object parent;

	
	public cdt_jung() {
		super("Context dimension tree");

		// Configurazione GRAFO

		

		try {
			OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			loadModel(m);
			ExtendedIterator<OntClass> iter = m.listHierarchyRootClasses();
	
			// Graph<V, E> where V is the type of the vertices and E is the type of the edges
	        g = new SparseMultigraph<Integer, String>();
	        // Add some vertices. From above we defined these to be type Integer.
	        g.addVertex((Integer)1);
	        g.addVertex((Integer)2);
	        g.addVertex((Integer)3); 
	        // Note that the default is for undirected edges, our Edges are Strings.
	        g.addEdge("Edge-A", 1, 2); // Note that Java 1.5 auto-boxes primitives
	        g.addEdge("Edge-B", 2, 3);  


			while (iter.hasNext()) {
				OntClass rootClass = iter.next();
				System.out.println("Root Class: " + rootClass.toString());
				getSubClasses(rootClass, "root");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			graph.getModel().endUpdate();
		}

		System.out.println(tree.toString());
	}

	protected void getSubClasses(OntClass rootClass, String padre) {
		String type = "";
		if (padre == "root") {
			type = "root";
		} else {
			type = "dim";
		}
		// tree.addLeaf(padre+"#"+type, rootClass.toString()+"#dim" );
		plot(padre, type, rootClass.toString(), "dim");
		for (Iterator<OntClass> i = rootClass.listSubClasses(); i.hasNext();) {
			OntClass c = i.next();
			System.out.println(" -> " + c.getURI());
//			getSubClasses(c, c.getURI());
		}
	}

	protected void plot(String padre, String padre_type, String node, String node_type) {
		// Creo un nuovo vertice di tipo Object e lo aggiungo alla hash map
		// usando come chiave un incrementale intero.
		// Uso il vertice padre (object) come parent in insertVertex
		tree.addLeaf(padre + "#" + padre_type, node.toString() + "#" + node_type);
		String[] elem_splitted = node.split("#");
		String stile;
		switch (node_type) {
		case "dim":
			stile = "ROUNDED;strokeColor=black;fillColor=black";
			break;
		case "val":
			stile = "ROUNDED;strokeColor=black;fillColor=white";
			break;
		case "par":
			stile = "ROUNDED;strokeColor=black;fillColor=white";
			break;
		case "root":
			stile = "DOUBLE_ELLIPSE;strokeColor=black;fillColor=white";
			break;
		default:
			stile = "ROUNDED;strokeColor=black;fillColor=white";
			break;
		}
//		Object padre_obj=vertexlist.get(padre);
//		Object v=graph.insertVertex(padre_obj, null, elem_splitted[1], 0, 0, 40, 40, stile);
//		vertexlist.put(node, v);
//		 graph.insertEdge(parent, null, "", padre_obj, v);
	}

	protected void loadModel(OntModel m) {
		FileManager.get().getLocationMapper().addAltEntry(SOURCE_URL, SOURCE_FILE);
		Model baseOntology = FileManager.get().loadModel(SOURCE_URL);
		m.addSubModel(baseOntology);
		m.setNsPrefix("st", NS);
	}

	public static void main(String[] args) {
//		context_dimension_tree_jena frame = new context_dimension_tree_jena();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(1000, 320);
//		frame.setVisible(true);

		context_dimension_tree_jena cdt = new context_dimension_tree_jena();
		// The Layout<V, E> is parameterized by the vertex and edge types
//		Layout<Integer, String> layout = new CircleLayout(cdt.g);
//        layout.setSize(new Dimension(300,300)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
//        BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
//        vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
        
        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(vv); 
        frame.pack();
        frame.setVisible(true);       
	}

}

package com.robertolosanno.cdt_maven;

import java.awt.BorderLayout;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

import org.w3c.dom.Node;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxLayoutManager;
import com.mxgraph.view.mxStylesheet;

public class context_dimension_tree_jena extends JFrame {

	private static final long serialVersionUID = -2707712944901661771L;
	protected Tree<String> tree = new Tree<String>("root#root");

	protected static int vertexName = 0;
	Hashtable<String, Object> vertexlist = new Hashtable<String, Object>();
	
	public static final String SOURCE_URL = "http://www.semanticweb.org/roberto/ontologies/2013/5/cdt_2";

	protected static final String SOURCE_FILE = "cdt_2b.owl";

	public static final String NS = SOURCE_URL + "#"; // the namespace of the ontology

	public static mxGraph graph;
	public static Object parent;

	public context_dimension_tree_jena() {
		super("Context dimension tree");

		// Configurazione GRAFO
		graph = new mxGraph();
		parent = graph.getDefaultParent();

		// IMPOSTO LO STILE DI DEFAULT
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		style.put(mxConstants.STYLE_OPACITY, 50);
		style.put(mxConstants.STYLE_FONTCOLOR, "red");
		stylesheet.putCellStyle("ROUNDED", style);

		Hashtable<String, Object> style_root = new Hashtable<String, Object>();
		style_root.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_DOUBLE_ELLIPSE);
		stylesheet.putCellStyle("DOUBLE_ELLIPSE", style_root);
		// FINE STILE

		try {
			OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			loadModel(m);
			ExtendedIterator<OntClass> iter = m.listHierarchyRootClasses();
			
//			mxPartitionLayout layout = new mxPartitionLayout(graph, true, 10, 20);
//			layout.execute(graph.getDefaultParent());
			//Configurazione layout grafo
			new mxLayoutManager(graph) {

				mxCompactTreeLayout layout = new mxCompactTreeLayout(graph);
				public mxIGraphLayout getLayout(Object parent) {
					if (graph.getModel().getChildCount(parent) > 0) { 
						return layout; 
					} 
					return null; 
				} 
			};
//			

//			new mxLayoutManager(graph) { 
//				mxCompactTreeLayout layout = new mxCompactTreeLayout(graph);
//				public mxIGraphLayout getLayout(Object parent) {
//					layout.setNodeDistance(20);
//					if (graph.getModel().getChildCount(parent) > 0) { 
//						return layout; 
//					} 
//					return null; 
//				} 
//			};
			
//			mxCompactTreeLayout.prototype.verticalLayout = function(node, parent, x0, y0, bounds) {
//			    node.x += x0 + node.offsetY;
//			    node.y += y0 + node.offsetX;
//			    bounds = this.apply(node, bounds);
//			    var child = node.child;
//			    if (child != null) {
//			        bounds = this.verticalLayout(child, node, node.x, node.y, bounds);
//			        var siblingOffset = node.x + child.offsetY;
//			        var s = child.next;
//			        while (s != null) {
//			            bounds = this.verticalLayout(s, node, siblingOffset, node.y + child.offsetX, bounds);
//			            siblingOffset += s.offsetY;
//			            s = s.next;
//			        }
//			    }
//			    return bounds;
//			};
			
			
			graph.getModel().beginUpdate();
			
			//Aggiunta nodo root
			Object v=graph.insertVertex(parent, null, "root", 0, 0, 40, 40, "DOUBLE_ELLIPSE;strokeColor=black;fillColor=white");
			vertexlist.put("root", v);
			
			while (iter.hasNext()) {
				OntClass rootClass = iter.next();
				System.out.println("Root Class: " + rootClass.toString());
				getSubClasses(rootClass, "root");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);

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
		Object padre_obj=vertexlist.get(padre);
		Object v=graph.insertVertex(padre_obj, null, elem_splitted[1], 0, 0, 40, 40, stile);
		vertexlist.put(node, v);
		// graph.insertVertex(parent, null, elem_splitted[1], i*120, 20, 40, 40, stile);
		 graph.insertEdge(parent, null, "", padre_obj, v);
	}

	protected void loadModel(OntModel m) {
		FileManager.get().getLocationMapper().addAltEntry(SOURCE_URL, SOURCE_FILE);
		Model baseOntology = FileManager.get().loadModel(SOURCE_URL);
		m.addSubModel(baseOntology);
		m.setNsPrefix("st", NS);
	}

	public static void main(String[] args) {
		cdt_jung frame = new cdt_jung();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 320);
		frame.setVisible(true);
	}

}
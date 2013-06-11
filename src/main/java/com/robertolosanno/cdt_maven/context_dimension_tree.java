package com.robertolosanno.cdt_maven;


import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mxgraph.swing.mxGraphComponent;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;


public class context_dimension_tree extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;
	protected Tree<String> tree = new Tree<String>("root#root");
	

	public context_dimension_tree() {
		super("Context dimension tree");
		try {
//			tree.addLeaf("root");
			// ************* Lettura XML ****************
			
			File fXmlFile = new File("cdt_rauseo.owl");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			// **************** LETTURA CLASSI ****************
			NodeList nList = doc.getElementsByTagName("owl:Class");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				// System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String class_name = eElement.getAttribute("rdf:about");
					String[] class_splitted = class_name.split("#");
					System.out.println("Class: " + class_splitted[1]);

					// Verifica se la classe (dimension) è figlia di un valore
					Element padre = (Element) eElement.getElementsByTagName("rdfs:isDefinedBy").item(0);
					if (padre != null) {
						//Il padre è un VALUE
						String padre_name = padre.getAttribute("rdf:resource");
						String[] padre_splitted = padre_name.split("#");
						System.out.println(" --> Padre: " + padre_splitted[1]);
						
						tree.addLeaf(padre_splitted[1]+"#val", class_splitted[1]+"#dim");
					}else{
						tree.addLeaf("root#root", class_splitted[1]+"#dim" );
					}
					// System.out.println("rdf:type : " + eElement.getAttribute("rdf:type"));
					// System.out.println("Last Name : " + eElement.getElementsByTagName("owl:Class").item(0).getTextContent());
				}
			}
			// **************** LETTURA INDIVIDUALS ****************
			nList = doc.getElementsByTagName("owl:NamedIndividual");
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				// System.out.println("\nCurrent Element :" +
				// nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					String class_name = eElement.getAttribute("rdf:about");
					String[] class_splitted = class_name.split("#");
					System.out.println("Individual: " + class_splitted[1]);

					Element padre = (Element) eElement.getElementsByTagName(
							"rdf:type").item(0);
					String padre_name = padre.getAttribute("rdf:resource");
					String[] padre_splitted = padre_name.split("#");
					System.out.println("Padre: " + padre_splitted[1]);

					tree.addLeaf(padre_splitted[1]+"#dim", class_splitted[1]+"#val" );
					
					// System.out.println("rdf:type : " +
					// eElement.getAttribute("rdf:type"));
					// System.out.println("Last Name : " +
					// eElement.getElementsByTagName("owl:Class").item(0).getTextContent());
					// System.out.println("Nick Name : " +
					// eElement.getElementsByTagName("nickname").item(0).getTextContent());
					// System.out.println("Salary : " +
					// eElement.getElementsByTagName("salary").item(0).getTextContent());

				}
			}

			// FINE lettura
		} catch (Exception e) {
			e.printStackTrace();
		}

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

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
		
		
		
		graph.getModel().beginUpdate();
		try {
			
			String head=tree.getHead();
			System.out.println("HEAD: "+head);
			Collection<String> successori=tree.getSuccessors(head);
			Iterator<String> iterator = successori.iterator();
			System.out.println(tree.toString());
			int i=1;
			while(iterator.hasNext()){
				String it = iterator.next();
				String[] elem_splitted = it.split("#");
				System.out.println("elem: " + elem_splitted[0]);
				String stile;
				switch (elem_splitted[1]) {
				case "dim":
					stile="ROUNDED;strokeColor=black;fillColor=black";
					break;
				case "val":
					stile="ROUNDED;strokeColor=black;fillColor=white";
					break;
				case "par":
					stile="ROUNDED;strokeColor=black;fillColor=white";
					break;
				case "root":
					stile="DOUBLE_ELLIPSE;strokeColor=black;fillColor=white";
					break;
				default:
					stile="ROUNDED;strokeColor=black;fillColor=white";
					break;
				}
				
//				Object bla= graph.insertVertex(parent, null, elem_splitted[0], i*120, 20, 40, 40, stile);
				graph.insertVertex(parent, null, elem_splitted[0], i*120, 20, 40, 40, stile);
				i++;
			}
			
			
//			Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 40, 40, "ROUNDED;strokeColor=black;fillColor=black");
//			Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 40, 40, "ROUNDED;strokeColor=black;fillColor=white");
//			Object v3 = graph.insertVertex(parent, null, "Hello", 240, 220, 40, 40, "STYLE_SHAPE=SHAPE_RECTANGLE");
//			graph.insertEdge(parent, null, "Edge", v1, v2);
//			graph.insertEdge(parent, null, "Parameter", v3, v2);
		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		
//		Tree<String> albero=tree.getTree("root");
		System.out.println(tree.toString());
		
		
		
		
//		Collection<Tree<String>> alberi=tree.getSubTrees();
//		Iterator<Tree<String>> iterator = alberi.iterator();
//		while(iterator.hasNext()){
//			Tree<String> it = iterator.next();
//			
//		}
	}

	public static void main(String[] args) {
		context_dimension_tree frame = new context_dimension_tree();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 320);
		frame.setVisible(true);
	}

}

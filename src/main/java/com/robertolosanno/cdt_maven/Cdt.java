package com.robertolosanno.cdt_maven;
/*
 * 2013, Roberto Losanno 885502 Univerità degli studi di Napoli Federico II.
 * 
 * This software is open-source under the BSD license;
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;


@SuppressWarnings("serial")
public class Cdt extends JApplet {

	/**
	 * Ontologia
	 */
	
	public static final String SOURCE_URL = "http://www.semanticweb.org/roberto/ontologies/2013/5/cdt_2";
	protected static final String SOURCE_FILE = "museum.owl";
	public static final String NS = SOURCE_URL + "#"; // the namespace of the ontology
	
    /**
     * the graph
     */
    Forest<String,String> graph;
	Factory<String> edgeFactory = new Factory<String>() {
		int i=0;
		public String create() {
			Integer z=i++;
			return z.toString();
		}};
    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String,String> vv;
    VisualizationServer.Paintable rings;
    String root;
    TreeLayout<String,String> treeLayout;
    RadialTreeLayout<String,String> radialLayout;

    public Cdt() {
    	//Ontologia
    	OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		loadModel(m);
		ExtendedIterator<OntClass> iter = m.listHierarchyRootClasses();
		
        // create a simple graph for the demo
        graph = new DelegateForest<String,String>();

        createTree(iter);
        
        treeLayout = new TreeLayout<String,String>(graph);
        radialLayout = new RadialTreeLayout<String,String>(graph);
        radialLayout.setSize(new Dimension(1200,600));
        vv =  new VisualizationViewer<String,String>(treeLayout, new Dimension(1200,600));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
//        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        // add a listener for ToolTips
//        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        
        rings = new Rings();
        

//        	    Transformer<String, String> transformer = new Transformer<String, String>() {
//        	        @Override public String transform(String arg0) { return arg0; }
//        	      };
//        	      vv.getRenderContext().setEdgeLabelTransformer(transformer);
        	      
        	      
        	        
        
		//***************** MODIFICA COLORE VERTICE ************************
        Transformer<String,Paint> vertexColor = new Transformer<String,Paint>() {
            public Paint transform(String s) {
            	String[] ss = s.split("#");
            	String type= ss[1];
                if(type.equals("root")) return Color.lightGray;
                if(type.equals("dim")) return Color.BLACK;
                if(type.equals("val")) return Color.WHITE;
                if(type.equals("par_val")) return Color.WHITE;
                if(type.equals("par_dim")) return Color.WHITE;
                
                return Color.GREEN;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        
        //**************** MODIFICA FORMA VERTICE ***************************
        
        //Dati per creare un triangolo (lo creaiamo attraverso la classe Polygon)
        final int[]xShape = new int[4];
        final int[]yShape = new int[4];
        final int nShape;  // count of points
        // Make a shape
        xShape[0]=-10; xShape[1]=0; xShape[2]=10;
        yShape[0]=0; yShape[1]=20; yShape[2]=0;
        nShape = 3;
        
        Transformer<String, Shape> vertexShape = new Transformer<String, Shape>() {
        	        private final Shape[] styles = {
        	            new Rectangle(-10, -10, 20, 20),
        	            new Ellipse2D.Double(-10, -10, 20, 20),
        	            new Polygon(xShape, yShape, nShape) //Triangolo
        	            
        	            };
        	        
        	 
        	        @Override
        	        public Shape transform(String i) {
        	        	String[] type = i.split("#");
        	        	if(type[1].equals("par_val")){
        	        		return styles[0];
        	        	}else if(type[1].equals("par_dim")){
        	        		return styles[2];
        	        	}else{
        	        		return styles[1];
        	        	}
        	        }
        	    };  
        vv.getRenderContext().setVertexShapeTransformer(vertexShape);
	    
//        	        	    vv.getRenderer().setVertexRenderer(new MyRenderer());
        
        
        
      //**************** MODIFICA FONT LABEL ***************************
        vv.getRenderContext().setVertexFontTransformer(new Transformer<String, Font>(){

			@Override
			public Font transform(String arg0) {
				Font font = new Font("Arial Unicode MS", Font.PLAIN, 11);
				return font;
			}});
        

        
     

        
        
        // ********************** POSIZIONA LA LABEL SOTTO IL VERTICE ****************************
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);
        	    
        
	    //******************** RIMUOVE DAL TESTO DELLA LABEL DEL VERTICE IL TIPO DI VERTICE ************************
        
        Transformer<String, String> transformer = new Transformer<String, String>() {
          @Override public String transform(String arg0) {
        	  String[] node = arg0.split("#");
        	  return node[0]; 
        }
        };
        
        vv.getRenderContext().setVertexLabelTransformer(transformer);
        
          	        
          	      
        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
        
        JToggleButton radial = new JToggleButton("Radial");
        radial.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					LayoutTransition<String,String> lt =
						new LayoutTransition<String,String>(vv, treeLayout, radialLayout);
					Animator animator = new Animator(lt);
					animator.start();
					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
					vv.addPreRenderPaintable(rings);
				} else {
					LayoutTransition<String,String> lt =
						new LayoutTransition<String,String>(vv, radialLayout, treeLayout);
					Animator animator = new Animator(lt);
					animator.start();
					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
					vv.removePreRenderPaintable(rings);
				}
				vv.repaint();
			}});

        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(radial);
        controls.add(scaleGrid);
        controls.add(modeBox);

        content.add(controls, BorderLayout.SOUTH);
    }
//    static class MyRenderer implements Renderer.Vertex<String, String> {
//        @Override public void paintVertex(RenderContext<String, String> rc, Layout<String, String> layout, String vertex) {
//          GraphicsDecorator graphicsContext = rc.getGraphicsContext();
//          Point2D center = layout.transform(vertex);
//          String[] temp = vertex.split("#");
//          String type=temp[1];
//          Shape shape = null;
//          Color color = null;
//          System.out.println("t: "+type);
//          if(type.equals("root")) {
//            shape = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
//            color = new Color(255, 0, 0);
//          } else if(type.equals("dim")) {
//            shape = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
//            color = new Color(0, 0, 0);
//          } else if(type.equals("val")) {
//            shape = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
//            color = new Color(0, 255, 255);
//          }else if(type.equals("par")) {
//            shape = new Rectangle((int)center.getX()-10, (int)center.getY()-10, 20, 20);
//            color = new Color(0, 255, 255);
//          }
//          else{
//        	  color = new Color(0, 127, 0);
//        	  shape = new Rectangle((int)center.getX()-10, (int)center.getY()-10, 20, 20);
//          }
//          graphicsContext.setPaint(color);
//          graphicsContext.fill(shape);
//        }
//      }
    
    // CARICA L'ONTOLOGIA DAL FILE
    protected void loadModel(OntModel m) {
		FileManager.get().getLocationMapper().addAltEntry(SOURCE_URL, SOURCE_FILE);
		Model baseOntology = FileManager.get().loadModel(SOURCE_URL);
		m.addSubModel(baseOntology);
		m.setNsPrefix("st", NS);
	}
    
    
    // PER LA VISUALIZZAZIONE RADIALE
    class Rings implements VisualizationServer.Paintable {
    	
    	Collection<Double> depths;
    	
    	public Rings() {
    		depths = getDepths();
    	}
    	
    	private Collection<Double> getDepths() {
    		Set<Double> depths = new HashSet<Double>();
    		Map<String,PolarPoint> polarLocations = radialLayout.getPolarLocations();
    		for(String v : graph.getVertices()) {
    			PolarPoint pp = polarLocations.get(v);
    			depths.add(pp.getRadius());
    		}
    		return depths;
    	}

		public void paint(Graphics g) {
			g.setColor(Color.lightGray);
		
			Graphics2D g2d = (Graphics2D)g;
			Point2D center = radialLayout.getCenter();

			Ellipse2D ellipse = new Ellipse2D.Double();
			for(double d : depths) {
				ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d, 
						center.getX()+d, center.getY()+d);
				Shape shape = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
				g2d.draw(shape);
			}
		}

		public boolean useTransform() {
			return true;
		}
    }
    
   
    	    
    private void createTree(ExtendedIterator<OntClass> iter) {
    	graph.addVertex("root#root");
    	while (iter.hasNext()) {
			OntClass rootClass = iter.next();
//			System.out.println("Root Class: " + rootClass.toString());
			getSubClasses("root#root",rootClass);
			
			//Verifico se le dimensioni figle di root hanno parametri
			ExtendedIterator<? extends OntResource> inst= rootClass.listInstances();
			while (inst.hasNext()) {
				OntResource instance = inst.next();
				System.out.println("Dim: "+rootClass+"; INstance: " + instance.getURI());
				plot(rootClass.getURI(), "dim", instance.getURI(), "par_dim");
			} 
			
		} 
    }

    protected void getSubClasses( String padre,OntClass rootClass) {
		String type_padre = "";
		String type_node = "";
		
		String[] padre_splitted = padre.split("#");
		String radice = padre_splitted[0];
		if (radice.equals("root")) {
			plot(padre, "root", rootClass.toString(), "dim");
		}
		
		
		for (Iterator<OntClass> i = rootClass.listSubClasses(); i.hasNext();) {
			OntClass c = i.next();
			
			String comment_padre = rootClass.getComment(null);
			if(comment_padre!=null && comment_padre.equals("value")){
				type_padre="val";
			}else{
				type_padre="dim";
			}
			
			String comment_node = c.getComment(null);
			if(comment_node!=null && comment_node.equals("value")){
				type_node="val";
			}else{
				type_node="dim";
			}
			
			String papa=rootClass.getURI();
			String node=c.getURI();
			String nodebak=node;
			String typenodebak=type_node;
			ExtendedIterator<? extends OntResource> inst= c.listInstances();
			
			plot(papa, type_padre, node, type_node);
			
			//Verifica esistenza parametri figli (i parametri sono modellati in Protégé tramite Istanze)
			while (inst.hasNext()) {
				OntResource instance = inst.next();
//				System.out.println("Node: "+node+"; INstance: " + instance.getURI());
				papa=nodebak;
				type_padre=typenodebak;
				node=instance.getURI();
				if(type_padre.equals("dim")){
					type_node="par_dim";
				}else{
					type_node="par_val";
				}
				
				plot(papa, type_padre, node, type_node);
			} 
			
			getSubClasses(c.getURI(), c);
		}
	}
    protected void plot(String padre, String padre_type, String node, String node_type) {
		String[] node_splitted = node.split("#");
		String[] padre_splitted = padre.split("#");
		//graph.addVertex(node_splitted[1]);
		graph.addEdge(edgeFactory.create(), padre_splitted[1]+'#'+padre_type, node_splitted[1]+'#'+node_type);
	}

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("CDT Viewer");
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new Cdt());
        frame.pack();
        frame.setVisible(true);
    }
}

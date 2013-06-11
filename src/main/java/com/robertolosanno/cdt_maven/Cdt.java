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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
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
import org.apache.commons.collections15.functors.ChainedTransformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.util.Animator;


@SuppressWarnings("serial")
public class Cdt extends JApplet {

	/**
	 * Ontologia
	 */
	
	public static final String SOURCE_URL = "http://www.semanticweb.org/roberto/ontologies/2013/5/cdt_2";

	protected static final String SOURCE_FILE = "cdt_2c.owl";

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
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        rings = new Rings();

        
//        
//        Transformer<String, Paint> vertexPaint = 
//        	    new Transformer<String, Paint>() {
//        	        private final Color[] palette = { Color.BLACK, 
//        	            Color.WHITE, Color.RED };
//        	 
//        	        public Paint transform(String i) {
//        	        	if(i.equals("dim")){
//        	        		return palette[0];
//        	        	}
//        	        	if(i.equals("val")){
//        	        		return palette[1];
//        	        	}
//        	        	return palette[2];
//        	        }
//        	    };
        	    
//        	    vv.getRenderContext().setVertexLabelTransformer(transformer);
        	    
        	    
//        	    vv.getRenderContext().setVertexLabelTransformer(new MyRenderer());
        	    
        
        
        
        
//        		Transformer labelTransformer = new BasicVertexLabelRenderer<>()
//
//        		vv.setVertexLabelTransformer(labelTransformer);
        
        
        
        
        
//        	    Transformer<String, String> transformer = new Transformer<String, String>() {
//        	        @Override public String transform(String arg0) { return arg0; }
//        	      };
//        	      vv.getRenderContext().setEdgeLabelTransformer(transformer);
        	      
        	      
//        	        vv.getRenderer().setVertexRenderer(new MyRenderer());
        	        Transformer<String,Paint> vertexColor = new Transformer<String,Paint>() {
        	            public Paint transform(String s) {
        	            	String[] ss = s.split("#");
        	            	String type= ss[1];
        	                if(type.equals("root")) return Color.BLUE;
        	                if(type.equals("dim")) return Color.BLACK;
        	                if(type.equals("val")) return Color.WHITE;
        	                if(type.equals("par")) return Color.GRAY;
        	                
        	                return Color.GREEN;
        	            }
        	        };
        	        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        	        
        	        Transformer<String, String> transformer = new Transformer<String, String>() {
          	          @Override public String transform(String arg0) {
          	        	  String[] node = arg0.split("#");
          	        	  return node[0]; 
          	        }
          	        };
          	        vv.getRenderContext().setVertexLabelTransformer(transformer);
//          	        transformer = new Transformer<String, String>() {
//          	          @Override public String transform(String arg0) { return arg0; }
//          	        };
//          	        vv.getRenderContext().setEdgeLabelTransformer(transformer);
        	        
          	      
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
    static class MyRenderer implements Renderer.Vertex<String, String> {
        @Override public void paintVertex(RenderContext<String, String> rc, Layout<String, String> layout, String vertex) {
          GraphicsDecorator graphicsContext = rc.getGraphicsContext();
          Point2D center = layout.transform(vertex);
          Shape shape = null;
          Color color = null;
//          System.out.println("Vert: "+vertex);
          if(vertex.equals("root")) {
//            shape = new Rectangle((int)center.getX()-10, (int)center.getY()-10, 20, 20);
            color = new Color(127, 127, 0);
          } else if(vertex.equals("dim")) {
//            shape = new Rectangle((int)center.getX()-10, (int)center.getY()-20, 20, 40);
            color = new Color(127, 0, 127);
          } else if(vertex.equals("val")) {
//            shape = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
            color = new Color(0, 127, 127);
          }else{
        	  color = new Color(0, 127, 0);
        	  shape = new Rectangle((int)center.getX()-10, (int)center.getY()-10, 20, 20);
          }
          graphicsContext.setPaint(color);
//          graphicsContext.fill(shape);
        }
      }
    
    protected void loadModel(OntModel m) {
		FileManager.get().getLocationMapper().addAltEntry(SOURCE_URL, SOURCE_FILE);
		Model baseOntology = FileManager.get().loadModel(SOURCE_URL);
		m.addSubModel(baseOntology);
		m.setNsPrefix("st", NS);
	}
    
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
		} 
    }

    protected void getSubClasses( String padre,OntClass rootClass) {
		String type_padre = "";
		String type_node = "";
		
		String[] padre_splitted = padre.split("#");
		String radice = padre_splitted[0];
//		System.out.println("padre:"+radice);
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
			ExtendedIterator<? extends OntResource> inst= c.listInstances();
			int j=0;
			while (inst.hasNext()) {
				OntResource instance = inst.next();
//				type_nome="par"
				System.out.println("Node: "+node+"; INstance: " + instance.getURI());
				papa=nodebak;
				node=instance.getURI();
				plot(papa, type_padre, node, type_node);
				j++;
			} 
			if(j==0)
				plot(papa, type_padre, node, type_node);
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
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new Cdt());
        frame.pack();
        frame.setVisible(true);
    }
}

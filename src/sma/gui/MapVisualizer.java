package sma.gui;

import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.Composite;
import java.awt.geom.Rectangle2D;
import java.awt.AlphaComposite;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import sma.ontology.Cell;
import sma.ontology.InfoAgent;
/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2009-10. Robocup Rescue.</p>
 * Visualization of the map. There are several elements to depict, as
 * for example, buildings, streets, hospitals, a gas station, the agents,
 * the blocked streets, etc.<br>
 * This class *should be* modified and improved in order to show as good as
 * possible all the changes in the simulation. We provide several high-level
 * methods which can be rewritten as needed.<br>
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert López
 * @version 2.0
 */
public class MapVisualizer extends JPanel {

  private int inset = 50;
   int nrows, ncols;
   private Cell[][] t;
   java.awt.Point start, end;
   int dx, dy, gap;
   private Rectangle2D.Double cellBorder;
   private Rectangle2D.Double building;
   private Color[] buildingColors = {
       new Color(3,62,255),    new Color(255,255,166),  new Color(255,242,143),
       new Color(250,204,55),  new Color(255,193,130),  new Color(250,153,153),
       new Color(255,130,130), new Color(255,0,0)};

   private Ellipse2D.Double agentFigure;



   public MapVisualizer(Cell[][] t) {
     this.t = t;
     nrows = t.length;
     ncols = t[0].length;

     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     start = new Point(inset, inset);
     end = new Point(screenSize.width - inset * 2, screenSize.height - inset * 2);
     dx = (end.x-start.x)/ncols;
     dy = ((end.y-start.y)/nrows)-4;

     gap = 5;
     cellBorder = new Rectangle2D.Double(gap+10, gap+10, dx, dy);

     agentFigure = new Ellipse2D.Double(gap+10+(dx/4),gap+10+(dy/4),(dx/2),(dy/2));

   }

    private void drawBuilding(Graphics2D g2d, int x, int y, Cell c) {
//       g2d.translate(dx * x, dy * y);
      try {
    	g2d.setPaint( buildingColors[(c.getBurned() * this.buildingColors.length) / 100]);
        g2d.fill(cellBorder);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(cellBorder);
        String msg = c.getBurned()+"/"+c.getNumInjuredPeople()+"/"+c.getNumPeople();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                             java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        java.awt.Font font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 12);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);
        g2d.drawString(msg,dx-10,dy);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }

    private void drawHospital(Graphics2D g2d, int x, int y, Cell c) {
//      System.out.println("HOSPITAL (x,y)=("+x+","+y+")");
//       g2d.translate(dx * x, dy * y);
      try {
        g2d.setPaint(Color.WHITE);
        g2d.fill(cellBorder);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(cellBorder);
        String msg = "id: " + c.getIdHospital();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                             java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        java.awt.Font font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 12);
        g2d.setFont(font);
        g2d.setPaint(Color.BLACK);
        g2d.drawString(msg,dx-10,dy);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }

    private void drawGasStation(Graphics2D g2d, int x, int y, Cell c) {
//      System.out.println("GAS_STATION (x,y)=("+x+","+y+")");
//       g2d.translate(dx * x, dy * y);
      try {
        g2d.setPaint(Color.YELLOW);
        g2d.fill(cellBorder);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(cellBorder);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    
    private void moveXY(Graphics2D g2d, int x, int y) {
      g2d.translate(dx * x, dy * y);
    }

    private Color getColor(InfoAgent agent) {
      if(agent.getAgentType()==InfoAgent.AMBULANCE) return new Color(255,255,255);
      if(agent.getAgentType()==InfoAgent.FIREMAN) return new Color(255,0,0);
      if(agent.getAgentType()==InfoAgent.POLICEMAN) return new Color(0,0,255);
      return null;
    }

    private void drawStreetAgents(Graphics2D g2d, int x, int y, Cell c) {
      //it can be an agent, a private car or nothing
      if(c.isThereAnAgent()) {
        InfoAgent agent = c.getAgent();
        g2d.setPaint(getColor(agent));
        g2d.translate((dx/6),(dy/6));
        g2d.fill(agentFigure);
        g2d.translate(-(dx/6),-(dy/6));
        if (agent.getAgentType()==InfoAgent.AMBULANCE){
        	String msg = "hosp: " + agent.getIdHospital();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                 java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            java.awt.Font font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 12);
            g2d.setFont(font);
            g2d.setPaint(Color.BLACK);
            g2d.drawString(msg,dx-40,dy-10);
        }
      }
      if(c.isThereACar()) {
    	 g2d.setPaint(new Color(0,128,0));
         g2d.translate((dx/6),(dy/6));
         g2d.fill(agentFigure);
         g2d.translate(-(dx/6),-(dy/6));
      }
    }

    private void drawStreet(Graphics2D g2d, int x, int y, Cell c) {
      //       g2d.translate(dx * x, dy * y);
      try {
        g2d.setPaint(Color.LIGHT_GRAY);
        g2d.fill(cellBorder);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(cellBorder);
        if(c.isBlocked()) {
          g2d.setPaint(Color.BLACK);
          g2d.translate((dx/8),(dy/8));
          g2d.fill(agentFigure);
          g2d.translate(-(dx/8),-(dy/8));
        } else {
          drawStreetAgents(g2d,x,y,c);
        }
      } catch(Exception e) {
        e.printStackTrace();
      }

    }


    public void paintComponent(Graphics g) {
      clear(g);
      Graphics2D g2d = (Graphics2D)g;
      for(int i=0; i<t.length; i++) {
        for(int j=0; j<t[0].length; j++) {
          g2d.draw(cellBorder);
          if(t[i][j].getCellType()==Cell.STREET) drawStreet(g2d, i, j, t[i][j]);
          if(t[i][j].getCellType()==Cell.HOSPITAL) drawHospital(g2d, i, j, t[i][j]);
          if(t[i][j].getCellType()==Cell.BUILDING) drawBuilding(g2d, i, j, t[i][j]);
          if(t[i][j].getCellType()==Cell.GAS_STATION) drawGasStation(g2d, i, j, t[i][j]);
          g2d.translate(dx,0);
        }
        g2d.translate(-(dx*t[0].length),dy);
      }

      this.repaint();
   }

   protected void clear(Graphics g) {
     super.paintComponent(g);
   }

   protected Ellipse2D.Double getCircle() {
     return(agentFigure);
   }

 }

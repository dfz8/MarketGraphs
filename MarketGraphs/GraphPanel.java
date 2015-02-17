import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.text.*;

public class GraphPanel extends JPanel
{
   static final int WIDTH = 1000;
   static final int HEIGHT = 400; 
   static final int BORDER = 20;
   static double CHANGE;
   
   String filename;
   BufferedReader input;
   boolean dispLive;
   
   double xscale, yscale;
   double ymin, ymax;
   double xmin, xmax; //del
	
   Timer t;
   Timer s;
   
   BufferedImage myImage;
   Graphics myBuffer;
   
   int counter;
	
   ArrayList<Double> values;
   double curVal;
   
   double open;
   double close; //del
   double max;
   double min;
   
   double pchange;
   double mmax;
   double mmin;
   int lastpos;
   int tps;
   
   int curMx;
   int curMy;
   boolean mc;
   Color gray;
   DecimalFormat format;
   
   public GraphPanel() throws Exception
   {
      //filename = JOptionPane.showInputDialog("Filename:");
      //input = new BufferedReader(new FileReader(new File(filename)));
      //dispLive = JOptionPane.showInputDialog("\"Live\"? (y/n)").equals("y");
      
      CHANGE = Double.parseDouble(JOptionPane.showInputDialog("Enter degree of change ( > 1.0)"));
      myImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
      myBuffer = myImage.getGraphics();
   	
      myBuffer.setColor(Color.WHITE);
      myBuffer.fillRect(0,0,WIDTH,HEIGHT);
      
      myBuffer.setFont( new Font("Agency FB",Font.BOLD,16));  
      counter = lastpos = 0;
      
      curVal = 1000;
      values = new ArrayList<Double>();
      values.add(curVal);
      
      //starting window
      ymin = curVal-150;
      ymax = curVal+150;
      mmin = mmax = curVal;
      yscale = (HEIGHT-5*BORDER)/(ymax-ymin);
   	
      open = close = max = min = curVal;
   	
      addMouseMotionListener(new Mouse());
      //addMouseListener(new MouseClicks());
      setFocusable(true);
      
      format = new DecimalFormat("0.00");
      
      gray = Color.GRAY.brighter();
      
      t = new Timer(50, new UpdateListener());
      s = new Timer(1000, new SessionListener());	
      
      tps = 1000/50;
      t.start();
      s.start();
   }
   
   public void paintComponent(Graphics g)
   {	
      g.drawImage(myImage, 0, 0, WIDTH, HEIGHT, null);
   }
   
   private class UpdateListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         paintAxis();
      
      
         myBuffer.setColor(Color.BLACK);
         double change = ((int)(100.0*(CHANGE*Math.random()-CHANGE/2)))/100.0; ////////
         //double change = 20;
         curVal+=change;
         if(curVal < min) min = curVal;
         if(curVal > max) max = curVal;
         if(curVal < mmin) mmin = curVal;
         if(curVal > mmax) mmax = curVal;
         values.add(curVal);
         
      	
         if(curVal > ymax || curVal < ymin){ rebound();}
      	
      	
         myBuffer.setColor(Color.WHITE);
         myBuffer.fillRect(200, HEIGHT-4*BORDER+1, 200, HEIGHT-BORDER);
         myBuffer.setColor(Color.BLACK);
         myBuffer.drawString("VALUE: " + format.format(curVal), 200, HEIGHT-2*BORDER);
      	
         myBuffer.drawLine(BORDER+(counter-1), convY(curVal-change), BORDER+(counter), convY(curVal));
      
         if(counter==WIDTH-2*BORDER-1) {t.stop(); s.stop(); drawSession();}
         counter++;
         repaint();
      }
   }
   
   public void rebound()
   {
      ymin = mmin - (mmax-mmin)/3;
      ymax = mmax + (mmax+mmin)/3;
      yscale = (HEIGHT-5*BORDER)/(ymax-ymin);
      
      myBuffer.setColor(Color.WHITE);
      myBuffer.fillRect(0,0,WIDTH, HEIGHT-BORDER);
      paintAxis();
      
      myBuffer.setColor(Color.BLACK);
      double tmin, tmax;
      tmin=tmax=values.get(0);
      int lp = 0;
      double v;
      for(int i = 1; i < values.size(); i++)
      {
         v = values.get(i);
         if(v > tmax) tmax = v;
         if(v < tmin) tmin = v;	
         
         myBuffer.setColor(Color.BLACK);
         myBuffer.drawLine(BORDER+(i-1), convY(values.get(i-1)), BORDER+(i), convY(values.get(i)));
         
         if(i%tps ==0)
         {
            drawSession(lp, i, tmax, tmin);
            tmax=tmin =  v;
            lp = i;
         }
      }
      
   }
         
   public void drawSession(int lp, int cp, double ma, double mi)
   {
      myBuffer.setColor(Color.WHITE);
      myBuffer.fillRect(0, HEIGHT-4*BORDER+1, 200, HEIGHT-BORDER);
      
      double cv = values.get(cp);
      double op = values.get(lp);
   	
      pchange = (cv-op)/cv;
      myBuffer.setColor(Color.BLACK);
      myBuffer.drawString("CHANGE: ", BORDER, HEIGHT-2*BORDER);
         
      if(pchange >= 0.0)
      { 
         myBuffer.setColor(Color.GREEN.darker());
         myBuffer.drawRect(lp+BORDER, convY(cv), cp-lp, (int)((cv-op)*yscale));
         myBuffer.drawLine(lp+BORDER+(cp-lp)/2, convY(cv), lp+BORDER+(cp-lp)/2, convY(ma));
         myBuffer.drawLine(lp+BORDER+(cp-lp)/2, convY(op), lp+BORDER+(cp-lp)/2, convY(mi));
      }
      else 
      {
         myBuffer.setColor(Color.RED);
         myBuffer.drawRect(lp+BORDER, convY(op), cp-lp, (int)((op-cv)*yscale));
         myBuffer.drawLine(lp+BORDER+(cp-lp)/2, convY(cv), lp+BORDER+(cp-lp)/2, convY(mi));
         myBuffer.drawLine(lp+BORDER+(cp-lp)/2, convY(op), lp+BORDER+(cp-lp)/2, convY(ma));
      }
      
         
      myBuffer.drawString(((int)(1000.0*pchange))/1000.0 + "%", BORDER+50, HEIGHT-2*BORDER);
      	  
      repaint();
   }
   
   public void drawSession()
   {
      myBuffer.setColor(Color.WHITE);
      myBuffer.fillRect(0, HEIGHT-4*BORDER+1, 200, HEIGHT-BORDER);
   
      pchange = (curVal-open)/curVal;
      myBuffer.setColor(Color.BLACK);
      myBuffer.drawString("CHANGE: ", BORDER, HEIGHT-2*BORDER);
         
      if(pchange >= 0.0)
      { 
         myBuffer.setColor(Color.GREEN.darker());
         myBuffer.drawRect(lastpos+BORDER, convY(curVal), counter-lastpos, (int)((curVal-open)*yscale));
         myBuffer.drawLine(lastpos+BORDER+(counter-lastpos)/2, convY(curVal), lastpos+BORDER+(counter-lastpos)/2, convY(max));
         myBuffer.drawLine(lastpos+BORDER+(counter-lastpos)/2, convY(open), lastpos+BORDER+(counter-lastpos)/2, convY(min));
      }
      else 
      {
         myBuffer.setColor(Color.RED);
         myBuffer.drawRect(lastpos+BORDER, convY(open), counter-lastpos, (int)((open-curVal)*yscale));
         myBuffer.drawLine(lastpos+BORDER+(counter-lastpos)/2, convY(curVal), lastpos+BORDER+(counter-lastpos)/2, convY(min));
         myBuffer.drawLine(lastpos+BORDER+(counter-lastpos)/2, convY(open), lastpos+BORDER+(counter-lastpos)/2, convY(max));
      }
      
         
      myBuffer.drawString(((int)(1000.0*pchange))/1000.0 + "%", BORDER+50, HEIGHT-2*BORDER);
        
      lastpos = counter; 
      open = min = max = curVal; 
      	  
      repaint();
   
   }


   public void paintAxis()
   {
      myBuffer.setColor(Color.BLACK);
      myBuffer.drawRect(BORDER,BORDER,WIDTH-2*BORDER,HEIGHT-5*BORDER);
      myBuffer.drawString(" "+ format.format(ymax), BORDER, BORDER+15);
      myBuffer.drawString(" "+ format.format(ymin), BORDER, HEIGHT-4*BORDER);
   
   
      myBuffer.setColor(gray);     	
      for(int i = (int)(ymin/100)+1; i <= (int)(ymax/100); i++)
      {
         myBuffer.drawLine(BORDER, convY(100*i), WIDTH-BORDER, convY(100*i));
      }
   
      if(ymin < 0 && ymax > 0)
      {
         myBuffer.setColor(gray.darker().darker());
         myBuffer.drawLine(BORDER, convY(0), WIDTH-BORDER, convY(0));
         myBuffer.setColor(Color.BLACK);
         myBuffer.drawString("0", BORDER, convY(0));
      }	
   }
   
   public int convY(double y)
   {
   	//System.out.println( (ymax-y)/yscale);
      return (int)((ymax-y)*yscale)+BORDER;
   }

   public class Mouse extends MouseMotionAdapter
   {
      public void mouseMoved(MouseEvent e)
      {
         curMx = e.getX();
         curMy = e.getY();
         
         if(BORDER < curMx && curMx < WIDTH-BORDER 
         && BORDER < curMy && curMy < HEIGHT-4*BORDER)
         {
            myBuffer.setColor(Color.WHITE);
            myBuffer.fillRect(WIDTH/2, HEIGHT-4*BORDER+1, 200, HEIGHT-BORDER);
            
            myBuffer.setColor(Color.BLACK);
            myBuffer.drawString("TIC  : " + (curMx-BORDER), WIDTH/2, HEIGHT-2*BORDER);
            if(curMx - BORDER < values.size())
               myBuffer.drawString("Value-Tic: " + format.format(values.get(curMx-BORDER)), WIDTH/2, HEIGHT-BORDER);
            else
               myBuffer.drawString("Value-Tic: --", WIDTH/2, HEIGHT-BORDER);
               
            myBuffer.drawString("Value-Cursor: " + format.format(((ymax-(curMy-BORDER)/yscale))), WIDTH/2, HEIGHT);
            repaint();
         }
         else
         {
         	/*
            myBuffer.drawString("TIC  : --" + (curMx-BORDER), WIDTH/2, HEIGHT-2*BORDER);
            myBuffer.drawString("V.A.T: --", WIDTH/2, HEIGHT-BORDER);
            myBuffer.drawString("V.A.C: --" + (ymax-(curMy-BORDER)/yscale), WIDTH/2, HEIGHT);
            */
         }
         
      }
   }
   	
   private class SessionListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         drawSession();
      }
   }

}


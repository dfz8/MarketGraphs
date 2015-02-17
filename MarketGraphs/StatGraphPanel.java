/*
* Screen is 
* 
* 
* 
* 
*/
   import java.io.*;
   import java.awt.*;
   import java.awt.image.*;
   import javax.swing.*;
   import java.util.*;

   public class StatGraphPanel
   {
      static final int WIDTH = 1000;
      static final int HEIGHT = 500;
      static final int BORDER = 15;
      static final double PRECISION = 1000.0; //3 dec
   	
      static ImageIcon myImage;
      static Graphics myBuffer;
      public static void main(String[] args) throws Exception
      {
         String filename = JOptionPane.showInputDialog("Filename");
         BufferedReader input = new BufferedReader(new FileReader(new File(filename+".txt")));
         int num = Integer.parseInt(input.readLine());
      	
         double[] values = new double[num+1];
         double[] diff = new double[num];
      	
         values[0] = Double.parseDouble(input.readLine());
         double max = 0; double min = values[0];
      	
         for(int i = 1; i < num+1; i++)
         {
            diff[i-1] = Double.parseDouble(input.readLine());
            values[i] = values[i-1]+diff[i-1];
            if(values[i] > max) max = values[i];
            if(values[i] < min) min = values[i];
         }
         System.out.println("Start: " + values[0] + " Finish: " + values[num]);
         double range = max - min;
         double ymin = min - 1.5*range;
         double ymax = max + 0.5*range;
      
         double xscale, yscale;
         yscale = ((int)(PRECISION*(ymax-ymin)/(HEIGHT-3*BORDER)))/PRECISION;
         System.out.println("yscale: " + yscale);
      	
         xscale = (WIDTH - 3*BORDER)/(num);
         if(xscale > 2.0) xscale = 2.0;//little bit of spacing out
         else if(xscale > 1.0) xscale = 1.0;
         System.out.println("xscale: " + xscale);
      	//painting the things on
      	
      	//creating the window
         JFrame frame = new JFrame("Graph of " + filename);
         frame.setSize(WIDTH, HEIGHT);
         frame.setVisible(true);
         frame.setResizable(false);
         frame.setLocation(300,200);
         frame.setContentPane(new GraphPanel());
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      }
   }
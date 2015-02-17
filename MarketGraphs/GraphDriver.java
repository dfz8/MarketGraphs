
import javax.swing.*;

public class GraphDriver
{
   public static void main(String[] args) throws Exception
   {
      JFrame frame = new JFrame("Graph");
      frame.setSize(1000+8, 400+34);
      frame.setLocation(300,200);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      GraphPanel panel = new GraphPanel();
      frame.setContentPane(panel);			
      frame.setVisible(true);         
      frame.setResizable(false);
      panel.requestFocus();
   }
}
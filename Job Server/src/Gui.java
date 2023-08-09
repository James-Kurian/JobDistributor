import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Gui implements MouseWheelListener,KeyListener{

    private JFrame frame;
    private JPanel panel;
    private int scrollAmount = 10;
    private JTextArea outputBox;
    private JTextField input;
    

    

    private Color darkGreen = new Color(0,153,0);




    public Gui(){
        
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setVisible(true);

        frame.addMouseWheelListener(this);

        	
        input = new JTextField(1);
        input.setBackground(Color.black);
        input.setForeground(darkGreen);
        
         
        input.setPreferredSize(new Dimension(50, 30));
        input.setFont(new Font("Serif", Font.PLAIN, 20));
        panel.add(input,BorderLayout.PAGE_END);
        input.addKeyListener(this);

        
        outputBox = new JTextArea();
        outputBox.setEditable(false);
        outputBox.setFont(new Font("Serif", Font.PLAIN, 22));
        outputBox.setBackground(Color.BLACK);
        outputBox.setForeground(darkGreen);
        outputBox.setLineWrap(true);

        JScrollPane scrollBar = new JScrollPane(outputBox);

        scrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        
        panel.add(scrollBar, BorderLayout.CENTER);
        
        
        frame.add(panel);
        frame.setSize(500,500);

        

    }
    
    public void write(String msg){
        outputBox.append(msg);
    }



    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        
        int pos = outputBox.getCaretPosition();

        if (e.getWheelRotation()==1){
            
            
            
            if (outputBox.getText().length()-pos-scrollAmount<=0){
                pos=outputBox.getText().length();
            }else{
                pos+=scrollAmount;
            }
            outputBox.setCaretPosition(pos);
            
        }
        if (e.getWheelRotation()==-1){
            
             
            
            if (pos-scrollAmount<=0){
                pos=0;
            }else{
                pos-=scrollAmount;
            }
            outputBox.setCaretPosition(pos);

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            Main.runCommand(input.getText());
            input.setText("");
            

        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    public void close(){
        frame.dispose();
    }

}

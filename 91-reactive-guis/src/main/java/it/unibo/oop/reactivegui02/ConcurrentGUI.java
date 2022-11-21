package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton down = new JButton("down");
    private final JButton up = new JButton("up");


    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel canvas = new JPanel();

        this.add(canvas);

        canvas.add(display);
        canvas.add(up);
        canvas.add(down);
        canvas.add(stop);

        Agent agent = new Agent();
        new Thread(agent).start();

        stop.addActionListener((e) -> agent.stopCounting());
        up.addActionListener((e) -> agent.letItGrow());
        down.addActionListener((e) -> agent.letItWane());

        this.setVisible(true);

    }


    private class Agent implements Runnable{

        private volatile boolean stop;
        private volatile boolean grow = true;
        private int counter;

        @Override
        public void run() {
            while(!stop){
                try{
                    counter = grow ? counter+1 : counter-1;
                    final String value = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(value));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(ConcurrentGUI.this, ex.getMessage());
                }
            } 
        }

        public void stopCounting() {
            this.stop = true;
            ConcurrentGUI.this.up.setEnabled(false);
            ConcurrentGUI.this.down.setEnabled(false);
            ConcurrentGUI.this.stop.setEnabled(false);
        }

        public void letItGrow() {
            this.grow = true;
        }

        public void letItWane() {
            this.grow = false;
        }
        
    }
}

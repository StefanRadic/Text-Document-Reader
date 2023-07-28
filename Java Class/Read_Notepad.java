package guiProjekat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class Read_Notepad {
	
	/*
	 *  Main method
	 */
	
    public static void main(String[] args) {
        new Read_Notepad();
    }

    public Read_Notepad() {
    	
    	/*
    	 *  Single Thread
    	 */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
            	
                JFrame frame = new JFrame();
                
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          
            }
        });
    }

    public static class TestPane extends JPanel {
    	
        private JTextPane textPane = new JTextPane();

        private Timer timer;
        private List<String> lines;
        public int counter = 0;
        private String currentLine;
        private int currentLinePosition;
        
        public TestPane() {
        
            setLayout(new BorderLayout());
            textPane = new JTextPane();
            add(new JScrollPane(textPane));
            textPane.setEditable(false);
            
            JPanel actionsPane = new JPanel(new GridBagLayout());
            
            final JButton Start = new JButton("Read the file");
            final JButton Stop = new JButton("Stop reading");

            actionsPane.add(Start);
            actionsPane.add(Stop);
            
            /*
             * Key Listener
             */
            
            textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
            	start();
        	    Start.setEnabled(false);
        	    Stop.setEnabled(true);
            	} 
			});
            
            /*
             * Mouse Listener
             */
            textPane.addMouseListener(new MouseAdapter() {
            	@Override
            	public void mouseClicked(MouseEvent arg0) {
            		start();
            	    Start.setEnabled(false);
            	    Stop.setEnabled(true);
            	}
			}); 
            
            /*
             * Listener for any key Pressed on a keyboard. It will start the reading. 
             */
            
            textPane.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
            	    Start.setEnabled(false);
            	    Stop.setEnabled(true);
					
				}

				@Override
				public void keyTyped(KeyEvent e) {
            	    Start.setEnabled(false);
            	    Stop.setEnabled(true);
					
				}

				@Override
				public void keyReleased(KeyEvent e) {
            	    Start.setEnabled(false);
            	    Stop.setEnabled(true);
					
				}
			});
           
            /*
             * Listener for Start JButton
             */
            
            Start.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	start();
                    Start.setEnabled(false);
                    Stop.setEnabled(true);
                }
            });
            
            /*
             * Listener for Stop JButton
             */
            
            Stop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stopTimer();
                    Start.setEnabled(true);
                    Stop.setEnabled(false);
                }
            });

            add(actionsPane, BorderLayout.SOUTH);
            loadScript();
        }
        
        /*
         * Add a Dimension size
         */
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1200, 760);
        }
        
        public void loadScript() {
            lines = new ArrayList<>(128);
            /*
             * One can put only the folder's name and get the content inside displayed.
             */
            try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/scripts/Document.txt")))) {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    lines.add(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(Read_Notepad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public int startPosition(String text) {
            if (text.isEmpty()) {
                return 0;
            }
            int index = 0;
            while (Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            return index;
        }

        public void insertWithOutError(String text) {
            try {
                insert(text);
            } catch (BadLocationException ex) {
                Logger.getLogger(Read_Notepad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void insert(String text) throws BadLocationException {
            Document document = textPane.getDocument();
            document.insertString(document.getLength(), text, null);
        }

        public void stopTimer() {
            if (timer != null) {
                timer.stop();
                timer = null;
            }

            if (currentLine != null) {
                if (currentLinePosition < currentLine.length()) {
                    String text = currentLine.substring(currentLinePosition);
                    try {
                        insert(text);
                        insertWithOutError("\n");
                    } catch (BadLocationException ex) {
                        Logger.getLogger(Read_Notepad.class.getName()).log(Level.SEVERE, null, ex);
                    }        
                }
            }

            currentLine = null;
        }

        public void start() {
            stopTimer();
            if (lines.isEmpty()) {
            	return;
                
            }
            try {
                // Pop the first line
                currentLine = lines.remove(0);
                int offset = startPosition(currentLine);
                if (currentLine.isEmpty() || offset == currentLine.length()) {
                    insertWithOutError("\n");
                } else {
                    String leading = currentLine.substring(0, offset);
                    insert(leading);
                }
                currentLinePosition = 0;
                currentLine = currentLine.trim();
                // Adjust the delay of timer (miliseconds).
                timer = new Timer(10, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (currentLinePosition < currentLine.length()) {
                            try {
                                String next = currentLine.substring(currentLinePosition, currentLinePosition + 1);
                                insert(next);             
                                currentLinePosition++;
                                
                            } catch (BadLocationException ex) {
                                Logger.getLogger(Read_Notepad.class.getName()).log(Level.SEVERE, null, ex);
                                stopTimer();
                            }
                        } else {
                            insertWithOutError("\n");
                            start();
                        }
                    }
                });
                timer.start();
            } catch (BadLocationException ex) {
                Logger.getLogger(Read_Notepad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

		public static Document getDocument() {
			return null;
		}
	}
  }
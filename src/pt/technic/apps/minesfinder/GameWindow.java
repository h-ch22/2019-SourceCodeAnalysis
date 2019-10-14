package pt.technic.apps.minesfinder;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.*;

import static javax.swing.JOptionPane.YES_NO_OPTION;

/**
 *
 * @author Gabriel Massadas
 */
public class GameWindow extends javax.swing.JFrame {

    private ButtonMinefield[][] buttons;
    private Minefield minefield;
    private RecordTable record;
    private boolean isLeftPressed = false;
    private boolean isRightPressed = false;
    String command_str;

    /**
     * Creates new form GameWindow
     */
    public GameWindow() {
        initComponents();
    }

    public GameWindow(Minefield minefield, RecordTable record) {
        initComponents();

        this.minefield = minefield;
        this.record = record;

        buttons = new ButtonMinefield[minefield.getWidth()][minefield.getHeight()];

        getContentPane().setLayout(new GridLayout(minefield.getWidth(),
                minefield.getHeight()));

        JMenuBar Game_MenuBar = new JMenuBar();
        JMenu Game_Menu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem Save = new JMenuItem("Save this Game");
        JMenuItem Statistics = new JMenuItem("Statistics");
        JMenuItem Options = new JMenuItem("Options");
        JMenuItem Exit = new JMenuItem("Exit");

        Game_MenuBar.add(Game_Menu);
        Game_Menu.add(newGame);
        Game_Menu.add(Save);
        Game_Menu.addSeparator();
        Game_Menu.add(Statistics);
        Game_Menu.add(Options);
        Game_Menu.add(Exit);

        setJMenuBar(Game_MenuBar);
        setVisible(true);

        MinesFinder MF = new MinesFinder();

        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                command_str = e.getActionCommand();
                if(command_str == "New Game"){
                    int newGame_result = JOptionPane.showConfirmDialog(null, "새 게임을 시작하시겠습니까? 통계에는 패배로 기록됩니다.", "새 게임 시작", YES_NO_OPTION);

                    if(newGame_result == 0){
                        if(MF.btnHard_isRunning){
                            Minefield.setDefeated = true;
                            MF.btnHard_isRunning = true;
                        }

                        if(MF.btnMedium_isRunning){
                            Minefield.setDefeated = true;
                            MF.btnMedium_isRunning = true;
                        }

                        if(MF.btnEasy_isRunning){
                            Minefield.setDefeated = true;
                            MF.btnEasy_isRunning = true;
                        }
                    }
                }
            }
        });

        ActionListener action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonMinefield button = (ButtonMinefield) e.getSource();
                int x = button.getCol();
                int y = button.getLine();
                minefield.revealGrid(x, y);
                updateButtonsStates();
                if (minefield.isGameFinished()) {
                    if (minefield.isPlayerDefeated()) {
                        int ans = JOptionPane.showConfirmDialog(null, "지뢰를 밟으셨습니다. 게임을 종료하시겠습니까?",
                                "Lost!", YES_NO_OPTION);

                        if(ans == 0){
                            minefield.isPlayerDefeated();
                            setVisible(false);
                        }

                        else{
                            minefield.isPlayerContinue();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Congratulations. You managed to discover all the mines in "
                                + (minefield.getGameDuration() / 1000) + " seconds",
                                "victory", JOptionPane.INFORMATION_MESSAGE
                        );
                        long a = minefield.getGameDuration();
                        long b = record.getScore();
                        boolean newRecord = minefield.getGameDuration() < record.getScore();

                        if (newRecord) {
                            String name = JOptionPane.showInputDialog("Enter your name");
                            if(name != "")
                                record.setRecord(name, minefield.getGameDuration());
                        }
                        setVisible(false);
                    }
                }
            }
        };

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)){
                    isLeftPressed = true;
                    Timer LeftMouseTimer = new Timer("LeftMouseDelay", false);
                    LeftMouseTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isLeftPressed = false;
                        }
                    }, 200);
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    isRightPressed = true;
                    Timer RightMouseTimer = new Timer("RightMouseDelay", false);
                    RightMouseTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isRightPressed = false;
                        }
                    }, 200);

                    ButtonMinefield botao = (ButtonMinefield) e.getSource();
                    int x = botao.getCol();
                    int y = botao.getLine();
                    if (minefield.getGridState(x, y) == minefield.COVERED) {
                        minefield.setMineMarked(x, y);
                    } else if (minefield.getGridState(x,
                            y) == minefield.MARKED) {
                        minefield.setMineQuestion(x, y);
                    } else if (minefield.getGridState(x,
                            y) == minefield.QUESTION) {
                        minefield.setMineCovered(x, y);
                    }
                    updateButtonsStates();
                }

                if(isRightPressed && isLeftPressed){
                    System.exit(1);
                }
            }

            @Override
            public void mouseClicked(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        };

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                ButtonMinefield botao = (ButtonMinefield) e.getSource();
                int x = botao.getWidth();
                int y = botao.getHeight();
                if (e.getKeyCode() == KeyEvent.VK_UP && y > 0) {
                    buttons[x][y - 1].requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT && x > 0) {
                    buttons[x - 1][y].requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && y
                        < minefield.getHeight() - 1) {
                    buttons[x][y + 1].requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && x
                        < minefield.getWidth() - 1) {
                    buttons[x + 1][y].requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    if (minefield.getGridState(x, y) == minefield.COVERED) {
                        minefield.setMineMarked(x, y);
                    } else if (minefield.getGridState(x,
                            y) == minefield.MARKED) {
                        minefield.setMineQuestion(x, y);
                    } else if (minefield.getGridState(x,
                            y) == minefield.QUESTION) {
                        minefield.setMineCovered(x, y);
                    }
                    updateButtonsStates();
                }
            }

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        };
        
        // Create buttons for the player
        for (int x = 0; x < minefield.getWidth(); x++) {
            for (int y = 0; y < minefield.getHeight(); y++) {
                buttons[x][y] = new ButtonMinefield(x, y);
                buttons[x][y].addActionListener(action);
                buttons[x][y].addMouseListener(mouseListener);
                buttons[x][y].addKeyListener(keyListener);
                getContentPane().add(buttons[x][y]);
            }
        }
    }

    private void updateButtonsStates() {
        for (int x = 0; x < minefield.getWidth(); x++) {
            for (int y = 0; y < minefield.getHeight(); y++) {
                buttons[x][y].setEstado(minefield.getGridState(x, y));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Game");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1094, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

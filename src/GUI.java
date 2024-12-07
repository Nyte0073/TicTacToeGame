import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GUI {
    static Board board = new Board();
    HashMap <JButton, Integer> buttonNumbers = new HashMap<>();
    JButton[] buttons;
    int buttonNumber = 0;
    ActionListener buttonListener, buttonListener2, buttonListener3;
    final JLabel titleLabel = new JLabel();
    final JLabel timerLabel = new JLabel("00:00");
    final JButton restartButton = new JButton("Restart Game");
    final JButton timerButton  = new JButton("Play/Pause");
    static boolean aiEnabled = false;
    boolean whichPlayer = true;
    boolean gameIsDone = false;
    ArrayList <Integer> possibleMoves = new ArrayList<>();
    int randomSpot;
    java.util.Timer timer;
    int minutes = 0;
    int seconds = 0;
    boolean phase = false;

    public void startTimer() {

        phase = !phase;

        if(phase) {
            timer = new java.util.Timer();
        }

        if(!phase) {
            timer.cancel();
            return;
        }

        if(seconds == 0) {
            for (int i = 0; i < 1200; i++) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        seconds++;
                        if(seconds == 60) {
                            minutes++;
                            seconds = 0;
                        }
                        String minutesStr = minutes <= 9 ? "0" + minutes : Integer.toString(minutes);
                        String secondsStr = seconds <= 9 ? "0" + seconds : Integer.toString(seconds);

                        timerLabel.setText(minutesStr + ":" + secondsStr);
                    }
                }, i*1000L);
            }
        } else {
            for (int i = 0; i < 1200 - (minutes * 60) + seconds; i++) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        seconds++;
                        if(seconds == 60) {
                            minutes++;
                            seconds = 0;
                        }
                        String minutesStr = minutes <= 9 ? "0" + minutes : Integer.toString(minutes);
                        String secondsStr = seconds <= 9 ? "0" + seconds : Integer.toString(seconds);

                        timerLabel.setText(minutesStr + ":" + secondsStr);
                    }
                }, i* 1000L);
            }
        }
    }

    public void resetTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }

    public void createBaseGUI() {
        JFrame frame = new JFrame("Tic Tac Toe Game");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        buttons = new JButton[Board.gridSize*Board.gridSize];
        JPanel panel = new JPanel(new GridLayout(Board.gridSize, Board.gridSize, 10, 10));
        panel.setBounds(340, 100, 800, 670);

        titleLabel.setBounds(340, 0, 800, 70);
        titleLabel.setText("It's " + Player.player1Name + "'s turn to go.");
        titleLabel.setBackground(Color.GRAY);
        titleLabel.setOpaque(true);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 30));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK));

        timerLabel.setBounds(10, 150, 300, 120);
        timerLabel.setText("00:00");
        timerLabel.setBackground(Color.GRAY);
        timerLabel.setOpaque(true);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 50));
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK));

        buttonListener = (e) -> {
            JButton button = (JButton) e.getSource();
            board.boardNumber = buttonNumbers.get(button);

            whichPlayer = !whichPlayer;

                remove(board.boardNumber);
                if(!whichPlayer) {
                    Player.player1Moves.add(board.boardNumber);
                    button.setText(Player.player1Letter);
                } else {
                    Player.player2Moves.add(board.boardNumber);
                    button.setText(Player.player2Letter);
                }

            if(button.isEnabled()) {
                button.setEnabled(false);
            }

            if(aiEnabled) {
                runAi(Player.player2Name);
            }

            if(board.checkForWinner("player1")) {
                for (JButton b : buttons) {
                    b.setEnabled(false);
                }
                restartButton.setEnabled(false);
                timerButton.setEnabled(false);
                resetTimer();
                gameIsDone = true;
                titleLabel.setText("Congratulations! " + Player.player1Name + " has won!");
                new Timer(4000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                }).start();
            } else if(board.checkForWinner("player2")) {
                for (JButton b : buttons) {
                    b.setEnabled(false);
                }
                restartButton.setEnabled(false);
                timerButton.setEnabled(false);
                resetTimer();
                gameIsDone = true;
                titleLabel.setText("Congratulations! " + Player.player2Name + " has won!");
                new Timer(4000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                }).start();
            }

            if(!gameIsDone) {
                if(!whichPlayer) {
                    titleLabel.setText("It's " + Player.player2Name + "'s turn to go.");
                } else {
                    titleLabel.setText("It's " + Player.player1Name+ "'s turn to go.");
                }
            }

            if(aiEnabled) {
                whichPlayer = true;
            }
        };

        buttonListener2 = e -> restartGame();

        buttonListener3 = (e) -> startTimer();

        timerButton.addActionListener(buttonListener3);
        timerButton.setBounds(10, 300, 300, 100);
        timerButton.setBackground(Color.GRAY);
        timerButton.setForeground(Color.BLACK);
        timerButton.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK));
        timerButton.setFont(new Font("JetBrains Mono", Font.PLAIN, 30));

        restartButton.addActionListener(buttonListener2);
        restartButton.setFont(new Font("JetBrains Mono", Font.PLAIN, 30));
        restartButton.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK));
        restartButton.setBounds(10, 0, 300, 100);
        restartButton.setBackground(Color.GRAY);
        restartButton.setForeground(Color.BLACK);

        for(int i = 0; i < buttons.length; i++) {
            buttonNumber++;
            buttons[i] = new JButton();
            buttons[i].setBackground(Color.GRAY);
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setFont(new Font("JetBrains Mono", Font.BOLD, 100));
            buttons[i].setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK));
            buttons[i].addActionListener(buttonListener);
            buttonNumbers.put(buttons[i], buttonNumber);
            possibleMoves.add(buttonNumber);
            panel.add(buttons[i]);
        }

        frame.add(timerLabel);
        frame.add(panel);
        frame.add(titleLabel);
        frame.add(restartButton);
        frame.add(timerButton);
        frame.setVisible(true);
    }

    public void runAi(String type) {
        switch(type) {
            case "ai":
                randomSpot = (int) ((Math.random()/(100/possibleMoves.size())) * 100);
                JButton button = find(possibleMoves.get(randomSpot == 0 ? 0 : randomSpot - 1));
                if(button.isEnabled()) {
                    board.aiNumber = buttonNumbers.get(button);
                    Player.player2Moves.add(buttonNumbers.get(button));
                    button.setText(Player.player2Letter);
                    button.setEnabled(false);
                } else {
                    try {
                        runAi("ai");
                    } catch (StackOverflowError e) {
                        System.out.println("No pieces left. Restart the game.");
                    }
                }
                titleLabel.setText("Its " + Player.player2Name + "'s turn to go.");
                break;

            case "advanced ai":

                break;
        }
    }

    public void restartGame() {
        whichPlayer = true;
        titleLabel.setText(Player.player1Name + "'s turn to go.");
        timerLabel.setText("00:00");
        seconds = 0;
        minutes = 0;
        if(timer != null) {
            resetTimer();
        }
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(true);
        }
        Player.player1Moves.clear();
        Player.player2Moves.clear();
    }

    public void remove(int buttonNumber) {
        Iterator <Integer> it = possibleMoves.iterator();
        while(it.hasNext()) {
            int num = it.next();
            if(Objects.equals(num, buttonNumber)) {
                it.remove();
                break;
            }
        }
    }

    public JButton find(int buttonNumber) {
        for(JButton button : buttonNumbers.keySet()) {
            if(Objects.equals(buttonNumber, buttonNumbers.get(button))) {
                return button;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        Player.player1Name = "Brayden";
        Player.player2Name = "ai";
        Player.player1Letter = "X";
        Player.player2Letter = "O";
        Board.gridSize = 5;
        gui.createBaseGUI();
    }
}

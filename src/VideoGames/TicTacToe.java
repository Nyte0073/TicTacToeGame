package VideoGames;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Timer;

/*Problems:
*
* 1. Find out which player in tic-tac-toe wins based on
* the sequencing of the spots that both players have claimed.
*
* 2. Find out if a space is taken up on the board and if the space
* that one player wants to take is available.
*
* Initial Information:
*
* - Names for player1 and player2
* - The size of the board
* - Letters for player1 and player2
* */
public class TicTacToe {

    public static class Objects {
        static Player player;
        static GUI gui;
        static Board board;
        public Objects(String player1Name, String player2Name, int gridSize) {
            player = new Player(player1Name, player2Name);
            gui = new GUI(gridSize);
            board = new Board(gridSize);
        }

        public Objects() {

        }
    }
    public static class Board extends Objects {
        static int gridSize; //Size of the board.
        JPanel boardPanel; //Graphic component for holding the buttons of the tic-tac-toe game.
        public Board(int gridSize) { //Initializes a new Board with the size of the tic-tac-toe grid.
            Board.gridSize = gridSize;
            boardPanel = new JPanel(new GridLayout(gridSize, gridSize, 10,10));
        }

        private final String[] directions = {"horizontal", "vertical", "diagonal_right", "diagonal_left"}; //All the possible directions for getting all in a row.

        public boolean checkForWinner(List <Integer> moves) { //Checks to see if the player won.
           for(String s : directions) {
               if(contains(s, moves)) {
                   return true;
               }
           }
           return false;
        }

        public boolean contains(String direction, List <Integer> moves) {
            List <Integer> winningMoves = new ArrayList<>();
            int placeholder;
            int counter = 0;
             switch(direction) {
                case "horizontal":
                    for(int i : moves) {
                        winningMoves.clear();
                        placeholder = i - (i % gridSize);
                        for(int j = 0; j < gridSize; j++) {
                            winningMoves.add(placeholder + counter);
                            counter++;
                        }
                        if(new HashSet<>(moves).containsAll(winningMoves)) {
                            return true;
                        }
                    }

                case "vertical":
                    counter = 0;
                    for(int i : moves) {
                        winningMoves.clear();
                        placeholder = (int) (Math.pow(gridSize, 2) - 1) - (gridSize - 1) + (i % gridSize);
                        System.out.println(STR."Placeholder vert: \{placeholder}");
                        for(int j = 0; j < gridSize; j++) {
                            winningMoves.add(placeholder - counter);
                            counter += gridSize;
                        }
                        if(new HashSet<>(moves).containsAll(winningMoves)) {
                            return true;
                        }
                    }

                case "diagonal_left":
                    counter = 0;
                    for(int _ : moves) {
                        winningMoves.clear();
                        placeholder = (int) Math.pow(gridSize, 2) - 1;
                        System.out.println(STR."placeholder diag left: \{placeholder}");
                        for(int j = 0; j < gridSize; j++) {
                            winningMoves.add(placeholder - counter);
                            counter += gridSize + 1;
                        }
                        if(new HashSet<>(moves).containsAll(winningMoves)) {
                            return true;
                        }
                    }

                case "diagonal_right":
                    counter = 0;
                    for(int _ : moves) {
                        winningMoves.clear();
                        placeholder = (int) (Math.pow(gridSize, 2) - 1) - gridSize;
                        for(int j = 0; j < gridSize; j++) {
                            winningMoves.add(placeholder - counter);
                            counter += gridSize -1;
                        }
                        if(new HashSet<>(moves).containsAll(winningMoves)) {
                            return true;
                        }
                    }
            }

            return false;
        } //Checks if the player got all in a row or column.
    }

    public static class Player {
       static String player1Name; //Name of player 1.
        static String player2Name; //Name of player 2.
        char player1Letter = 'X'; //Player 1's letter.
        char player2Letter = 'O'; //Player 2's letter.
        List <Integer> player1Moves = new ArrayList<>(), player2Moves = new ArrayList<>(); //Player 1 and 2's lists, plus the AI's list.
        public Player(String player1Name, String player2Name) { //Initializes a new Player with the names of both Player 1 and 2.
            Player.player1Name = player1Name;
            Player.player2Name = player2Name;
        }
    }

    public static class GUI extends Objects {
        int gridSize; //Reference to the size of the board.
        static final HashMap<JButton, Integer> buttonMap = new HashMap<>(); //Link between the Buttons on board and certain board numbers.
        private final AI ai = new AI();
        JLabel titleLabel = new JLabel(STR."<html>It's \{Player.player1Name}'s <br> turn to go.</html>"); //The label that shows the player's what's happening in the game.
        JButton startPauseButton = new JButton("00:00");
        JButton restartButton = new JButton("Restart");
        boolean aiEnabled = true;
        public GUI(int gridSize) { //Initializes new GUI with the sze of the board.
           this.gridSize = gridSize;
        }

        public ActionListener getButtonListener() { //Runs this code when a button on the tic-tac-toe board is pressed.
            return e -> {
                JButton button = (JButton) e.getSource();
                System.out.println(STR."Button num: \{buttonMap.get(button)}");
                if(button.isEnabled()) {
                    if(player.player1Moves.size() > player.player2Moves.size() && !aiEnabled) {
                        player.player2Moves.add(buttonMap.get(button));
                        System.out.println(STR."Added to player2's list: \{player.player2Moves}");
                        button.setEnabled(false);
                        button.setText(Character.toString(player.player2Letter));

                        titleLabel.setText(STR."<html>It's \{Player.player1Name}'s turn to go.</html>");
                    } else {
                        player.player1Moves.add(buttonMap.get(button));
                        System.out.println(STR."Added to player1's list: \{player.player1Moves}");
                        button.setEnabled(false);
                        button.setText(Character.toString(player.player1Letter));

                        if(aiEnabled) {
                            ai.runRegularAI();
                        } else {
                            titleLabel.setText(STR."<html>It's \{Player.player2Name}'s turn to go.</html>");
                        }
                    }
                }

                if(board.checkForWinner(player.player1Moves)) {
                        titleLabel.setText(STR."<html>Congratulations. <br> \{Player.player1Name} has won.</html>");
                        for(JButton b : buttonMap.keySet()) {
                            b.setEnabled(false);
                        }
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                System.exit(0);
                            }
                        }, 4000);
                } else if(board.checkForWinner(player.player2Moves)) {
                        titleLabel.setText(STR."<html>Congratulations. <br> \{Player.player2Name} has won.</html>");
                        for(JButton b : buttonMap.keySet()) {
                            b.setEnabled(false);
                        }
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            System.exit(0);
                        }
                    }, 4000);
                }
            };
        }

        public ActionListener getRestartButtonListener() {
            return _ -> {
                for(JButton button : buttonMap.keySet()) {
                    button.setEnabled(true);
                    button.setText("");
                }
                player.player1Moves.clear();
                player.player2Moves.clear();
                titleLabel.setText(STR."<html>It's \{Player.player1Name}'s turn to go.</html>");
                restartButton.setText("00:00");
            };
        }
        public void createGUI(int gridSize) { //Runs the programmed GUI and creates the layout for the board, the labels and initializes all the GUI components.
            JFrame frame = new JFrame();
            frame.setSize(new Dimension(1000, 1000));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(null);

            JPanel buttonPanel = new JPanel(new GridLayout(gridSize, gridSize, 10, 10));
            buttonPanel.setBounds(350, 0, 800, 770);

            titleLabel.setBounds(30, 0, 250, 200);
            titleLabel.setBackground(Color.GRAY);
            titleLabel.setOpaque(true);
            titleLabel.setForeground(Color.BLACK);
            titleLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 20));
            titleLabel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            startPauseButton.setBounds(30, 80, 250, 200);


            restartButton.setBounds(30, 120, 250, 200);
            restartButton.addActionListener(getRestartButtonListener());

            for(int i = 0; i < Math.pow(gridSize, 2); i++) {
                System.out.println(i);
                JButton button = new JButton();
                button.setFont(new Font("JetBrains Mono", Font.PLAIN, 100));
                button.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK));
                button.setBackground(Color.GRAY);
                button.setForeground(Color.BLACK);
                button.addActionListener(getButtonListener());
                buttonMap.put(button, i);
                buttonPanel.add(button);
            }

            frame.add(titleLabel);
            frame.add(buttonPanel);
            frame.setVisible(true);
        }
    }

    public static class AI extends Objects {
        public void runRegularAI() {
            System.out.println("ran AI");
            List <Integer> availableButtons = new ArrayList<>();
            for(JButton button : GUI.buttonMap.keySet()) {
                if(button.isEnabled()) {
                    availableButtons.add(GUI.buttonMap.get(button));
                }
            }

            if(availableButtons.isEmpty()) {
                gui.titleLabel.setText("<html>No more spaces available. Restart or end game.</html>");
                return;
            }

            double randomMove = Math.random();
            int move = (int) (randomMove * availableButtons.size());
            int selectedButtonNumber = availableButtons.get(move);

            for(JButton button : GUI.buttonMap.keySet()) {
                if(GUI.buttonMap.get(button) == selectedButtonNumber && button.isEnabled()) {
                    button.setText(Character.toString(player.player2Letter));
                    button.setEnabled(false);
                    player.player2Moves.add(GUI.buttonMap.get(button));
                    break;
                }
            }
        }

    }

    public void run(String player1Name, String player2Name, int gridSize, boolean aiEnabled) {
        new Objects(player1Name, player2Name, gridSize);
        Objects.gui.createGUI(gridSize);
        Objects.gui.aiEnabled = aiEnabled;
    }

    public static void main(String[] args) {
        new TicTacToe().run("Brayden", "Jamie", 5, true);
    }
}

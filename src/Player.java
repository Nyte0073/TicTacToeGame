import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {
    static Scanner sc = new Scanner(System.in);
    static List <Integer> player1Moves = new ArrayList<>();
    static List <Integer> player2Moves = new ArrayList<>();
    static String player1Name;
    static String player2Name;
    static String player1Letter;
    static String player2Letter;
    Board board = new Board();

    public void getPlayer1Name() {
        System.out.println("Player 1 - Enter name.");
        player1Name = sc.nextLine();
    }

    public void getPlayer2Name() {
        System.out.println("Player 2 - Enter name.");
        player2Name = sc.nextLine();
    }

    public void getPlayer1Letter() {
        System.out.println("Player 1 - Enter letter.");
        player1Letter = sc.nextLine();
    }

    public void getPlayer2Letter() {
        System.out.println("Player 2 - Enter letter.");
        player2Letter = sc.nextLine();
    }

    public void getGridSize() {
        System.out.println("Enter grid size.");
        Board.gridSize = sc.nextInt();
    }
}

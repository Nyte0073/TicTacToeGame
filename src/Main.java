import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main {
    public Map<String, String> firstChar(String[] strings) {
        ArrayList <Character> characters = new ArrayList<>();
        Map <String, String> map = new HashMap<>();
        char character;

        for(String str : strings) {
            character = str.toLowerCase().charAt(0);
            if(!contains(characters, character)) {
                characters.add(character);
            }
        }

        for(char c : characters) {
            map.put(Character.toString(c), add(c, strings));
        }

        return map;
    }

    public boolean contains(ArrayList <Character> list, char character) {
        for(char c : list) {
            if(Objects.equals(c, character)) {
                return true;
            }
        }
        return false;
    }

    public String add(char c, String[] strings) {

        StringBuilder builder = new StringBuilder();

        for(String str : strings) {
            if(Objects.equals(str.toLowerCase().charAt(0), c)) {
                builder.append(str);
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        String[] strings = {"Apple", "Tea", "Soda", "Toast"};
        Main main = new Main();

        Map <String, String> map = main.firstChar(strings);

        System.out.println(map);
    }
}

class TestingTicTacToe {
    public static void main(String[] args) {
        Board board = new Board();
       Player.player1Moves.add(1);
       Player.player1Moves.add(6);
       Player.player1Moves.add(11);
       Player.player1Moves.add(16);
       Board.gridSize = 4;
       board.boardNumber = 11;
       int contains = 0;
       board.rightAmount = Board.gridSize;

       System.out.println(board.hasAllInDiagonalForwardLine(Player.player1Moves, board.boardNumber));

    }
}
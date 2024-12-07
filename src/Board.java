
import java.util.List;

public class Board {
    int boardNumber;
    int aiNumber;
    static int gridSize = 0;
    int rightAmount;
    boolean playerOneWon = false;
    boolean playerTwoWon = false;

    public boolean checkBoardSpace(int boardNumber) {
        return Player.player1Moves.contains(boardNumber) || Player.player2Moves.contains(boardNumber);
    }

    public boolean checkForWinner(String player) {
        rightAmount = gridSize;

        boolean winner = false;

        switch(player) {
            case "player1":
              if(contains(Player.player1Moves, boardNumber)) {
                  playerOneWon = true;
                  winner = true;
              }
              break;

            case "player2":
                if(contains(Player.player2Moves, GUI.aiEnabled ? aiNumber : boardNumber)) {
                    playerTwoWon = true;
                    winner = true;
                }
                break;
        }

        return winner;
    }

    public boolean contains(List <Integer> list, int number) {

        for (int i = 0; i < 3; i++) {
            if(hasAll(list, number, i + 1)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAll(List <Integer> list, int boardNumber, int phase) {
        boolean hasAll = false;

        switch(phase) {
            case 1:
                if(hasAllInDiagonalForwardLine(list, boardNumber)) {
                    hasAll = true;
                } else if(hasAllInBackWardDiagonalLine(list, boardNumber)) {
                    hasAll = true;
                } else if(hasAllInReversedBackwardDiagonalLine(list, boardNumber)) {
                    hasAll = true;
                }
                break;

            case 2:
                if(hasAllInUpwardsVerticalLine(list, boardNumber)) {
                  hasAll = true;
                } else if(hasAllInDownwardsVerticalLine(list, boardNumber)) {
                    hasAll = true;
                } else if(hasAllInReversedForwardDiagonalLine(list, boardNumber)) {
                    hasAll = true;
                }
                break;

            case 3:
                if(hasAllInForwardHorizontalLine(list, boardNumber)) {
                   hasAll = true;
                } else if(hasAllInBackwardsHorizontalLine(list, boardNumber)) {
                    hasAll = true;
                }
                break;
        }

        return hasAll;
    }

    public boolean hasAllInDiagonalForwardLine(List <Integer> list, int currentNumber) { //Done
        int contains = 0;

        for (int i = 0; i < gridSize; i++) {
            if(currentNumber - (gridSize + 1) >= 0) {
                currentNumber -= gridSize + 1;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if(list.contains(currentNumber)) {
                contains++;
                currentNumber += gridSize + 1;
            }
        }

        return contains == rightAmount;
    }

    public boolean hasAllInBackWardDiagonalLine(List <Integer> list, int currentNumber) { //Done
        int contains = 0;

        for (int i = 0; i < gridSize; i++) {
            if(currentNumber + (gridSize + 1) <= Math.pow(gridSize, 2)) {
                currentNumber += gridSize + 1;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if(list.contains(currentNumber)) {
                contains++;
                currentNumber -= gridSize + 1;
            }
        }

        return contains == rightAmount;
    }

    public boolean hasAllInUpwardsVerticalLine(List <Integer> list, int currentNumber) { //Done
        int contains = 0;

        for (int i = 0; i < gridSize; i++) {
            if((currentNumber + gridSize) <= Math.pow(gridSize, 2)) {
                currentNumber += gridSize;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if(list.contains(currentNumber)) {
                contains++;
                currentNumber -= gridSize;
            }
        }

        return contains == rightAmount;
    }

    public boolean hasAllInDownwardsVerticalLine(List <Integer> list, int currentNumber) { //Done
        int contains = 0;

        for (int i = 0; i < gridSize; i++) {
            if((currentNumber - gridSize) >= 0) {
                currentNumber -= gridSize;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if (list.contains(currentNumber)) {
                contains++;
                currentNumber += gridSize;
            }
        }

        return contains == rightAmount;
    }

    public boolean hasAllInForwardHorizontalLine(List <Integer> list, int currentNumber) { //Done
        int contains = 0;

        int closestNum = findClosestLeftNum(currentNumber);


        for (int i = 0; i < gridSize; i++) {
            if((currentNumber - 1) >= closestNum) {
                currentNumber -= 1;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if(list.contains(currentNumber)) {
                contains++;
                currentNumber += 1;
            }
        }

        return contains == rightAmount;
    }

    public boolean hasAllInBackwardsHorizontalLine(List <Integer> list, int currentNumber) {
        int contains = 0;

        int closestNum = findClosestRightNum(currentNumber);

        for (int i = 0; i < gridSize; i++) {
            if((currentNumber + 1) <= closestNum) {
                currentNumber += 1;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if(list.contains(currentNumber)) {
                currentNumber -= 1;
            }
        }

        return contains == rightAmount;
    }

    public boolean hasAllInReversedForwardDiagonalLine(List <Integer> list, int currentNumber) {
        int contains = 0;

        for (int i = 0; i < gridSize; i++) {
            if(currentNumber + (gridSize - 1) <= (Math.pow(gridSize, 2) - (gridSize - 1))) {
                currentNumber += gridSize + 1;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if(list.contains(currentNumber)) {
                contains++;
                currentNumber -= gridSize - 1;
            }
        }

        return contains == rightAmount;
    }

    public boolean hasAllInReversedBackwardDiagonalLine(List <Integer> list, int currentNumber) {
        int contains = 0;

        for (int i = 0; i < gridSize; i++) {
            if(currentNumber - (gridSize - 1) >= gridSize) {
                currentNumber -= gridSize - 1;
            } else {
                break;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if(list.contains(currentNumber)) {
                contains++;
                currentNumber += gridSize - 1;
            }
        }

        return contains == rightAmount;
    }

    public int findClosestLeftNum(int num) {
        boolean bool = false;
        int closestNum = 1;

        while(!bool) {
            closestNum += gridSize;
            if(num >= closestNum && num <= (closestNum + gridSize)) {
                bool = true;
            }
        }
        return closestNum == 0 ? 1 : closestNum;
    }

    public int findClosestRightNum(int num) {
        boolean bool = false;
        int closestNum = 1;

        while(!bool) {
            if(closestNum == 1) {
                closestNum += gridSize - 1;
            } else {
                closestNum += gridSize;
            }

            if(num <= closestNum && num >= closestNum - (gridSize - 1)) {
                bool = true;
            }
        }

        return closestNum;
    }

}

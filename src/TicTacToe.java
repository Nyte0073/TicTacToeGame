public class TicTacToe {
    Player player = new Player();
    GUI gui = new GUI();
    public void run() throws Exception {
        player.getGridSize();
        Player.sc.nextLine();
        player.getPlayer1Name();
//        Player.sc.nextLine();
        player.getPlayer2Name();
//        Player.sc.nextLine();
        player.getPlayer1Letter();
//        Player.sc.nextLine();
        player.getPlayer2Letter();

        if(Player.player2Name.toLowerCase().contains("ai")) {
            GUI.aiEnabled = true;
        }

        System.out.println(GUI.aiEnabled);

        gui.createBaseGUI();
    }

    public static void main(String[] args) throws Exception {
        TicTacToe ticTacToe = new TicTacToe();
        ticTacToe.run();
    }
}

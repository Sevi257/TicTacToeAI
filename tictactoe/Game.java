package pgdp.tictactoe;

import pgdp.tictactoe.ai.CompetitionAI;
import pgdp.tictactoe.ai.HumanPlayer;
import pgdp.tictactoe.ai.SimpleAI;

import java.util.Arrays;

public class Game {
    private PenguAI firstPlayer;
    private PenguAI secondPlayer;
    private PenguAI winner = null;
    public Field[][] board = new Field[3][3];
    private boolean[] firstPlayedPieces = new boolean[9];
    private boolean[] secondPlayedPieces = new boolean[9];
    public Game(PenguAI first, PenguAI second) {
        firstPlayer = first;
        secondPlayer = second;
    }

    public PenguAI getWinner() {
        if(winner == null){
            return null;
        }
        else{
            return winner;
        }
    }

    public void playGame() {
        //firstPlayerMove
        for(int i = 0;i<9;i++){
            if (oneMove(true,firstPlayer,firstPlayedPieces)){
                if(winner==null){
                    winner = firstPlayer;
                }
                return;
            }
            if(oneMove(false,secondPlayer,secondPlayedPieces)){
                if(winner==null){
                    winner = secondPlayer;
                }
                return;
            }
        }
    }
    public static boolean checkForWin(Field[][] board, boolean firstPlayer,boolean[] pieces){
        //Check vertical
        boolean noWin = true;
        for(int i = 0; i<3;i++){
            noWin = false;
            for(int j = 0; j<3;j++){
                if(board[j][i]!=null) {
                    if (board[j][i].firstPlayer() != firstPlayer) {
                        noWin = true;
                        break;
                    }
                }
                else {
                    noWin = true;
                    break;
                }
            }
            if(!noWin){
                return true;
            }
        }
        //Check horizontal
        for(int i = 0; i<3;i++){
            noWin = false;
            for(int j = 0; j<3;j++){
                if(board[i][j]!=null) {
                    if (board[i][j].firstPlayer() != firstPlayer) {
                        noWin = true;
                        break;
                    }
                }
                else {
                    noWin = true;
                    break;
                }
            }
            if(!noWin){
                return true;
            }
        }
        //Check diagonal
        for(int k = 0; k<3;k++) {
            noWin = false;
            if(board[k][k]!=null) {
                if (board[k][k].firstPlayer() != firstPlayer) {
                    noWin = true;
                    break;
                }
            }
            else {
                noWin = true;
                break;
            }
        }
        if(!noWin){
            return true;
        }
        int count = 0;
        for (int i = 2;i>=0;i--){
            noWin = false;
            if(board[count][i]!=null) {
                if (board[count++][i].firstPlayer() != firstPlayer) {
                    noWin = true;
                    break;
                }
            }
            else {
                noWin = true;
                break;
            }
        }
        if(noWin == false){
            return true;
        }
        int countPieces = 0;
        for(int i = 0;i<pieces.length;i++){
            if(!pieces[i]){
                return false;
            }
        }
        for(int i = 0;i<3;i++) C:{
            for (int j = 0; j < 3; j++) {
                noWin = false;
                if(board[i][j]==null){
                    noWin = true;
                    break C;
                }
            }
        }
        if(!noWin){
            return true;
        }
        return false;
    }
    private boolean oneMove(boolean isFirst, PenguAI player, boolean[] pieces){
        C: for(int i = 0;i<3;i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println(i + " " + j);
                if(board[i][j]==null){
                    break C;
                }
                if(board[i][j].firstPlayer()!=isFirst){
                    for(int k = 0;k< pieces.length;k++){
                        if(!pieces[k]&&k>board[i][j].value()){
                            break C;
                        }
                    }
                }
            }
            System.out.println(i);
            if(i == 2){
                if(isFirst){
                    winner = secondPlayer;
                }
                else {
                    winner = firstPlayer;
                }
                System.out.println("Brakes here");
                return true;
            }
        }

        if(!checkForWin(board,isFirst,pieces)) {
            Move firstP = player.makeMove(board, isFirst, firstPlayedPieces, secondPlayedPieces);
            int x = firstP.x();
            int y = firstP.y();
            //Wenn er ein noch nicht belegtes Feld nimmt
            if (x >= board.length || y >= board[0].length || x < 0 || y < 0 || firstP.value() > 8 || firstP.value() < 0) {
                if (player == firstPlayer) {
                    winner = secondPlayer;
                } else {
                    winner = firstPlayer;
                }
                return true;
            }
            if (board[x][y] != null) {
                //Wenn entweder sein Stein da liegt oder ein höherer Wert
                if (board[x][y].firstPlayer() == isFirst || (!board[x][y].firstPlayer() == isFirst && board[x][y].value() >= firstP.value())) {
                    if (player == firstPlayer) {
                        winner = secondPlayer;
                    } else {
                        winner = firstPlayer;
                    }
                    return true;
                }
            }

            //WEnn er den Stein schon gespielt hat
            if (pieces[firstP.value()]) {
                if (player == firstPlayer) {
                    winner = secondPlayer;
                } else {
                    winner = firstPlayer;
                }
                return true;
            }
            pieces[firstP.value()] = true;
            Field newMove = new Field(firstP.value(), isFirst);
            board[x][y] = newMove;
        }
        return checkForWin(board,isFirst,pieces);
    }

    public static void printBoard(Field[][] board) {
        System.out.println("┏━━━┳━━━┳━━━┓");
        for (int y = 0; y < board.length; y++) {
            System.out.print("┃");
            for (int x = 0; x < board.length; x++) {
                if (board[x][y] != null) {
                    System.out.print(board[x][y] + "┃");
                } else {
                    System.out.print("   ┃");
                }
            }
            System.out.println();
            if (y != board.length - 1) {
                System.out.println("┣━━━╋━━━╋━━━┫");
            }
        }
        System.out.println("┗━━━┻━━━┻━━━┛");
    }

    public static void main(String[] args) {

        PenguAI firstPlayer = new SimpleAI();
        PenguAI secondPlayer = new HumanPlayer();
         Game game = new Game(firstPlayer, secondPlayer);
            game.playGame();
            if (firstPlayer == game.getWinner()) {
                System.out.println("Herzlichen Glückwunsch erster Spieler");
            } else if (secondPlayer == game.getWinner()) {
                System.out.println("Herzlichen Glückwunsch zweiter Spieler");
            } else {
                System.out.println("Unentschieden");
            }
    }
}

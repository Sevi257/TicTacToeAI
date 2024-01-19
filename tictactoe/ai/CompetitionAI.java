package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

import java.util.*;

public class CompetitionAI extends PenguAI {
    private Random random;
    private String winner;
    private String firstPlayerIn = "firstPlayerIn";
    private String secondPlayerIn = "secondPlayerIn";
    private String tied = "tied";
    private int valueOfFirst = 0;
    private int valueOfSecond = 0;

    public CompetitionAI() {
        random = new Random();
        winner = null;
    }

    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces, boolean[] secondPlayedPieces) {
        //TODO make viable for isSecondPlayer
        //TODO macht teils invalid Züge
        //Findet nicht winning oder losing Züge vllt alpha beta pruning falsch?
        //Bestrafen für mehr Züge zum Sieg
        //legt nicht auf null Felder zum Sieg sondern belegt lieber andere (vllt Belohnung für viel am Spielbrett)?
        //macht immer einen Zug zu viel bevor Sieg -> Depth 4
        //Bei Depth 3 mach er das nicht Fehler bei Min Max
        //Macht bei tieferer Depth teilweise keinen sinnvollen Move
        boolean[] pieces;
        boolean[] oppPieces;
        int bestScore = Integer.MIN_VALUE;
        int score;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        //AI maximiert
        int depth = 2;
        Move keep = new Move(0,0,0);
        if(firstPlayer){
            pieces = firstPlayedPieces;
            oppPieces = secondPlayedPieces;
        }
        else {
            pieces = secondPlayedPieces;
            oppPieces = firstPlayedPieces;
        }
        for(int i = 0;i<3;i++){
            for(int j=0;j<3;j++){
                for(int k = 0;k<9;k++){
                    Move test = new Move(i,j,k);
                    if(isValidMoveForAI(test.x(),test.y(),board,test,firstPlayer,pieces)){
                        Field temp = new Field(test.value(), firstPlayer);
                        Field save = board[i][j];
                        board[i][j] = temp;
                        //if(firstPlayer) {
                            score = minimax(board, depth, !firstPlayer, firstPlayedPieces, secondPlayedPieces, alpha, beta);
                        /*}
                        else {
                            score = minimax(board, depth, firstPlayer, firstPlayedPieces, secondPlayedPieces, alpha, beta);
                        }*/
                        board[i][j] = save;
                        if(score>bestScore){
                            bestScore = score;
                            keep = new Move(i,j,k);
                        }
                    }
                }
            }
        }
        System.out.println(keep);
        return keep;
    }
    //firstPlayer X second Player O -> X is Maximizing -> Winning +++++ little Value tile +++
    //if result != null ->
    //Bestrafen für zu viele Züge
    Map<String,Integer> scores = Map.ofEntries(
            Map.entry("valueOfFirst",valueOfFirst*-1),
            Map.entry("valueOfSecond",valueOfSecond),
            Map.entry("firstPlayerIn",100000),
            Map.entry("secondPlayerIn",-100000),
            Map.entry("tied",0)
    );
    public int minimax(Field[][] board, int depth, boolean isMaximizing,boolean[] firstPieces, boolean[] secondPieces, int alpha,int beta){
        boolean[] pieces;
        boolean[] oppPieces;
        if(isMaximizing){
            pieces = firstPieces;
            oppPieces = secondPieces;
        }
        else {
            pieces = secondPieces;
            oppPieces = firstPieces;
        }
        //GesamtAnzahl der Teile berechnen und mitreinrechnen -> Auf depth begrenzen wenn noch kein Sieger dann benutzte Teile zählen
        if(checkForWin(board,isMaximizing,pieces,oppPieces)){
            return scores.get(winner);
        }
        if(checkForWin(board,!isMaximizing,pieces,oppPieces)){
            return scores.get(winner);
        }
        if(depth==0){
            //calculate all visible Pieces
            valueOfFirst = 0;
            valueOfSecond = 0;
            for(int i = 0;i<3;i++){
                for(int j = 0;j<3;j++){
                    if(board[i][j]!=null) {
                        if (board[i][j].firstPlayer() == isMaximizing) {
                            valueOfFirst += board[i][j].value();
                        } else {
                            valueOfSecond += board[i][j].value();
                        }
                    }
                }
            }
            if(isMaximizing){
                return scores.get("valueOfFirst");
            }
            else {
                return scores.get("valueOfSecond");
            }
        }
        int bestScore;
        if(isMaximizing){
            bestScore = Integer.MIN_VALUE;
            for(int i = 0;i<3;i++) C:{
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 9; k++) {
                        Move neuerMove = new Move(i,j,k);
                        if(isValidMoveForAI(i,j,board,neuerMove,true,pieces)) {
                            Field neuesField = new Field(k,true);
                            Field save = board[i][j];
                            board[i][j] = neuesField;
                            int score = minimax(board,depth-1,false,pieces,oppPieces,alpha,beta);
                            board[i][j] = save;
                            if(score>bestScore){
                                bestScore = score;
                            }
                            if(score>alpha){
                                alpha = score;
                            }
                            if(beta<=alpha){
                                break C;
                            }
                        }
                    }
                }
            }
        }
        else {
            bestScore = Integer.MAX_VALUE;
            for(int i = 0;i<3;i++) C:{
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 9; k++) {
                        Move neuerMove = new Move(i,j,k);
                        if(isValidMoveForAI(i,j,board,neuerMove,false,oppPieces)) {
                            Field neuesField = new Field(k,false);
                            Field save = board[i][j];
                            board[i][j] = neuesField;
                            int score = minimax(board,depth-1,true,pieces,oppPieces,alpha,beta);
                            board[i][j] = save;
                            if(score<bestScore){
                                bestScore = score;
                            }
                            if(score<beta){
                                beta = score;
                            }
                            if(beta<=alpha){
                                break C;
                            }
                        }
                    }
                }
            }
        }
        return bestScore;
    }
    public boolean checkForWin(Field[][] board, boolean firstPlayer, boolean[] pieces, boolean[] oppPieces){
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
                if (firstPlayer){
                    winner = firstPlayerIn;
                }
                else {
                    winner = secondPlayerIn;
                }

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
                if (firstPlayer){
                    winner = firstPlayerIn;
                }
                else {
                    winner = secondPlayerIn;
                }
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
            if (firstPlayer){
                winner = firstPlayerIn;
            }
            else {
                winner = secondPlayerIn;
            }
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
        if(!noWin){
            if (firstPlayer){
                winner = firstPlayerIn;
            }
            else {
                winner = secondPlayerIn;
            }

            return true;
        }
        int countPieces = 0;
        for(int i = 0;i<pieces.length;i++){
            if(!pieces[i]){
                return false;
            }
        }
        for (int i = 0;i<oppPieces.length;i++){
            if(!pieces[i]){
                return false;
            }
        }
        for(int i = 0;i<3;i++){
            for(int j = 0;j<3;j++){
                assert board[i][j] != null;
                if(board[i][j].firstPlayer()==firstPlayer){
                    valueOfFirst += board[i][j].value();
                }
                else {
                    valueOfSecond += board[i][j].value();
                }
            }
        }
        if(valueOfFirst!=valueOfSecond){
            if(valueOfFirst>valueOfSecond){
                winner = secondPlayerIn;
            }
            else {
                winner = firstPlayerIn;
            }

            return true;
        }
        winner = tied;
        return true;
    }
    public boolean isValidMoveForAI(int x, int y, Field[][] board, Move test, boolean isFirst, boolean[] pieces){
        //Im Feld
        if(x >= board.length || y >= board[0].length || x < 0 || y < 0 || test.value() > 8 || test.value() < 0){
            return false;
        }
        //
        if (board[x][y] != null) {
            //Wenn entweder sein Stein da liegt oder ein höherer Wert
            if (board[x][y].firstPlayer() == isFirst || (!board[x][y].firstPlayer() == isFirst && board[x][y].value() >= test.value())) {
                return false;
            }
        }
        if(pieces[test.value()]){
            return false;
        }
        return true;
    }
}

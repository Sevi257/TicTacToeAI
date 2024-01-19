package pgdp.tictactoe.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

public class SimpleAI extends PenguAI {

    private Random random;

    public SimpleAI() {
        random = new Random();
    }

    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
            boolean[] secondPlayedPieces) {
        boolean[] pieces;
        boolean[] otherPieces;
        if(firstPlayer){
            otherPieces = secondPlayedPieces;
            pieces = firstPlayedPieces;
        }
        else {
            otherPieces = firstPlayedPieces;
            pieces = secondPlayedPieces;
        }
        //Check for winning move
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < firstPlayedPieces.length; k++) {
                    Move test = new Move(i, j, k);
                    if (isValidMoveForAI(test.x(), test.y(), board, test, firstPlayer,pieces)) {
                        Field save = board[i][j];
                        Field testMove = new Field(test.value(), firstPlayer);
                        board[i][j] = testMove;
                        if (Game.checkForWin(board, firstPlayer,pieces)) {
                            for (int u = 8; u >= 0; u--) {
                                if (!pieces[u]) {
                                    board[i][j] = save;
                                    return new Move(test.x(), test.y(), u);
                                }
                            }
                        }
                        else {
                            board[i][j] = save;
                        }
                    }
                }
            }
        }
        //Check for not losing move (Winning Move for Opponent and prevent it
        ArrayList<Move> winningMovesforOpp = new ArrayList<>();
        HashMap<Integer,Move> preventingWin = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < pieces.length; k++) {
                    Move test = new Move(i, j, k);
                    //tests with the wrong pieces
                    if (isValidMoveForAI(test.x(), test.y(), board, test, !firstPlayer,otherPieces)) {
                        Field save = board[i][j];
                        Field testMove = new Field(test.value(), !firstPlayer);
                        board[i][j] = testMove;
                        if (Game.checkForWin(board, !firstPlayer,pieces)) {
                            board[i][j] = save;
                            winningMovesforOpp.add(test);
                        }
                        board[i][j] = save;
                    }
                }
            }
        }
        System.out.println(winningMovesforOpp);

        //gibt es einen Zug der alle verhindert?
        int count = 0;
        if(winningMovesforOpp.size()>0){
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        for (int k = 0; k < pieces.length; k++) {
                            Move counter = new Move(i,j,k);
                            if(isValidMoveForAI(counter.x(),counter.y(),board,counter,firstPlayer,pieces)){
                                    for(int l = 0 ; l< winningMovesforOpp.size();l++){
                                        Field prev = board[counter.x()][counter.y()];
                                        Move moveTry = winningMovesforOpp.get(l);
                                        Field fieldTry = new Field(moveTry.value(), !firstPlayer);
                                        Field saveTry = board[moveTry.x()][moveTry.y()];
                                        Field counterField = new Field(counter.value(), firstPlayer);
                                        board[moveTry.x()][moveTry.y()] = fieldTry;
                                        if(isValidMoveForAI(counter.x(),counter.y(),board,counter,firstPlayer,pieces)) {
                                            board[counter.x()][counter.y()] = counterField;
                                        }
                                        //Gegner hat seinen Stein platziert und KI auch
                                        if(!Game.checkForWin(board,!firstPlayer,pieces)){
                                            count++;
                                        }
                                        board[moveTry.x()][moveTry.y()] = saveTry;
                                        board[counter.x()][counter.y()] = prev;
                                    }
                                    if(count>0) {
                                        preventingWin.put(count, counter);
                                    }
                                    count = 0;
                            }
                        }
                    }
                }
            System.out.println(preventingWin.entrySet());
                if(preventingWin.size()>0){
                int max =  preventingWin.keySet().stream().mapToInt(move -> move).max().getAsInt();
                return preventingWin.get(max);
                }
        }
            //Make random Move in Ruleset
            Move rand = new Move(random.nextInt(board.length), random.nextInt(board.length),
                    random.nextInt(firstPlayedPieces.length));
            for (int i = 0; i < 500 && !isValidMoveForAI(rand.x(), rand.y(), board, rand, firstPlayer, pieces); i++) {
                rand = new Move(random.nextInt(board.length), random.nextInt(board.length),
                        random.nextInt(firstPlayedPieces.length));
            }
            return rand;
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

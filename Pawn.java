package com.example.f.chess;

import java.util.ArrayList;

/**
 * Created by F on 7/31/2017.
 */
public class Pawn extends Piece {
    private boolean justDoubleMoved = false;
    private int moveCount;

    public Pawn (boolean color) {
        super(color);
    }

    public int[][] getMoveList (int let, int num){
        ArrayList<int[]> potentialMoves = new ArrayList<int[]>();

        if(getColor()) {
            for (int i = 1; i < 3; i++) {
                if (num + i < 8) {
                    int[] validMove = {let, num + i};
                    potentialMoves.add(validMove);
                }
            }
            if(let + 1 < 8 && let + 1 >= 0 && num + 1 < 8 && num + 1 >= 0){
                int[] validMove = {let + 1, num + 1};
                potentialMoves.add(validMove);
            }
            if(let - 1 < 8 && let - 1 >= 0 && num + 1 < 8 && num + 1 >= 0){
                int[] validMove = {let - 1, num + 1};
                potentialMoves.add(validMove);
            }
        }
        else{
            for (int i = -1; i > -3; i--) {
                if (num + i >= 0) {
                    int[] validMove = {let, num + i};
                    potentialMoves.add(validMove);
                }
            }
            if(let + 1 < 8 && let + 1 >= 0 && num - 1 < 8 && num - 1 >= 0){
                int[] validMove = {let + 1, num - 1};
                potentialMoves.add(validMove);
            }
            if(let - 1 < 8 && let - 1 >= 0 && num - 1 < 8 && num - 1 >= 0){
                int[] validMove = {let - 1, num - 1};
                potentialMoves.add(validMove);
            }
        }

        int[][] legalMoves = new int[potentialMoves.size()][2];
        //Converts potential move ArrayList to an array
        for(int i = 0; i < legalMoves.length; i++){
            legalMoves[i] = potentialMoves.get(i);
        }
        return legalMoves;
    }

    public boolean isJustDoubleMoved(int moveCount) {
        if(moveCount - this.moveCount > 1){
            justDoubleMoved = false;
        }
        return justDoubleMoved;
    }

    public void setJustDoubleMoved(boolean bool, int moveCount){
        this.moveCount = moveCount;
        justDoubleMoved = bool;
    }

    public int getMoveCount(){
        return moveCount;
    }

    public char getType(){
        return 'P';
    }
}

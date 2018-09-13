package com.example.f.chess;

import java.util.ArrayList;

/**
 * Created by F on 7/31/2017.
 */
public class Rook extends Piece{
    private boolean hasMoved = false;

    public Rook (boolean color) {
        super(color);
    }

    public int[][] getMoveList (int let, int num){
        ArrayList<int[]> potentialMoves = new ArrayList<int[]>();

        for(int i = 0; i < 8; i++){
            if(i != let){
                int[] validMove = {i, num};
                potentialMoves.add(validMove);
            }
        }
        for(int i = 0; i < 8; i++){
            if(i != num){
                int[] validMove = {let, i};
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

    public boolean hasMoved(){
        return hasMoved;
    }

    public void setMoved(boolean hasMoved){
        this.hasMoved = hasMoved;
    }

    public char getType(){
        return 'R';
    }

}

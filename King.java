package com.example.f.chess;

import java.util.ArrayList;

/**
 * Created by F on 7/7/2017.
 */
public class King extends Piece {
    private boolean hasMoved;

    public King (boolean color) {
        super(color);
        hasMoved = false;
    }
    public int[][] getMoveList (int let, int num){
        ArrayList<int[]> potentialMoves = new ArrayList<int[]>();
        //Considers a 3x3 space
        for(int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                //eliminates the king's own position
                if(i != 0 || j != 0) {
                    int col = let + i;
                    int row = num + j;
                    //if the square is inbounds, add it to potential moves
                    if(row >= 0 && row < 8 && col >= 0 && col < 8){
                        int[] validMove = {col, row};
                        potentialMoves.add(validMove);
                    }

                }
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
        return 'K';
    }

}

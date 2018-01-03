package com.example.f.chess;

import java.util.ArrayList;

/**
 * Created by F on 7/7/2017.
 */
public class Knight extends Piece{
    boolean color;
    public Knight (boolean color){
        super(color);
    }
    public int[][] getMoveList (int let, int num){
        ArrayList<int[]> potentialMoves = new ArrayList<int[]>();

        //consider a 5x5 space
        for(int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                //eliminates all illegal hops
                if(i != 0 && j != 0 && i != j && (i + j) != 0) {
                    int col = let + i;
                    int row = num + j;

                    //if the move is in bounds, add it
                    if(row >= 0 && row < 8 && col >= 0 && col < 8){
                        int[] validMove = {col, row};
                        potentialMoves.add(validMove);
                    }

                }
            }
        }
        int[][] legalMoves = new int[potentialMoves.size()][2];

        //transfer to the 2D array
        for(int i = 0; i < legalMoves.length; i++){
            legalMoves[i] = potentialMoves.get(i);
        }
        return legalMoves;

    }

    public char getType(){
        return 'N';
    }

}

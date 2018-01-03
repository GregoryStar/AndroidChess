package com.example.f.chess;

import java.util.ArrayList;

/**
 * Created by F on 7/30/2017.
 */
public class Bishop extends Piece{
    boolean color;
    public Bishop (boolean color){
        super(color);
    }

    public int[][] getMoveList (int let, int num){
        ArrayList<int[]> potentialMoves = new ArrayList<int[]>();


        //Test all four branching diagonals and return valid squares
        int curX = let;
        int curY = num;
        while(curX >= 0 && curX < 8 && curY >= 0 && curY < 8){
            if(curX != let || curY != num){
                int[] validMove = {curX, curY};
                potentialMoves.add(validMove);
            }
            curX -= 1;
            curY -= 1;
        }
        curX = let;
        curY = num;
        while(curX >= 0 && curX < 8 && curY >= 0 && curY < 8){
            if(curX != let || curY != num){
                int[] validMove = {curX, curY};
                potentialMoves.add(validMove);
            }
            curX += 1;
            curY -= 1;
        }
        curX = let;
        curY = num;
        while(curX >= 0 && curX < 8 && curY >= 0 && curY < 8){
            if(curX != let || curY != num){
                int[] validMove = {curX, curY};
                potentialMoves.add(validMove);
            }
            curX += 1;
            curY += 1;
        }
        curX = let;
        curY = num;
        while(curX >= 0 && curX < 8 && curY >= 0 && curY < 8){
            if(curX != let || curY != num){
                int[] validMove = {curX, curY};
                potentialMoves.add(validMove);
            }
            curX -= 1;
            curY += 1;
        }

       int[][] legalMoves = new int[potentialMoves.size()][2];

        //transfer to the 2D array
        for(int i = 0; i < legalMoves.length; i++){
            legalMoves[i] = potentialMoves.get(i);
        }
        return legalMoves;

    }

    public char getType(){
        return 'B';
    }
}

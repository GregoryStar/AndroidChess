package com.example.f.chess;

import java.util.ArrayList;

/**
 * Created by F on 7/31/2017.
 */
public class Queen extends Piece{
    boolean color;

    public Queen (boolean color) {
        super(color);
    }

    public int[][] getMoveList(int let, int num){
        ArrayList<int[]> bishopMoves = getBishopMoves(let, num);
        ArrayList<int[]> rookMoves = getRookMoves(let, num);

        bishopMoves.addAll(rookMoves);

        int[][] legalMoves = new int[bishopMoves.size()][2];

        for(int i = 0; i < legalMoves.length; i++){
            legalMoves[i] = bishopMoves.get(i);
        }
        return legalMoves;
    }

    private ArrayList<int[]> getRookMoves (int let, int num){
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

        return potentialMoves;

    }
    private ArrayList<int[]> getBishopMoves (int let, int num){
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

        return potentialMoves;

    }

    public char getType(){
        return 'Q';
    }
}

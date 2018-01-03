package com.example.f.chess;

/**
 * Created by F on 7/6/2017.
 */
abstract class Piece {
    private boolean color;

    public Piece (){

    }
    public Piece (boolean color){
        this.color = color;
    }
    public int[][] getMoveList(int let, int num){
        return null;
    }

    public boolean getColor(){
        return color;
    }

    public char getType(){
        return '?';
    }
}

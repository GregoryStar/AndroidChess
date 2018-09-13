package com.example.f.chess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by F on 8/14/2017.
 */

public class VisualBoardManager implements View.OnClickListener {
    private List<Board> gamePositions = new ArrayList<Board>();
    private int currentMoveIndex;
    private int darkSquareColor = Color.GRAY;
    private int lightSquareColor = Color.WHITE;
    private int potentialMoveColor = Color.YELLOW;
    private int checkColor = Color.RED;
    private int previousMoveColor = Color.GREEN;
    private int[][] currentLegalMoves;
    private Bitmap whiteKnight;
    private Bitmap blackKnight;
    private Bitmap whitePawn;
    private Bitmap blackPawn;
    private Bitmap whiteBishop;
    private Bitmap blackBishop;
    private Bitmap whiteQueen;
    private Bitmap blackQueen;
    private Bitmap whiteRook;
    private Bitmap blackRook;
    private Bitmap whiteKing;
    private Bitmap blackKing;
    private int selectedSquareX = -1;
    private int selectedSquareY = -1;
    private Context context;
    ImageButton[][] squares = new ImageButton[8][8];


    public VisualBoardManager(LinearLayout chessboard, Context context){
        currentMoveIndex = 0;
        gamePositions.add(new Board());
        this.context = context;
        for(int i = 0; i < 8; i++){
            LinearLayout row = (LinearLayout)chessboard.getChildAt(i);
            for(int j = 0; j < 8; j++){
                ImageButton square = squares[j][7-i] = (ImageButton)row.getChildAt(j);
                square.setOnClickListener(this);
            }
        }
        loadPieceImages();
        updateBoard();
    }

    public void undo(){
        if(currentMoveIndex > 0){
            currentMoveIndex--;
            currentLegalMoves = null;
            updateBoard();
            selectedSquareX = -1;
            selectedSquareY = -1;
        }


    }

    public void redo(){
        if(currentMoveIndex < gamePositions.size() - 1){
            currentMoveIndex++;
            currentLegalMoves = null;
            updateBoard();
            selectedSquareX = -1;
            selectedSquareY = -1;
        }

    }

    public void queenPromote(){

    }


    private void gameOver(String boardState){
        new AlertDialog.Builder(context)
                .setTitle("Game Over")
                .setMessage(boardState + "!")
                .setCancelable(true)
                .setNegativeButton("Play Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gamePositions.clear();
                        gamePositions.add(new Board());
                        currentMoveIndex = 0;
                        updateBoard();
                    }
                })
                .setPositiveButton("Undo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        undo();
                        undo();
                    }
                })
                .show();
    }

    private void promotion(int x1, int y1){
        final Board currentBoard = gamePositions.get(currentMoveIndex); //Is the counter being incremented?
        final Piece piece = currentBoard.getBoard()[x1][y1];

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        PlayGameActivity a = (PlayGameActivity)context;
        LayoutInflater inflater = a.getLayoutInflater();
        LinearLayout alertLayout = (LinearLayout)inflater.inflate(R.layout.promotionselection, null);

        ImageButton queen = (ImageButton)alertLayout.getChildAt(0);
        ImageButton rook = (ImageButton)alertLayout.getChildAt(1);
        ImageButton bishop = (ImageButton)alertLayout.getChildAt(2);
        ImageButton knight = (ImageButton)alertLayout.getChildAt(3);

        builder.setView(alertLayout);
        builder.setTitle("Promotion");
        builder.setMessage("Choose a piece!");
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        queen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentBoard.promoteTo(new Queen(piece.getColor()));
                alertDialog.dismiss();
                updateBoard();
            }
        });
        rook.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentBoard.promoteTo(new Rook(piece.getColor()));
                alertDialog.dismiss();
                updateBoard();
            }
        });
        bishop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentBoard.promoteTo(new Bishop(piece.getColor()));
                alertDialog.dismiss();
                updateBoard();
            }
        });
        knight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentBoard.promoteTo(new Knight(piece.getColor()));
                alertDialog.dismiss();
                updateBoard();
            }
        });




    }



    @Override
    public void onClick(View square){
        //When one of the squares is clicked this is called
        int i = 0;
        int j = 0;

        //We locate the square in our ImageButton array
        while(squares[i][j] != square){
            if(i == 7){
                j++;
            }
            i = (i + 1) % 8;
        }

        //Once we know the index associated with that square we
        //can begin logical manipulations.
        processClick(i, j);

    }

    private void processClick(int x, int y){
        //Nothing was selected and you just selected a valid piece
        if(selectedSquareX < 0){
            //We only do something if the square you clicked on wasn't empty
            if(!gamePositions.get(currentMoveIndex).isSquareEmpty(x, y)) {
                selectedSquareX = x;
                selectedSquareY = y;
                currentLegalMoves = gamePositions.get(currentMoveIndex).getLegalMoves(x, y);
                updateBoard();
            }
        }
        else{
            //A piece is selected and you selected a square it can go to
            Board boardToAdd = gamePositions.get(currentMoveIndex).getBoardCopy();
            if(boardToAdd.attemptMove(selectedSquareX, selectedSquareY, x, y)){
                //The move is legal and has gone through; no need to current legal moves anymore
                currentLegalMoves = null;
                //If the current move index is equal to the move count (number of positions)
                if(currentMoveIndex + 1 == gamePositions.size()) {
                    gamePositions.add(boardToAdd);
                }
                else{
                    //Since the move we're making contradicts future moves, remove all future moves
                    //(Should check to make sure it actually contradicts but whatever, minor detail)
                    gamePositions = gamePositions.subList(0, currentMoveIndex + 1);
                    gamePositions.add(boardToAdd);
                }
                currentMoveIndex = gamePositions.size() - 1;

                String boardState = boardToAdd.getState();

                if(boardState.equals("Checkmate") || boardState.equals("Stalemate")){
                    gameOver(boardState);
                }
                else if(boardState.equals("Promotion")){
                    promotion(selectedSquareX, selectedSquareY);
                }

                updateBoard();

                //Reset the selected squares. Now nothing is selected.
                selectedSquareX = -1;
                selectedSquareY = -1;
            }
            //A piece is selected and you selected a square it can't go to
            else{
                selectedSquareX = x;
                selectedSquareY = y;

                //Get all the legal moves of the currently highlighted piece
                currentLegalMoves = gamePositions.get(currentMoveIndex).getLegalMoves(x, y);
                updateBoard();
            }
        }
    }



    private void loadPieceImages(){
        try {
            //Loads all of the piece images into conveniently titled variables.
            whiteKnight = BitmapFactory.decodeResource(context.getResources(), R.drawable.whiteknight);
            whiteKing = BitmapFactory.decodeResource(context.getResources(), R.drawable.whiteking);
            whiteQueen = BitmapFactory.decodeResource(context.getResources(), R.drawable.whitequeen);
            whiteBishop = BitmapFactory.decodeResource(context.getResources(), R.drawable.whitebishop);
            whiteRook = BitmapFactory.decodeResource(context.getResources(), R.drawable.whiterook);
            whitePawn = BitmapFactory.decodeResource(context.getResources(), R.drawable.whitepawn);
            blackKnight = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackknight);
            blackKing = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackking);
            blackQueen = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackqueen);
            blackBishop = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackbishop);
            blackRook = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackrook);
            blackPawn = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackpawn);
        }
        catch(Exception e){

        }
    }


    private void updateBoard(){
        //Gets the current position and draws it
        Piece[][] newSquares = gamePositions.get(currentMoveIndex).getBoard();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Piece currentPiece = newSquares[i][j];
                //We need to find out what piece is present on what
                //square and also set the square colors
                if((i + j) % 2 == 0){
                    squares[i][j].setBackgroundColor(darkSquareColor);
                }
                else{
                    squares[i][j].setBackgroundColor(lightSquareColor);
                }
                if(currentPiece == null){
                    squares[i][j].setImageBitmap(null);
                }
                else {
                    boolean color = currentPiece.getColor();
                    if (currentPiece.getType() == 'R' && color == false) {
                        squares[i][j].setImageBitmap(blackRook);
                    } else if (currentPiece.getType() == 'R' && color == true) {
                        squares[i][j].setImageBitmap(whiteRook);
                    } else if (currentPiece.getType() == 'N' && color == false) {
                        squares[i][j].setImageBitmap(blackKnight);
                    } else if (currentPiece.getType() == 'N' && color == true) {
                        squares[i][j].setImageBitmap(whiteKnight);
                    } else if (currentPiece.getType() == 'B' && color == false) {
                        squares[i][j].setImageBitmap(blackBishop);
                    } else if (currentPiece.getType() == 'B' && color == true) {
                        squares[i][j].setImageBitmap(whiteBishop);
                    } else if (currentPiece.getType() == 'Q' && color == false) {
                        squares[i][j].setImageBitmap(blackQueen);
                    } else if (currentPiece.getType() == 'Q' && color == true) {
                        squares[i][j].setImageBitmap(whiteQueen);
                    } else if (currentPiece.getType() == 'K' && color == false) {
                        squares[i][j].setImageBitmap(blackKing);
                    } else if (currentPiece.getType() == 'K' && color == true) {
                        squares[i][j].setImageBitmap(whiteKing);
                    } else if (currentPiece.getType() == 'P' && color == false) {
                        squares[i][j].setImageBitmap(blackPawn);
                    } else if (currentPiece.getType() == 'P' && color == true) {
                        squares[i][j].setImageBitmap(whitePawn);
                    } else {
                        squares[i][j].setImageBitmap(null);
                    }
                }

            }
        }

        //Check to make sure there are some legal moves to highlight
        if(currentLegalMoves != null) {
            //If there are then go through all of them
            for (int[] coord : currentLegalMoves) {
                ImageButton square = squares[coord[0]][coord[1]];

                //The way this is written there is some redundancy so this could probably be done
                //better. However the idea is to mix the current color with the potential move color.
                if((coord[0] + coord[1]) % 2 == 0){
                    square.setBackgroundColor(mixColors(potentialMoveColor, darkSquareColor));
                }
                else{
                    square.setBackgroundColor(mixColors(potentialMoveColor, lightSquareColor));
                }
            }
        }
    }

    //Takes two integer colors and mixes them.
    int mixColors(int color1, int color2){
        //In an integer color, one byte is given to the alpha (brightness)
        //one to the red value, one to the green value, and one to the blue
        //value in exactly that order (from left to right). Thus we can extract
        //them with bit manipulations.

        //Get A,R,G,B values of the first color
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        //Get A,R,G,B values of the second color
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        //Average out A,R,G,B values of both colors
        int af = (a1 + a2)/2;
        int rf = (r1 + r2)/2;
        int gf = (g1 + g2)/2;
        int bf = (b1 + b2)/2;

        //Return the newly formed average color
        return (af << 24) | (rf << 16) | (gf << 8) | bf;


    }


}

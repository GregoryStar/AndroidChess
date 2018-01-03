package com.example.f.chess;

import java.util.ArrayList;

/**
 * Created by F on 7/6/2falsetrue7.
 */
public class Board {
    private Piece[][] squares = new Piece[8][8];
    private int moveCount;
    private String state = "OK";
    private char attemptedMoveType = 's'; //use lowercase
    private boolean isHypotheticalMove = false;
    private boolean gettingValidMoves = false;
    private int[] lastAttemptedMove = {-1, -1, -1, -1};

    public Board(){
        moveCount = 0;
        squares[0][0] = new Rook(true);
        squares[1][0] = new Knight(true);
        squares[2][0] = new Bishop(true);
        squares[3][0] = new Queen(true);
        squares[4][0] = new King(true);
        squares[5][0] = new Bishop(true);
        squares[6][0] = new Knight(true);
        squares[7][0] = new Rook(true);
        for(int i = 0; i < 8; i++){
            squares[i][1] = new Pawn(true);
        }
        for(int i = 0; i < 8; i++){
            squares[i][6] = new Pawn(false);
        }
        squares[0][7] = new Rook(false);
        squares[1][7] = new Knight(false);
        squares[2][7] = new Bishop(false);
        squares[3][7] = new Queen(false);
        squares[4][7] = new King(false);
        squares[5][7] = new Bishop(false);
        squares[6][7] = new Knight(false);
        squares[7][7] = new Rook(false);
    }

    public Board getBoardCopy(){
        Board board = new Board();
        board.setBoard(getBoard());
        board.setMoveCount(getMoveCount());
        return board;
    }

    public boolean isSquareEmpty(int x, int y){
        return squares[x][y] == null;
    }

    public Piece[][] getBoard(){
        Piece[][] copy = new Piece[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Piece currentPiece = squares[i][j];
                if(currentPiece == null) {
                    copy[i][j] = null;
                }
                else if(currentPiece.getType() == 'K'){
                    King current = (King)currentPiece;
                    King king = new King(currentPiece.getColor());
                    king.setMoved(current.hasMoved());
                    copy[i][j] = king;
                }
                else if(currentPiece.getType() == 'R'){
                    Rook current = (Rook) currentPiece;
                    Rook rook = new Rook(currentPiece.getColor());
                    rook.setMoved(current.hasMoved());
                    copy[i][j] = rook;
                }
                else if(currentPiece.getType() == 'N'){
                    Knight knight = new Knight(currentPiece.getColor());
                    copy[i][j] = knight;
                }
                else if(currentPiece.getType() == 'Q'){
                    Queen queen = new Queen(currentPiece.getColor());
                    copy[i][j] = queen;
                }
                else if(currentPiece.getType() == 'B'){
                    Bishop bishop = new Bishop(currentPiece.getColor());
                    copy[i][j] = bishop;
                }
                else{
                    Pawn current = (Pawn)currentPiece;
                    Pawn pawn = new Pawn(currentPiece.getColor());
                    pawn.setJustDoubleMoved(current.isJustDoubleMoved(moveCount), current.getMoveCount());
                    copy[i][j] = pawn;
                }

            }
        }

        return copy;
    }

    public boolean attemptMove(int x1, int y1, int x2, int y2){
        if(!gettingValidMoves && !isHypotheticalMove){
            lastAttemptedMove[0] = x1;
            lastAttemptedMove[1] = y1;
            lastAttemptedMove[2] = x2;
            lastAttemptedMove[3] = y2;
        }
        if (attemptMoveWithoutCheck(x1, y1, x2, y2) &&  !willCauseCheck(x1, y1, x2, y2)){
            if(!gettingValidMoves) {
                move(x1, y1, x2, y2);
                calculateState();
                return true;
            }
            attemptedMoveType = 's';
            return true;
        }
        else {
            attemptedMoveType = 's';
            return false;
        }
    }


    private boolean attemptMoveWithoutCheck(int x1, int y1, int x2, int y2){
        Piece selectedPiece = (squares[x1][y1]);
        Piece capturedPiece = (squares[x2][y2]);
        int[][] validMovesList;
        //Check if the selected piece is even a piece
        if(selectedPiece != null){
            validMovesList = selectedPiece.getMoveList(x1, y1);
        }
        else{
            return false;
        }

        //If the move passes those basic tests, begin extensive checks
        return basicChecks(x1, y1, x2, y2, validMovesList, selectedPiece, capturedPiece) &&
                extensiveTesting(x1, y1, x2, y2, validMovesList, selectedPiece.getType());
    }

    private boolean basicChecks(int x1, int y1, int x2, int y2, int[][] validMovesList, Piece selectedPiece, Piece capturedPiece){
        //check if the destination square and the current square contain pieces of the same color
        if(capturedPiece != null && (capturedPiece.getColor() == selectedPiece.getColor())){
            return false;
        }

        //check if the proper color is moving
        if(selectedPiece.getColor() != whoseMove()){
            return false;
        }


        //check if the destination square is in the list of potential moves of the piece
        if(!checkCastling(x1, y1, x2, y2)) {
            boolean moveFeasible = false;
            for (int[] coord : validMovesList) {
                if (coord[0] == x2 && coord[1] == y2) {
                    moveFeasible = true;
                }
            }
            if (!moveFeasible) {
                return false;
            }
        }

        return true;
    }

    //The name says it all; this is "extensive testing". Basically, check a bunch of bizarre pawn
    //moves and check for intersections.
    private boolean extensiveTesting(int x1, int y1, int x2, int y2, int[][] validMovesList, char type){

        boolean isIntersecting = false;
        boolean illegalPawnMove = false;

        //Intersections are impossible with kings and knights. With knights it's obvious, but with
        //kings they're impossible because kings only move one square at a time.
        if(type != 'N' && type != 'K') {
            isIntersecting = testIntersection(x1, y1, x2, y2);
        }
        if(type == 'P' && !extensivePawnTesting(x1, y1, x2, y2)){
            illegalPawnMove = true;
        }
        return !isIntersecting && !illegalPawnMove;
    }

    private boolean testIntersection(int x1, int y1, int x2, int y2){
        //We check to see if any of the squares in between the initial square and destination
        //square contain a piece. Since only bishops, pawns, rooks, and queens need to worry about
        //intersections, this method is only called on those piece. Moreover, pawns can only
        //intersect when they're making a double move, and when they make a double move they move
        //like a rook. Also, Queens can only move like either a bishop or a rook in any one move.
        //Thus, we can pretend that the piece being moved is either a rook or a bishop.

        //We notice some similarities between checking for a rook move intersection and checking
        //for a bishop move intersection, and cleverly exploit this to get a generalized solution.

        //Find the direction the x and y are changing in
        int dx = x2 - x1;
        int dy = y2 - y1;

        //Find the bigger of dx and dy. Notice that if the move is a rook move, then either dx = 0
        //or dy = 0 but not both. However if it's a bishop move, then dx = dy.
        int maxMagnitude = Math.max(Math.abs(dx),Math.abs(dy));

        //Regardless or whether it's a rook or bishop move, the number of squares we need to check
        //is exactly the maximum of dx and dy. Consider the three possibilities
        //(vertical rook move, horizontal rook move, bishop move)
        for(int i = 1; i < maxMagnitude; i++){
            //Taking dx/maxMagnitude for a horizontal rook move produces either -1 or 1, which
            //is it's direction. Multiplying i by that and offseting it by x1 lets us loop through
            //the possible intersection squares. Notice that this one statements works for vertical
            //rook moves and also for bishop moves. This is the clever bit: if either dx or dy are
            //zero, as they will be in a rook move, then dividing by maxMagnitude yields 0 as well.
            //Thus, you get no offset in the undesired direction.
            if(squares[x1 + i*(dx/maxMagnitude)][y1 + i*(dy/maxMagnitude)] != null){
                return true;
            }
        }
        return false;

    }

    private boolean extensivePawnTesting(int x1, int y1, int x2, int y2){

        //Because of some strange quirks in the code that I don't want to fix at this point,
        //all of these tests actually need to happen. Normally I just return false as soon as one
        //fails but that doesn't work here. Thus I run all tests and return their logical OR.
        boolean pct = pawnCaptureTest(x1, y1, x2, y2);
        boolean cvdm = checkValidDoubleMove(x1, y1, y2);
        boolean ce = checkEnpassant(x1, y1, x2, y2);
        checkPromotion(y1, y2);
        return pct || cvdm || ce;
    }

    private boolean pawnCaptureTest(int x1, int y1, int x2, int y2){
        if (Math.abs(y2 - y1) == 2){
            return false;
        }
        Piece piece = squares[x2][y2];
        if(piece == null){
            return x1 == x2;
        }
        else{
            return x1 != x2;
        }
    }

    private void checkPromotion(int y1, int y2){
        if ((y1 == 6 && y2 == 7) || (y1 == 1 && y2 == 0)){
            attemptedMoveType = 'p';
        }
    }

    private boolean checkValidDoubleMove(int x1, int y1, int y2){
        Pawn pawn = (Pawn)squares[x1][y1];
        if(((y1 == 1 && y2 == 3) || (y1 == 6 && y2 == 4)) && squares[x1][y2] == null){
            pawn.setJustDoubleMoved(true, moveCount);
            return true;
        }
        else return false;
    }

    private boolean checkEnpassant(int x1, int y1, int x2, int y2){
        if(x1 == x2){
            return false;
        }
        else if(squares[x2][y1] == null || squares[x2][y1].getType() != 'P') {
            return false;
        }
        else {
            Pawn pawn1 = (Pawn) squares[x1][y1];
            Pawn pawn2 = (Pawn) squares[x2][y1];
            if (pawn2.isJustDoubleMoved(moveCount) && !(pawn1.getColor() == pawn2.getColor())){
                attemptedMoveType = 'e';
                return true;
            }
            return false;
        }
    }

    private void move(int x1, int y1, int x2, int y2){
        //If the move is "hypothetical" to test for checks, the move counter should not be incremented.
        if(!isHypotheticalMove){
            moveCount++;
            Piece piece = squares[x1][y1];
            if(piece.getType() == 'R'){
                Rook rook = (Rook)piece;
                rook.setMoved(true);
            }
            if(piece.getType() == 'K'){
                King king = (King)piece;
                king.setMoved(true);
            }

        }
        else if(attemptedMoveType == 'p'){
            attemptedMoveType = 's';
        }

        //standard move
        if(attemptedMoveType == 's'){
            standardMove(x1, y1, x2, y2);
        }
        //enpassant
        else if(attemptedMoveType == 'e'){
            enpassantMove(x1, y1, x2, y2);
        }
        //pawn promotion
        else if(attemptedMoveType == 'p' && !isHypotheticalMove){
            state = "Promotion";
            //promotionMove(x1, y1, x2, y2);
        }
        //castle kingside
        else if(attemptedMoveType == 'k'){
            castleKingsideMove(y1);
        }
        //castle queenside
        else if(attemptedMoveType == 'q'){
            castleQueensideMove(y2);
        }
        attemptedMoveType = 's';
    }

    public void promoteTo(Piece piece){
        int x1 = lastAttemptedMove[0];
        int y1 = lastAttemptedMove[1];
        int x2 = lastAttemptedMove[2];
        int y2 = lastAttemptedMove[3];
        promotionMove(x1, y1, x2, y2, piece);
    }

    private void standardMove(int x1, int y1, int x2, int y2){
        Piece piece = squares[x1][y1];
        squares[x1][y1] = null;
        squares[x2][y2] = piece;
    }

    private void castleKingsideMove(int y1){
        Piece piece = squares[4][y1];
        boolean color = piece.getColor();

        squares[4][y1] = null;
        squares[7][y1] = null;
        squares[5][y1] = new Rook(color);
        squares[6][y1] = new King(color);
    }

    private void castleQueensideMove(int y1){
        Piece piece1 = squares[4][y1];
        Piece piece2 = squares[0][y1];
        King king = (King)piece1;
        Rook rook = (Rook)piece2;

        squares[4][y1] = null;
        squares[0][y1] = null;
        squares[3][y1] = rook;
        squares[2][y1] = king;
    }

    private void promotionMove(int x1, int y1, int x2, int y2, Piece piece){
        squares[x1][y1] = null;
        squares[x2][y2] = piece;
    }

    private void enpassantMove(int x1, int y1, int x2, int y2){
        Piece piece = squares[x1][y1];
        squares[x1][y1] = null;
        squares[x2][y1] = null;
        squares[x2][y2] = piece;
    }


    private boolean willCauseCheck(int x1, int y1, int x2, int y2){
        Piece selectedPiece = squares[x1][y1];
        Piece capturedPiece = squares[x2][y2];

        //Store the piece on both the left and right of the piece being moved. In the case of
        //enpassant we need to restore these.
        Piece pieceOnLeft = null;
        Piece pieceOnRight = null;
        if(x1 - 1 >= 0){
            pieceOnLeft = squares[x1-1][y1];
        }
        if(x1 + 1 < 8){
            pieceOnRight = squares[x1+1][y1];
        }
        boolean selectedPieceColor = selectedPiece.getColor();
        isHypotheticalMove = true;

        //information is saved about the current position, because a bunch of "hypothetical" moves will take place.
        //This method looks for check by first collecting a list of all squares where an attacking piece could be.
        //This is done by combining the move list of a queen and a knight standing on the king's current position.
        //Since all captures in chess (including pawn captures) fall into a subset of a queen's potential squares
        //and a knight's potential squares, this is a mathematically correct approach. Once a complete list of squares
        //is made, all squares in the list are checked for a piece. If they have a piece and it is of the opposite color
        //to the king, we attempt to move it to the king's location via a "hypothetical" move. Normally when moves are
        //made, attemptMove() is called, but here we call attemptMoveWithoutCheck() which is a variant of attemptMove().
        //We do this because attemptMove() would end up calling willCauseCheck(), which would create an infinite
        //recursive call. If the piece in question can indeed move to the king's position (not taking check into account),
        //then clearly the move we're considering in attemptMove() would cause check, and thus we reject it.
        char storedAttemptedMoveType = attemptedMoveType;
        if(attemptedMoveType == 'k' || attemptedMoveType == 'q') {
            attemptedMoveType = 's';
        }

        //Move the piece back and restore it's left and right adjacent squares in case the move
        //was an enpassant
        move(x1, y1, x2, y2);

        int[] kingPosition = findKingPosition(selectedPieceColor);

        Queen queen = new Queen(!selectedPieceColor);
        Knight knight = new Knight(!selectedPieceColor);

        int[][] knightMoves = knight.getMoveList(kingPosition[0], kingPosition[1]);
        int[][] queenMoves = queen.getMoveList(kingPosition[0], kingPosition[1]);
        int[][] superKingMoves = new int[knightMoves.length + queenMoves.length][];
        for(int i = 0; i < knightMoves.length; i++){
            superKingMoves[i] = knightMoves[i];
        }
        for(int i = 0; i < queenMoves.length; i++){
            superKingMoves[i + knightMoves.length] = queenMoves[i];
        }


        for(int i = 0; i < superKingMoves.length; i++){
            int a = superKingMoves[i][0];
            int b = superKingMoves[i][1];
            int c = kingPosition[0];
            int d = kingPosition[1];
            Piece attackingPiece = squares[a][b];
            //Attempt to move every piece that could be attacking the king to the king's position. Obviously
            //if one of them can reach the king, then the move currently being attempted causes check. A special
            //check is required for pawns because they move forward, but can only attack diagonally. This is because the
            //program does not distinguish between standard moves and captures (usually, they are identical).
            if(attackingPiece != null && !(attackingPiece.getType() == 'P' && a == c) && attemptMoveWithoutCheck(a, b, c, d)){
                move(x2, y2, x1, y1);
                squares[x2][y2] = capturedPiece;
                isHypotheticalMove = false;
                return true;
            }
        }
        move(x2, y2, x1, y1);
        if(x1 - 1 >= 0){
            squares[x1-1][y1] = pieceOnLeft;
        }
        if(x1 + 1 < 8){
            squares[x1+1][y1] = pieceOnRight;
        }

        squares[x2][y2] = capturedPiece;
        isHypotheticalMove = false;
        attemptedMoveType = storedAttemptedMoveType;
        return false;
    }

    private int[] findKingPosition(boolean color){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(squares[i][j] != null && squares[i][j].getType() == 'K' && squares[i][j].getColor() == color){
                    int[] coord = {i, j};
                    return coord;
                }
            }
        }
        int[] coord = {0, 0};
        return coord;
    }

    private boolean checkCastling(int x1, int y1, int x2, int y2){
        Piece piece = squares[x1][y1];
        if(piece == null || piece.getType() != 'K'){
            return false;
        }

        King king = (King)piece;
        if(king.hasMoved()){
            return false;
        }

        boolean color = king.getColor();
        if(color && x2 == 6 && y2 == 0 && attemptWhiteCastleK(x1, y1)){
            attemptedMoveType = 'k';
            return true;
        }
        else if(color && x2 == 2 && y2 == 0 && attemptWhiteCastleQ(x1, y1)){
            attemptedMoveType = 'q';
            return true;
        }
        else if(!color && x2 == 6 && y2 == 7 && attemptBlackCastleK(x1, y1)){
            attemptedMoveType = 'k';
            return true;
        }
        else if(!color && x2 == 2 && y2 == 7 && attemptBlackCastleQ(x1, y1)){
            attemptedMoveType = 'q';
            return true;
        }
        else return false;
    }

    private boolean attemptWhiteCastleK(int x1, int y1){
        Piece piece = squares[7][0];
        if(piece == null || piece.getType() != 'R' || piece.getColor() != true){
            return false;
        }

        Rook rook = (Rook)piece;
        if(rook.hasMoved()){
            return false;
        }

        if(testIntersection(x1, y1, 7, 0)){
            return false;
        }

        for(int i = 0; i < 2; i++){
            if(willCauseCheck(x1, y1, x1 + i, y1)){
                return false;
            }
        }

        return true;
    }

    private boolean attemptBlackCastleK(int x1, int y1){
        Piece piece = squares[7][7];
        if(piece == null || piece.getType() != 'R' || piece.getColor() == true){
            return false;
        }

        Rook rook = (Rook)piece;
        if(rook.hasMoved()){
            return false;
        }

        if(testIntersection(x1, y1, 7, 7)){
            return false;
        }

        for(int i = 0; i < 2; i++){
            if(willCauseCheck(x1, y1, x1 + i, y1)){
                return false;
            }
        }

        return true;
    }

    private boolean attemptWhiteCastleQ(int x1, int y1){
        Piece piece = squares[0][0];
        if(piece == null || piece.getType() != 'R' || piece.getColor() != true){
            return false;
        }

        Rook rook = (Rook)piece;
        if(rook.hasMoved()){
            return false;
        }

        if(testIntersection(x1, y1, 0, 0)){
            return false;
        }

        for(int i = 0; i < 2; i++){
            if(willCauseCheck(x1, y1, x1 - i, y1)){
                return false;
            }
        }

        return true;
    }

    private boolean attemptBlackCastleQ(int x1, int y1){
        Piece piece = squares[0][7];
        if(piece == null || piece.getType() != 'R' || piece.getColor() == true){
            return false;
        }

        Rook rook = (Rook)piece;
        if(rook.hasMoved()){
            return false;
        }

        if(testIntersection(x1, y1, 0, 7)){
            return false;
        }

        for(int i = 0; i < 2; i++){
            if(willCauseCheck(x1, y1, x1 - i, y1)){
                return false;
            }
        }

        return true;
    }

    public void setBoard(Piece[][] squares){
        this.squares = squares;
    }

    public int[][] getLegalMoves(int x, int y){
        Piece piece = squares[x][y];
        if(piece == null){
            return null;
        }
        else{
            int[][] potentialMoves = piece.getMoveList(x, y);
            ArrayList<int[]> legalMoves = new ArrayList<int[]>();
            gettingValidMoves = true;
            for(int[] coord : potentialMoves){
                 if(attemptMove(x, y, coord[0], coord[1])){
                    legalMoves.add(coord);
                }
            }

            int[][] legalMoveList = new int[legalMoves.size()][2];
            for(int i = 0; i < legalMoveList.length; i++){
                legalMoveList[i] = legalMoves.get(i);
            }
            gettingValidMoves = false;

            return legalMoveList;
        }
    }

    public int getMoveCount(){
        return moveCount;
    }

    public void setMoveCount(int moveCount){
        this.moveCount = moveCount;
    }

    private boolean whoseMove(){
        if(isHypotheticalMove){
            return moveCount % 2 == 1;
        }
        else{
            return moveCount % 2 == 0;
        }

    }

    private boolean canMove(){
        for (Piece[] row: squares) {
            for(Piece piece: row){
                if(piece != null && piece.getColor() == whoseMove() && getLegalMoves(piece).length != 0){
                    return true;
                }
            }

        }
        return false;
    }

    private int[][] getLegalMoves(Piece piece){
        int i = 0;
        int j = 0;
        while(squares[i][j] != piece){
            i++;
            if(i == 8){
                j++;
            }
            i %= 8;
        }

        return getLegalMoves(i, j);
    }

    boolean isCheck(){
        int[] kingPosition = findKingPosition(whoseMove());
        return willCauseCheck(kingPosition[0], kingPosition[1], kingPosition[0], kingPosition[1]);
    }

    private void setState(String state){
        this.state = state;
    }

    private void calculateState(){
        if(state.equals("Promotion")){
            state = "Promotion";
        }
        else if(!canMove()){
            if(isCheck()){
                state = "Checkmate";
            }
            else{
                state = "Stalemate";
            }
        }
        else{
            state = "OK";
        }
    }

    public String getState(){
        return state;
    }
}

package com.example.f.chess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class PlayGameActivity extends AppCompatActivity {
    private VisualBoardManager boardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_game);
        boardManager = new VisualBoardManager((LinearLayout)findViewById(R.id.chessboard), this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void undo(View view){
        boardManager.undo();
    }

    public void redo(View view){
        boardManager.redo();
    }
}

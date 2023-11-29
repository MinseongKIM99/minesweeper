package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TableLayout table;
    ToggleButton toggleButton;
    TextView text;
    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            1.0f
    );
    BlockButton[][] buttons = new BlockButton[9][9];

    static int flags;
    static int blocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        table = (TableLayout) findViewById(R.id.tablelayout);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        text = (TextView)findViewById(R.id.textView);

        int row;
        int col;
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Toast.makeText(MainActivity.this, "flag", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "break", Toast.LENGTH_SHORT).show();
                }
            }
        });
        for (row = 0; row < 9; row++) {
            TableRow tableRow = new TableRow(this);
            for (col = 0; col < 9; col++) {
                buttons[row][col] = new BlockButton(this, row, col);
                buttons[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(toggleButton.isChecked()){   //toggleButton
                            remain();
                            ((BlockButton)view).toggleFlag();
                            ((BlockButton)view).setClickable(false);
                        }
                        else {  // breakBlock
                            if(((BlockButton)view).isMine()){
                                gameOver(false);
                            }
                            ((BlockButton)view).breakBlock();
                            if(((BlockButton)view).getBlocks()==10){
                                gameOver(true);
                            }
                        }
                    }
                });
                tableRow.addView(buttons[row][col]);
            }
            table.addView(tableRow);
        }

        // 지뢰 위치 정하기
        int minesToPlay = 10; // 지뢰개수
        int placemine = 0; // 놓인개수
        text.setText("Mines : "+minesToPlay);
        Random random = new Random();
        while (placemine < minesToPlay) {
            int randomRow = random.nextInt(9);
            int randomCol = random.nextInt(9);
            BlockButton block = buttons[randomRow][randomCol];
            if (!block.isMine()) {
                block.setMine(true);
                placemine++;
            }
        }

    }//onCreate
    // 게임오버
    public void gameOver(boolean isWin) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                buttons[row][col].setClickable(false);
            }
        }
        if (isWin) {
            Toast.makeText(this, "Win", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this, "Lose", Toast.LENGTH_SHORT).show();
        }

    }
    // TextView에 표시
    public void remain(){
        int minesToPlay = 10; // 지뢰개수
        int remain = minesToPlay - flags;
        text.setText("Mines : "+remain);
    }
    // 위치확인
    boolean isValidPosition(int row, int col) {
        return (row >= 0 && row < 9 && col >= 0 && col < 9);
    }
    @SuppressLint("AppCompatCustomView")
    public class BlockButton extends Button {
        int x;
        int y;
        boolean mine;
        boolean flag;
        int neighborMines;
        boolean prev; // 이전에 검사를 한건지 안한건지 확인

        // 각각의 block 정의
        public BlockButton(Context context, int x, int y) {
            super(context);
            setLayoutParams(layoutParams);
            this.x = x;
            this.y = y;
            this.mine = false;
            this.flag = false;
            this.neighborMines = 0;
            this.prev = false;
            blocks++;
        }

        @Override
        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public boolean isPrev() {
            return prev;
        }

        public void setPrev(boolean prev) {
            this.prev = prev;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public boolean isMine() {
            return mine;
        }

        public int getNeighborMines(int row, int col) {      // 주변지뢰개수 찾기
            int count = 0;
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (isValidPosition(i, j) && buttons[i][j].isMine())
                        count++;
                }
            }
            return count;
        }

        public int getBlocks() {
            return blocks;
        }

        public void toggleFlag() {       // flag or unflag
            if (!isFlag()) {
                setFlag(true);
                setText("+");
                flags++;
            } else {
                setFlag(false);
                setText("");
                flags--;

            }
            remain();
        }

        public boolean breakBlock() {        // 블록 깨기
            setClickable(false);
            setPrev(true);
            blocks--;
            if (isMine()) {   // 지뢰인 경우
                setText("X");   // mine symbol
                return true;
            }else{
            // 지뢰아닐때
            int neighborMines = getNeighborMines(x, y);
            // 숫자 표기
            setText(String.valueOf(neighborMines));
            if(neighborMines == 0) {    // 0일때 주변 블록 탐색해서 깨기
                check();
            }
        }
            return false;
        }//breakBlock()
    // 재귀를 통해 블록탐색
    public void check(){
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isValidPosition(i, j)) {
                    BlockButton sur = buttons[i][j];
                    if(!sur.isPrev()){
                        sur.breakBlock();
                    }
                }
            }
        }
    }
    }// BlockButton

} // MainActivity
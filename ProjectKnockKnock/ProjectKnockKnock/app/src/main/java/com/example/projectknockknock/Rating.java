package com.example.projectknockknock;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Rating extends AppCompatActivity  {

    RatingBar ratingBar;
    Button btnRatingBar;
    TextView ratingBarTxt;

    //Intent intent = new Intent(this.getIntent());
    //String singer = intent.getStringExtra("singer");
    //String song = intent.getStringExtra("song");
    //final int[] i = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratingbar);

        final Song son = new Song();


        Intent intent = new Intent(this.getIntent());
        String singer = intent.getStringExtra("singer");
        String song = intent.getStringExtra("song");
        son.setSinger(singer);
        son.setSong(song);


        ratingBar = (RatingBar) findViewById(R.id.rating);
        btnRatingBar = (Button) findViewById(R.id.btnRatingBar);
        ratingBarTxt = (TextView) findViewById(R.id.ratingBarTxt) ;

        btnRatingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = String.valueOf(ratingBar.getRating());
                Toast.makeText(getApplicationContext(), rating+"점이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                //ratingBarTxt.setText(rating);

                //ratingBarTxt.setText(singer + song);
                ratingBarTxt.setText(son.getSinger()+son.getSong());

                //int rat = Integer.getInteger(rating);
                //클릭한 시간으로 데이터베이스에 저장시킴
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow = new SimpleDateFormat("ddhhmmss");
                //SimpleDateFormat sdfNow = new SimpleDateFormat("hh:mm:ss");
                String strNow = sdfNow.format(date);


                    // 데이터 베이스에 값을 넘겨주어야함.
                    DatabaseReference mDatabase;
                    mDatabase = FirebaseDatabase.getInstance().getReference("feedback");
                    DatabaseReference rDatabase = mDatabase.child("rating"+ strNow);
                    //데이터 베이스 feedback의 child 'rating'을 참조했음

                    rDatabase.child("rate").setValue(rating);
                    rDatabase.child("singer").setValue(son.getSinger());
                    rDatabase.child("song").setValue(son.getSong());



                finish();


            }
        });






        //addListenerOnButtonClick();


/*

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBarTxt.setText(""+ rating);
                Toast.makeText(getApplicationContext(), rating+"점을 주셨습니다.", Toast.LENGTH_SHORT);
                // ************** 곡 정보와 rating 값을 데이터 베이스에 저장해야함
            }
        });

        btnRatingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

*/




    }
}

class Song {
    private String singer;
    private String song;
    public Song(){

    }

    public Song(String singer, String song) {
        this.singer = singer;
        this.song = song;
    }
    public String getSinger(){
        return singer;
    }
    public void setSinger(String a) {
        this.singer = a;
    }
    public String getSong() {
        return song;
    }
    public void setSong(String b) {
        this.song = b;
    }

}
package com.example.projectknockknock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubActivity extends AppCompatActivity implements Serializable {
    Button btnFeedback, btnReset, musicButton , testBtn;
    TextView musicIng, musicLink;
    View dialogView;

    List fileList = new ArrayList<>();
    ArrayAdapter adapter;
    static boolean calledAlready = false;

    static String song[] = new String[2];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent intent2 = getIntent();
        String emotion = intent2.getStringExtra("emotion");


//        Intent intent = getIntent();
        Intent intent = new Intent(this.getIntent());
        final int heart = intent.getIntExtra("heartBeat", 0);
        //intent.putIn

        //피드백임
        btnFeedback = (Button) findViewById(R.id.btnFeedback);
        btnReset = (Button) findViewById(R.id.btnReset);
        musicIng = (TextView) findViewById(R.id.musicIng);
        musicLink = (TextView) findViewById(R.id.musicLink);
        musicButton = (Button) findViewById(R.id.musicButton);
        testBtn = (Button) findViewById(R.id.testBtn);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicLink.setText(heart+"");
            }
        });


        // 음악 실행 버튼 누르면 파이어베이스 기능 실행
        // 음악실행버튼 누르면 6가지 감정으로 분류해서 출력
        //  분노 혐오 두려움 행복 슬픔 놀람


        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseDatabase database;
                DatabaseReference myRefSad, myRefAngry, myRefDisgust, myRefFear, myRefSmile, myRefSurprise;


                // swtich 문으로, intent를 통해 넘어온 emotion의 정보에 따라 음악을 분류해서 출력.
                database = FirebaseDatabase.getInstance();
                myRefSad = database.getReference("sad"); // 감정에 따라서 바꿔줌
                myRefAngry = database.getReference("angry");
                myRefDisgust = database.getReference("disgust");
                myRefFear = database.getReference("fear");
                myRefSmile = database.getReference("smile");
                myRefSurprise = database.getReference("surprise");


                int number = 1;
                number = (int)(Math.random()*3); // 랜덤 함수
                String num = String.valueOf(number); // 넘겨줄 값


                // **** Smile 해당
                myRefSmile.child(num).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String myNames="";
                        int a= 0;
                        for (DataSnapshot myChild : dataSnapshot.getChildren()) {
                            myNames = myNames + "\n\n" + myChild.getValue();

                            song[a] = myNames;
                            a++;
                        }
                        musicLink.setText(myNames);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


               // *** sad 해당
               myRefSad.child(num).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String myNames="";
                        int a= 0;

                        for (DataSnapshot myChild : dataSnapshot.getChildren()) {
                            myNames = myNames + "\n\n" + myChild.getValue();
                            //myNames = myNames + "\n\n" + myChild.child("1").getValue();
                            //myNames = myNames + "\n\n" + myChild.limitToLast(1).getValue();

                            song[a] = myNames;
                            a++;
                        }
                        musicLink.setText(myNames);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
/*
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("test");

                myRef.setValue("Hello, World!");

                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        //Log.d(TAG, "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

                DatabaseReference mdatabase;
                mdatabase = FirebaseDatabase.getInstance().getReference();
                musicLink.setText(mdatabase.toString());


            }

            */
        });


        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 피드백을 주기위한 버튼
                // Rating Bar사용

                // 곡정보를 넘겨줌
                Intent intent3 = new Intent(SubActivity.this, Rating.class);
                intent3.putExtra("singer", song[0]);
                intent3.putExtra("song", song[1]);
                startActivity(intent3);


            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 심박수를 재는 xml로 넘어감
                // 심박수에 따라 감정을 다시 분류하고 새로운 추천을 진행함

                Intent intent2 = new Intent(SubActivity.this, Heart.class);
                startActivity(intent2);

            }
        });
    }
}

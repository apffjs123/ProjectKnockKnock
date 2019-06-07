package com.example.projectknockknock;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.app.AppCompatActivity;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.CompressFormat.JPEG;



public class MainActivity extends AppCompatActivity {

    Button face;
    Button recog;
    Button btnNext;
    ImageView iv;

    private Uri photoUri;
    private String filePath;
    String imgFile;

    String currentPhotoPath;
    String fileName;//이미지 이름
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    Intent data;
    private String imageFilePath;
    Intent intent;
    private int REQUEST_TEST = 1;
    TextView emotion;


    Bitmap src;
    byte b;
    final String clientId = "03fyTLwrHV5l0ABjQaCW";//애플리케이션 클라이언트 아이디값";
    final String clientSecret = "W134QS_Tw9";//애플리케이션 클라이언트 시크릿값";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        face = findViewById(R.id.face);
        recog = findViewById(R.id.recog);
        iv = findViewById(R.id.iv);
        emotion = findViewById(R.id.emotion);

        //btnCamer = (Button) findViewById(R.id.btnCamera); // 카메라 실행 후 종료
        //btnEmotion = (Button) findViewById(R.id.btnEmotion); // 찍힌 사진을 불러와 표정인식

        btnNext = (Button) findViewById(R.id.btnNext); // 다음 화면으로 전환

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



               //Intent intent = new Intent(getApplicationContext() , SubActivity.class);
/*              Intent intent = new Intent(getApplicationContext(), HeartRate.class);*/
                Intent intent = new Intent(MainActivity.this, SubActivity.class); // 심박수 측정 JAVA 이상있음
                intent.putExtra("emotion",emotion.getText().toString());
                startActivity(intent);

            }
        });

//        Intent intent2 = getIntent();
//        String lastHeartBeat = intent2.getExtras().getString("lastHeartBeat");

//        Toast toast = Toast.makeText(this, lastHeartBeat, Toast.LENGTH_SHORT);



        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTakePhotoIntent();
            }

        });
        recog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check runtime permission for access to external storage.
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    }
                } else {
                    final CfrAsyncTask asyncTask = new CfrAsyncTask();
                    asyncTask.execute(currentPhotoPath);

                }

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
           /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap src = BitmapFactory.decodeFile(currentPhotoPath, options);
            Bitmap resized = Bitmap.createScaledBitmap(src, 150, 200, true);*/
            iv.setImageURI(photoUri);

        }
    }


    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp;
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();

        return image;

    }
     /*  BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap src = BitmapFactory.decodeFile(currentPhotoPath, options);
            Bitmap resized = Bitmap.createScaledBitmap(src, 150, 200, true);*/

  /*  public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }*/


    class CfrAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = null;





            try {
                String paramName = "image"; // 파라미터명은 image로 지정
                String imgFile = strings[0];
                File uploadFile = new File(imgFile);
                String apiURL = "https://openapi.naver.com/v1/vision/face"; // 얼굴 감지
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);
                // multipart request
                String boundary = "---" + System.currentTimeMillis() + "---";
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                OutputStream outputStream = con.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
                String LINE_FEED = "\r\n";
                // file 추가
                String fileName = uploadFile.getName();
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();
                FileInputStream inputStream = new FileInputStream(uploadFile);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();
                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();
                BufferedReader br = null;
                int responseCode = con.getResponseCode();
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    System.out.println("error!!!!!!! responseCode= " + responseCode);
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }

                if (br != null) {
                    StringBuffer response = new StringBuffer();
                    while ((result = br.readLine()) != null) {
                        response.append(result);
                    }
                    br.close();
                    result = response.toString();
                } else {
                    result = "error !!!";
                }
            } catch (Exception e) {
                result = e.toString();
            }

            System.out.println(result);

            return result;
        }


        @Override
        protected void onPostExecute (String s){
            emotion.setText(s);
        }
    }
}

package com.example.projectknockknock;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HeartRate extends Activity {
    private String TAG = HeartRate.class.getName();
    private GoogleApiClient googleApiClient;
    private boolean authInProgress = false;
    private OnDataPointListener onDataPointListener;
    private static final int AUTH_REQUEST = 1;

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.BODY_SENSORS
    };

    private static final int REQUEST_PERMISSION_CODE = 12345;
    private List<String> missingPermission = new ArrayList<>();

    private boolean bCheckStarted = false;
    private boolean bGoogleConnected = false;

    Button btnStart, btnFeedback;
    ProgressBar spinner;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    TextView textMon;


    TextView textMon2;

// *********************현재 심박수 센서 작동안함!!!!!!!!!!!!!!!!!!!!!!!1ㅠㅠㅠㅠㅠㅠㅠㅠ

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        //Intent intent2 = getIntent();

      /*  spinner.setVisibility(View.INVISIBLE);

        // powerManager : 전력을 조절해주는 매니저, POWER_SERVICE 형태로
        // SCREEN_BRIGHT_WAKE_LOCK은 화면을 밝게 켜주는 역할을 하고
        // ACQUIRE_CAUSES_WAKEUP은 꺼진 화면을 켜주는 역할을 하고
        // ON_AFTER_RELEASE는 화면이 켜진 상태를 더 오래 유지할 수 있는 역할을 해줌
        powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        initUI();
        checkAndRequestPermissions();*/
    }

    public void initUI() {

        initGoogleApiClient();

        //textMon2 = findViewById(R.id.textMon2);
        btnFeedback = findViewById(R.id.btnFeedback);
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bCheckStarted) {
                    //btnStart.setText(R.string.msg_start);
                    btnStart.setText("Start");
                    bCheckStarted = false;

                    unregisterFitnessDataListener();

                    spinner.setVisibility(View.INVISIBLE);

                    wakeLock.release();
                }
                else {
                    if (bGoogleConnected == true) {
                        findDataSources();
                        registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);
                        btnStart.setText("Stop");
                        //btnStart.setText(R.string.msg_stop);
                        bCheckStarted = true;
                        spinner.setVisibility(View.VISIBLE);
                        wakeLock.acquire();

                    }
                    else {
                        if (HeartRate.this.googleApiClient != null)
                            HeartRate.this.googleApiClient.connect();
                    }
                }
            }
        });

        textMon = findViewById(R.id.textMon);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.INVISIBLE);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setText("Wait please ...");
        btnStart.setEnabled(false);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bCheckStarted) {
                    //btnStart.setText(R.string.msg_start);
                    btnStart.setText("Start");
                    bCheckStarted = false;

                    unregisterFitnessDataListener();

                    spinner.setVisibility(View.INVISIBLE);

                    wakeLock.release();
                }
                else {
                    if (bGoogleConnected == true) {
                        findDataSources();
                        registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);
                        btnStart.setText("Stop");
                        //btnStart.setText(R.string.msg_stop);
                        bCheckStarted = true;
                        spinner.setVisibility(View.VISIBLE);
                        wakeLock.acquire();

                    }
                    else {
                        if (HeartRate.this.googleApiClient != null)
                            HeartRate.this.googleApiClient.connect();
                    }
                }
            }
        });
    }

    private void initGoogleApiClient() {
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                //.addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.d(TAG, "initGoogleApiClient() onConnected good...");

                                bGoogleConnected = true;
                                btnStart.setText("Start");
                                btnStart.setEnabled(true);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.d(TAG, "onConnectionSuspended() network_lost bad...");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.d(TAG, "onConnectionSuspended() service_disconnected bad...");

                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {

                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.d(TAG, "Connection failed. Cause: " + result.toString());

                                if (!result.hasResolution()) {
                                    HeartRate.this.finish();
                                    return;
                                }

                                if (!authInProgress) {
                                    try {
                                        Log.d(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(HeartRate.this,
                                                AUTH_REQUEST);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                        HeartRate.this.finish();
                                    }
                                }
                                else {
                                    HeartRate.this.finish();
                                }
                            }
                        }
                )
                .build();
    }

    //Checks if there is any missing permissions, and
    //requests runtime permission if needed.



    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            if (HeartRate.this.googleApiClient != null)
                HeartRate.this.googleApiClient.connect();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        } else {
            if (HeartRate.this.googleApiClient != null)
                HeartRate.this.googleApiClient.connect();
        }

    }

    /**
     * Result of runtime permission request
     */


    // 권한 허가 처리
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            initGoogleApiClient();
            if (HeartRate.this.googleApiClient != null)
                HeartRate.this.googleApiClient.connect();
        } else {
            Toast.makeText(getApplicationContext(), "Failed get permissions", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void findDataSources() {
        Fitness.SensorsApi.findDataSources(googleApiClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                // .setDataTypes(DataType.TYPE_SPEED)
                // .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {

                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {

                            if (dataSource.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)
                                    && onDataPointListener == null) {
                                Log.d(TAG, "findDataSources onResult() registering dataSource=" + dataSource);
                                registerDataSourceListener(DataType.TYPE_HEART_RATE_BPM);

                            }
                        }
                    }
                });

    }


    private void registerDataSourceListener(DataType dataType) {
        onDataPointListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value aValue = dataPoint.getValue(field);
                    //Log.d(TAG, "Detected DataPoint field: " + field.getName());
                    //Log.d(TAG, "Detected DataPoint value: " + aValue);

                    //addContentToView("dataPoint=" + field.getName() + " " + aValue + "\n");
                    addContentToView(aValue.asFloat());
                }
            }
        };

        Fitness.SensorsApi.add(
                googleApiClient,
                new SensorRequest.Builder()
                        .setDataType(dataType)
                        .setSamplingRate(5, TimeUnit.SECONDS)
                        .setAccuracyMode(SensorRequest.ACCURACY_MODE_DEFAULT)
                        .build(),
                onDataPointListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "onDataPointListener  registered good");
                        } else {
                            Log.d(TAG, "onDataPointListener failed to register bad");
                        }
                    }
                });

    }

    private void unregisterFitnessDataListener() {
        if (this.onDataPointListener == null) {
            return;
        }

        if (this.googleApiClient == null) {
            return;
        }

        if (this.googleApiClient.isConnected() == false) {
            return;
        }

        Fitness.SensorsApi.remove(
                this.googleApiClient,
                this.onDataPointListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Listener was removed!");
                        } else {
                            Log.d(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onStart connect attempted");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFitnessDataListener();

        if (this.googleApiClient != null && this.googleApiClient.isConnected()) {
            this.googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTH_REQUEST) {
            authInProgress = false;

            if (resultCode == RESULT_OK) {

                if (!this.googleApiClient.isConnecting() && !this.googleApiClient.isConnected()) {
                    this.googleApiClient.connect();
                    Log.d(TAG, "onActivityResult googleApiClient.connect() attempted in background");

                }
            }
        }
    }

    private synchronized void addContentToView(final float value) {


        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (spinner.getVisibility() == View.VISIBLE)
                    spinner.setVisibility(View.INVISIBLE);

                Log.d(TAG,"Heart Beat Rate Value : " + value);
                textMon.setText("Heart Beat Rate Value : " + value);

/*                if (value >= 80) {
                    textMon2.setText("심박수가 80보다 높습니다.");
                } else {
                    textMon2.setText("심박수가 80보다 낮습니다.");
                }*/
            }
        });
    }
}

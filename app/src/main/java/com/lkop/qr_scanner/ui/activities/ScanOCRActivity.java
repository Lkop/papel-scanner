package com.lkop.qr_scanner.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lkop.qr_scanner.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;


public class ScanOCRActivity extends AppCompatActivity {

    private Context context = this;
    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;
    boolean key = false;

    private static final String TAG = "ScanOCR_Activity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_new);

        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);

        startCameraSource();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ScanOCRActivity.this, new String[]{Manifest.permission.CAMERA}, requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();

                    if (items.size() != 0 ){

                            mTextView.post(new Runnable() {
                                @Override
                                public void run() {

                                    StringBuilder stringBuilder = new StringBuilder();

                                    for (int i = 0; i < items.size(); i++) {
                                        TextBlock item = items.valueAt(i);
                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                    }
                                    mTextView.setText(stringBuilder.toString());

                                    int am_check = stringBuilder.toString().indexOf("A.M.:");
                                    String[] am = new String[19 - 6];

                                    if (am_check != -1) {
                                        for (int i = 0; i < 19 - 6; i++) {

                                            am[i] = stringBuilder.substring(am_check + 6 + i, am_check + 6 + i + 1);
                                        }

                                        boolean numeric = true;

                                        for (int i = 0; i < 19 - 6; i++) {

                                            try {
                                                Integer.parseInt(am[i]);

                                            } catch (NumberFormatException e) {

                                                numeric = false;
                                            }

                                        }

                                        if (numeric) {

                                            mCameraSource.stop();

                                            //string array to string
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            for(int i = 0; i < am.length; i++) {
                                                stringBuilder2.append(am[i]);
                                            }

                                            //open only once
                                            if (!key) {

                                                key = true;

                                                Intent intent = new Intent(getApplicationContext(), ClassroomActivity.class);

                                                //not opening the activity twice
                                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                intent.putExtra("openFragment", "FragmentMatchPassId");
                                                intent.putExtra("pass_id_arg", getIntent().getStringExtra("pass_id_arg"));
                                                intent.putExtra("am_arg", stringBuilder2.toString());
                                                startActivity(intent);

                                                finish();
                                            }

                                            //ExecutorService executorService = Executors.newSingleThreadExecutor();
                                            //Future longRunningTaskFuture = executorService.submit(this);

                                            // At some point in the future, if you want to kill the task:
                                            //longRunningTaskFuture.cancel(true);


                                            //Fragment fr = new FragmentMatchPassId();
                                            //Bundle bdl = new Bundle();
                                            //dl.putString("pass_id_arg", getIntent().getStringExtra("pass_id_arg"));
                                            //bdl.putString("am_arg", am.toString());
                                            //fr.setArguments(bdl);


                                            //startFragment(fr);

                                            //add_btn.setVisibility(View.VISIBLE);

                                        }

                                    }

                                }
                            });
                    }
                }
            });
        }
    }
}

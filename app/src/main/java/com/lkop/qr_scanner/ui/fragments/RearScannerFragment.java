package com.lkop.qr_scanner.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import com.example.lkop.qr_scanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RearScannerFragment extends Fragment {

    private View view;
    private PreviewView camera_previewview;
    private ImageView camera_imageview;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private BarcodeScanner barcode_scanner;
    private TextRecognizer text_recognizer;
    private String pass_id_value, am_value;
    private Rect last_qr_rect, last_text_rect;
    private boolean onetime_flag = false;
    private boolean is_bitmap_full = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rear_scanner_fragment, container, false);

        camera_previewview = (PreviewView) view.findViewById(R.id.camera_previewview);
        camera_imageview = (ImageView) view.findViewById(R.id.camera_imageview);

        last_qr_rect = new Rect();
        last_text_rect = new Rect();

        initCamera();

        return view;
    }
    private void initCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(view.getContext());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(view.getContext()));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(camera_previewview.getSurfaceProvider());

        //initialize analysis
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcode_scanner = BarcodeScanning.getClient(options);
        text_recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                // enable the following line if RGBA output is needed.
                //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setTargetResolution(new Size(720, 1280))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(view.getContext()), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
                if (mediaImage != null) {
                    InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                    Task<List<Barcode>> barcode_result = barcode_scanner.process(image)
                            .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                                @Override
                                public void onSuccess(List<Barcode> barcodes) {
                                    if (onetime_flag == true){
                                        return;
                                    }
                                    for (Barcode barcode: barcodes) {
                                        if (barcode.getFormat() == Barcode.FORMAT_QR_CODE && barcode.getValueType() == Barcode.TYPE_TEXT) {
                                            last_qr_rect = barcode.getBoundingBox();
                                            drawPreviewRects(last_qr_rect, last_text_rect);

                                            pass_id_value = barcode.getRawValue();
                                            try {
                                                Long.parseLong(pass_id_value);
                                            } catch (NumberFormatException e) {
                                                pass_id_value = null;
                                            }
                                            if (pass_id_value != null && am_value != null) {
                                                sendResults();
                                            }
                                            onetime_flag = true;
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    imageProxy.close();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Barcode>> task) {
                                    imageProxy.close();
                                }
                            });

                    Task<Text> text_result = text_recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            if (onetime_flag == false){
                                return;
                            }

                            for (Text.TextBlock block : visionText.getTextBlocks()) {
                                for (Text.Line line : block.getLines()) {
                                    String line_text = line.getText();

                                    int am_check = line_text.indexOf("A.M.:");
                                    if (am_check != -1) {
                                        last_text_rect = line.getBoundingBox();
                                        drawPreviewRects(last_qr_rect, last_text_rect);

                                        String[] am = new String[19 - 6];

                                        for (int i = 0; i < 19 - 6; i++) {
                                            am[i] = line_text.substring(am_check + 6 + i, am_check + 6 + i + 1);
                                        }

                                        am_value = TextUtils.join("", am);
                                        try {
                                            Long.parseLong(am_value);
                                        } catch (NumberFormatException e) {
                                            am_value = null;
                                        }
                                        if (pass_id_value != null && am_value != null) {
                                            sendResults();
                                        }
                                        onetime_flag = false;
                                    }
                                }
                            }
                            imageProxy.close();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            imageProxy.close();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Text>() {
                        @Override
                        public void onComplete(@NonNull Task<Text> task) {
                            imageProxy.close();
                        }
                    });;
                }

            }
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    private void drawPreviewRects(Rect qr_rect, Rect text_rect) {
        Bitmap bitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint qr_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        qr_paint.setColor(Color.RED);
        qr_paint.setStyle(Paint.Style.STROKE);
        qr_paint.setStrokeWidth(4);
        canvas.drawRect(qr_rect.left-10, qr_rect.top-20, qr_rect.right+10, qr_rect.bottom+10, qr_paint);

        Paint text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        text_paint.setColor(Color.WHITE);
        text_paint.setStyle(Paint.Style.STROKE);
        text_paint.setStrokeWidth(4);
        canvas.drawRect(text_rect.left-20, text_rect.top-20, text_rect.right, text_rect.bottom, text_paint);

        camera_imageview.setImageBitmap(bitmap);
    }

    private void sendResults() {
        barcode_scanner.close();
        text_recognizer.close();
        Bundle bundle = new Bundle();
        bundle.putString("am", am_value);
        bundle.putString("pass_id", pass_id_value);
        getParentFragmentManager().setFragmentResult("match_pass_id_response", bundle);
    }

}

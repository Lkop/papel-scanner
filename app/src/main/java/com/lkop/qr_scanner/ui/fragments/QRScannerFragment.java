package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.lkop.qr_scanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import java.util.Collections;
import java.util.List;

public class QRScannerFragment extends Fragment {

    private View view;
    private DecoratedBarcodeView barcode_view;
    private String message;

    public QRScannerFragment(String message) {
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.qr_scanner_fragment, container, false);
        barcode_view = view.findViewById(R.id.scanner_area_scanner_fragment);

        barcode_view.setStatusText(message);
        barcode_view.getViewFinder().setLaserVisibility(false);
        barcode_view.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(Collections.singletonList(BarcodeFormat.QR_CODE)));
        barcode_view.getBarcodeView().decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String qr_code = result.getText();
                Bundle bundle = new Bundle();
                bundle.putString("qr_text", qr_code);
                getParentFragmentManager().setFragmentResult("qr_scanner_response", bundle);
            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                for(ResultPoint rp : resultPoints) {
                    barcode_view.getViewFinder().addPossibleResultPoint(rp);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        barcode_view.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcode_view.pauseAndWait();
    }
}
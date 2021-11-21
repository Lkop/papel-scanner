package com.lkop.qr_scanner.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.lkop.qr_scanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import java.util.Collections;
import java.util.List;

public class ScannerFragment extends Fragment {

    private View view;
    private DecoratedBarcodeView barcode_view;

    public ScannerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.scanner_fragment, container, false);
        barcode_view = view.findViewById(R.id.scanner_area_scanner_fragment);

        barcode_view.setStatusText("Σκανάρετε το QR από το πάσο");
        barcode_view.getViewFinder().setLaserVisibility(false);
        barcode_view.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(Collections.singletonList(BarcodeFormat.QR_CODE)));
        barcode_view.getBarcodeView().decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_LONG).show();
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
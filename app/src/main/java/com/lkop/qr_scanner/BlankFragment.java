package com.lkop.qr_scanner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lkop.qr_scanner.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class BlankFragment extends Fragment {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        barcodeScannerView = getView().findViewById(R.id.barcodee);
        //barcodeScannerView.setTorchListener(this);


        capture = new CaptureManager(getActivity(), barcodeScannerView);
        capture.initializeFromIntent(getActivity().getIntent(), savedInstanceState);

        capture.decode();

//        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
//
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
//        integrator.getCaptureActivity(BlankFragment);
//        integrator.setPrompt("Scan a barcode");
//        integrator.setCameraId(0);  // Use a specific camera of the device
//        integrator.setBeepEnabled(false);
//        integrator.setBarcodeImageEnabled(true);
//        integrator.initiateScan();



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }


}

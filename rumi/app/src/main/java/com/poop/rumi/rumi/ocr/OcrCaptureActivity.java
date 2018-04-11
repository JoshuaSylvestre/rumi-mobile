package com.poop.rumi.rumi.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.poop.rumi.rumi.MainActivity;
import com.poop.rumi.rumi.Receipt;
import com.poop.rumi.rumi.camera.CameraSource;
import com.poop.rumi.rumi.camera.CameraSourcePreview;
import com.poop.rumi.rumi.camera.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;
import com.poop.rumi.rumi.R;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class OcrCaptureActivity extends AppCompatActivity{

    private static final String TAG = "OcrCaptureActivity";

    private final Context mContext = this;

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    //
    private static final String IMAGE_DIRECTORY_NAME = "rumiImages";

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int EXTERN_STORAGE_PERM = 3;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";

    //Camera & GraphicOverlay
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    //Buttons for image validation
    private ImageButton xMark;
    private ImageButton tickMark;
    // for purposes of making sure user taps either of the above buttons before being
    // able to tap (and thus remove) TextBlocks
    boolean validated = false;

    //Capture Button
    private FloatingActionButton captureButton;


    // Helper objects for detecting taps
    private GestureDetector gestureDetector;

    private String imagePath;

    private Receipt mReceipt;

    private int promptDialogStage = 0;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ocr_capture);


        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);

        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        int rd = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(rd == PackageManager.PERMISSION_DENIED) {
            requestStoragePermission();
        }


        gestureDetector = new GestureDetector(this, new CaptureGestureListener());

        xMark = findViewById(R.id.xMark);
        tickMark = findViewById(R.id.tickMark);

        captureButton = findViewById(R.id.btnCapturePicture);

        toDisplayButtons(true, false, false);
        //displaySubmitAndSwitches(false);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                Toast.makeText(mPreview.getContext(), R.string.picture_taken, Toast.LENGTH_SHORT)
                        .show();

                toDisplayButtons(false, true, true);

                // TAG: COMMENT_ABE
                // Doesn't save picture yet but instead saves byte [] to CameraSource field tempImage.
                // Picture is saved upon user validation prompted through the tickMark button.
                // takePicture() calls freeze() to freeze SurfaceView and TextDetector/GraphicOverlay
                mCameraSource.takePicture(null, null);

                validateImgWithUser();

            }
        });


    }


    private void validateImgWithUser() {

        tickMark.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                Toast.makeText(OcrCaptureActivity.this, "Tapped tick mark", Toast.LENGTH_SHORT).show();

                // Create image file and save image
                File fl = createUniqueFileForImage();

                assert fl != null;
                imagePath = fl.getAbsolutePath();

                try {
                    mCameraSource.saveImage(imagePath);
                } catch(Exception ignored)
                {
                    Log.d(TAG, "Failed to save image.", ignored);
                } finally {

                    toDisplayButtons(false, true, false);

                    startDialogSequence();

                    //displaySubmitAndSwitches(true);
                    validated = true;
                }

            }

        });

        xMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(OcrCaptureActivity.this, "Tapped x mark", Toast.LENGTH_SHORT).show();

                recreate(); // .. this activity
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createUniqueFileForImage() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }

            Log.d(IMAGE_DIRECTORY_NAME, "Directory doesn't exist, so it was created");
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    // Begins prompting user to tap on items the prices by calling invokeDialog()
    // Ideally, would include instructions to increase accuracy of parsing algorithms
    // implemented in Receipt class
    private void startDialogSequence(){

        mReceipt = new Receipt();

        // Would be nice to highlight words that change. eg, ITEMS, PRICES, etc.
        final String [] promptMsg = {"TAP ON ITEMS PLS AND TAP TICK MARK WHEN FINISHED",
                                     "TAP ON PRICES PLS AND TAP TICK MARK WHEN FINISHED",
                                     "TAP ON STORE NAME PLS AND TAP TICK MARK WHEN FINISHED"};

        invokeDialog(promptMsg[promptDialogStage++]);

        if(promptDialogStage < 3) {
            tickMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(OcrCaptureActivity.this, "Tapped tick mark", Toast.LENGTH_SHORT).show();

                    invokeDialog(promptMsg[promptDialogStage++]);

                }

            });
        }

        else
        {
            Toast.makeText(OcrCaptureActivity.this, "DONE", Toast.LENGTH_SHORT).show();

//            Intent i = new Intent(mContext, OcrCaptureActivity.class);
//            i.putExtra("RECEIPT", mReceipt);


            //TODO: Push to next activity
        }


    }

    private void invokeDialog(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(OcrCaptureActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alertdialog_view,null);

        // Specify alert dialog is not cancelable/not ignorable
        builder.setCancelable(false);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);

        TextView dialog_message = (TextView) dialogView.findViewById(R.id.dialog_title);
        dialog_message.setText(msg);
        //        final EditText et_name = (EditText) dialogView.findViewById(R.id.et_name);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();



            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss/cancel the alert dialog
                //dialog.cancel();
                dialog.dismiss();
                recreate();

                Toast.makeText(getApplication(),
                        "No button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Display the custom alert dialog on interface
        dialog.show();
    }



    private void toDisplayButtons(boolean displayCaptureButton, boolean displayTickMark, boolean displayXMark){

        if(displayCaptureButton)
            captureButton.setVisibility(View.VISIBLE);
        else
            captureButton.setVisibility(View.INVISIBLE);

        if(displayTickMark)
            tickMark.setVisibility(View.VISIBLE);
        else
            tickMark.setVisibility(View.INVISIBLE);

        if(displayXMark)
            xMark.setVisibility(View.VISIBLE);
        else
            xMark.setVisibility(View.INVISIBLE);

    }
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void requestStoragePermission() {
        Log.w(TAG, "Storage permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, EXTERN_STORAGE_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        EXTERN_STORAGE_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_storage_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        boolean b = gestureDetector.onTouchEvent(e);

        return b || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        Log.d("TESTING", "onResume");
        super.onResume();
        startCameraSource();

        toDisplayButtons(true, false, false);
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {

        Log.d("TESTING", "onPause");

        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        Log.d("TESTING", "onDestroy");

        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }
  
    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case RC_HANDLE_CAMERA_PERM:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Camera permission granted - initialize the camera source");
                    // We have permission, so create the camerasource
                    boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
                    boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
                    createCameraSource(autoFocus, useFlash);
                    return;
                }

                Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

                DialogInterface.OnClickListener camListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                };

                AlertDialog.Builder camBuilder = new AlertDialog.Builder(this);
                camBuilder.setTitle("rumi")

                        .setMessage(R.string.no_camera_permission)
                        .setPositiveButton(R.string.ok, camListener)
                        .show();
                return;

            case EXTERN_STORAGE_PERM:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    return;
                }

                Log.e("value", "Permission Denied, You cannot use local drive .");
                DialogInterface.OnClickListener storageListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                };

                AlertDialog.Builder storageBuilder = new AlertDialog.Builder(this);
                storageBuilder.setTitle("rumi")

                        .setMessage(R.string.no_storage_permission)
                        .setPositiveButton(R.string.ok, storageListener)
                        .show();
                return;



            default:
                Log.d(TAG, "Got unexpected permission result: " + requestCode);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }

    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private boolean onTap(float rawX, float rawY) {

        if(validated && (tickMark.getVisibility() == View.INVISIBLE || xMark.getVisibility() == View.INVISIBLE)) {
            OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);

            TextBlock text = null;
            if (graphic != null) {
                text = graphic.getTextBlock();

                if (text != null && text.getValue() != null) {

//                    Log.d(TAG, "TEXT TAPPED! : " + text.getValue());
//                    Toast.makeText(mPreview.getContext(), text.getValue(), Toast.LENGTH_LONG)
//                            .show();

                    // Adding to items vs prices of mReceipt
                    if(promptDialogStage == 1){
                        mReceipt.addItems(text.getValue());

                        if(mReceipt.numItems()!= 0)
                            Toast.makeText(mPreview.getContext(), mReceipt.printItems(), Toast.LENGTH_LONG)
                            .show();

                    }
                    else if(promptDialogStage == 2){
                        mReceipt.addPrices(text.getValue());

                        if(mReceipt.numPrices()!= 0)
                            Toast.makeText(mPreview.getContext(), mReceipt.printPrices(), Toast.LENGTH_LONG)
                                .show();
                    }
                    else{
                        mReceipt.setStoreName(text.getValue());

                       Toast.makeText(mPreview.getContext(), mReceipt.getStoreName(), Toast.LENGTH_LONG)
                                    .show();
                    }

                    mGraphicOverlay.remove(graphic);

                } else {
                    Log.d(TAG, "text data is null");
                }

            } else {
                Log.d(TAG, "no text detected");
            }
            return text != null;
        }
        else {
            Toast.makeText(mPreview.getContext(), R.string.VerifyFirst, Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Log.d("TAP LOCATION","("+ e.getRawX()+" , "+e.getRawY()+")");
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }
}
package pappu.com.objectdetection.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pappu.com.objectdetection.R;
import pappu.com.objectdetection.datamodel.CameraData;
import pappu.com.objectdetection.datamodel.CascadeData;
import pappu.com.objectdetection.datamodel.CascadeDataCollection;
import pappu.com.objectdetection.datamodel.CascadeType;
import pappu.com.objectdetection.datamodel.Orientation;
import pappu.com.objectdetection.glview.GlSurfaceView;
import pappu.com.objectdetection.java_jni.Facedetection;
import pappu.com.objectdetection.renderer.ImageRenderer;
import pappu.com.objectdetection.utils.FileUtils;


/**
 * Created by pappu on 12/5/16.
 */


public class CameraActivity extends Activity implements SensorEventListener, Camera.PreviewCallback,View.OnClickListener{

    private Camera mCamera = null;
    private final static int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private FrameLayout previewLayout;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    int mFrameHeight,mFrameWidth;
    public Orientation orientation = Orientation.PotraitUp;
    SurfaceTexture surfaceTexture;

    private  boolean startVideo = true;
    private Button imageCaptureButton,videoCaptureButton;
    private GlSurfaceView glSurfaceView;
    private ImageRenderer imageRenderer;
    private Facedetection facedetection = new Facedetection();
    private int cpuCoreNumber = 0;
    private List<CascadeData> cascadeDataList;
    private TextView labelTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_activity);
        senSensorManager = (SensorManager) this.getSystemService(
                Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        cpuCoreNumber = FileUtils.getNumberOfCores();
        previewLayout = (FrameLayout)findViewById(R.id.preview);
        labelTextview = (TextView)findViewById(R.id.label);
        videoCaptureButton = (Button)findViewById(R.id.capture_video);
        videoCaptureButton.setOnClickListener(this);
        setCamera();

        glSurfaceView = new GlSurfaceView(this);
        imageRenderer = new ImageRenderer(this,mFrameWidth,mFrameHeight);
        imageRenderer.changeCameraOrientation(CameraData.backCamera);
        glSurfaceView.setRenderer(imageRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        previewLayout.addView(glSurfaceView);
        cascadeDataList = CascadeDataCollection.getCascadeDataList();
        moveCascadeDataToInternalDir();
        deserialize();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.capture_video:
                if(startVideo){
                    videoCaptureButton.setText("StopCapture");
                    startVideo = false;
                }
                else {
                    videoCaptureButton.setText("VideoCapture");
                    startVideo = true;
                }
                break;
        }
    }


    private void moveCascadeDataToInternalDir() {

        try {
            File cascadeDir = this.getFilesDir();


            Log.d(" desirealize  ",""  + cascadeDataList.size());
            for (int i = 0; i < cascadeDataList.size(); i++) {
                CascadeData cascadeData = cascadeDataList.get(i);
                File cascadeFile = new File(cascadeDir, cascadeData.getCascadeFileName());
                cascadeData.setCascadeFilePath(cascadeFile.getAbsolutePath());
                if (cascadeFile.exists()) {
                    continue;
                }

                InputStream is = this.getResources().openRawResource(cascadeData.getResourceId());
                FileOutputStream os = null;
                os = new FileOutputStream(cascadeFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();
            }
            //Log.d(" desirealize  ",""  + facedetection.deserialize(mCascadeFile.getAbsolutePath()));
            //cascadeDir.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deserialize() {
        int [] ids = new int[cascadeDataList.size()];
        String [] xmlFilePaths = new String[cascadeDataList.size()];
        for (int i = 0; i < cascadeDataList.size(); i++) {
            CascadeData cascadeData = cascadeDataList.get(i);
            ids[i] = cascadeData.getCascadeType().getId();
            xmlFilePaths[i] = cascadeData.getCascadeFilePath();
        }
        facedetection.deserialize(ids,xmlFilePaths);
    }



    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        imageRenderer.updateYUVBuffers(data);
        glSurfaceView.requestRender();
        int[] detected =facedetection.faceDetect(data,mFrameWidth,mFrameHeight,orientation.value(),cpuCoreNumber,true);
        if(detected!=null){

            for(int i=0;i<detected.length;i++){
                ArrayList<long[]> faceRect = facedetection.getRectangle(detected[i]);
                imageRenderer.drawFaceRect(faceRect,orientation);

                if(detected[i]==CascadeType.FACE_DETECTION.getId()){
                    labelTextview.setText("Face");
                    Log.d(" detectObject  ","detected Face " );
                }else if(detected[i]==CascadeType.CAR_DETECTION.getId()){
                    Log.d(" detectObject  ","detected Car " );
                    labelTextview.setText("Car");
                }else if(detected[i]==CascadeType.FULL_BODY_DETECTION.getId()){
                    labelTextview.setText("Pedastrin");
                }
            }
        }
        else {
            labelTextview.setText("None");
        }

    }


    void setCamera(){

        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size previewSize = params.getPreviewSize() ;

        List<Camera.Size> prevsizeArray =  params.getSupportedPreviewSizes();

        for(int i=0;i<prevsizeArray.size();i++){

            int width = prevsizeArray.get(i).width,height = prevsizeArray.get(i).height;
            float ratio = (float)width/height;
            if(ratio==(float)16/9 && height<=720){
                previewSize.width = width;
                previewSize.height = height;
                break;
            }
//            Log.d("resolution",""+previewSize.height+"  "+previewSize.width);
        }

//        Log.d("resolution","final    : "+previewSize.height+"  "+previewSize.width);
        mFrameHeight = previewSize.height;
        mFrameWidth =previewSize.width;
        params.setPreviewSize(mFrameWidth,mFrameHeight);

        mCamera.setParameters(params);
        try {
            surfaceTexture = new SurfaceTexture(0);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (Exception error ) {
            error.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if ((sensorEvent.values[0] < 3 && sensorEvent.values[0] > -3)
                    && (sensorEvent.values[1] < 3 && sensorEvent.values[1] > -3)) {
               orientation = Orientation.Flat;

            } else {
                if (Math.abs(sensorEvent.values[1]) > Math
                        .abs(sensorEvent.values[0])) {

                    if (sensorEvent.values[1] < 0) {
                       orientation = Orientation.PotraitDown;

                    } else {
                        orientation = Orientation.PotraitUp;
                    }

                } else {
                    if (sensorEvent.values[0] < 0) {
                        orientation = Orientation.LandscapeDown;

                    } else {
                        orientation = Orientation.LandscapeUp;

                    }

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(mCameraId);
        } catch (Exception error) {
            error.printStackTrace();
            Log.e("mainactivity", "[ERROR] Camera open failed." + error.getMessage());
        }

        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
        videoCaptureButton.setText("VideoCapture");
        startVideo = true;
    }

    @Override
    protected void onDestroy() {
        senSensorManager.unregisterListener(this);
        releaseCamera();
        super.onDestroy();
    }

    public void releaseCamera() {
        if (mCamera != null) {
            try{
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                surfaceTexture.release();
                mCamera.release();
                mCamera = null;
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

}

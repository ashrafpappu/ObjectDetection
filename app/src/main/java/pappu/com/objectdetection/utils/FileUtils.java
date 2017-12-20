/*
 * Copyright 2016 Tzutalin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pappu.com.objectdetection.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.regex.Pattern;


public class FileUtils {

    public static String getFaceShapeModelPath(Context context) {
        File sdcard = Environment.getExternalStorageDirectory();
        String folder_main = context.getPackageName();
        File f = new File(sdcard.getAbsolutePath(), folder_main);
        if (!f.exists()) {
            if (f.mkdirs()) {

                Log.d("Constants","Directory creation failed: "+folder_main);
            }
            else {

                Log.d("Constants","Directory creation successfull: "+folder_main);
            }

        }
        String targetPath = sdcard.getAbsolutePath() + File.separator + context.getPackageName() + File.separator + "shape_predictor_68_face_landmarks.dat";
        return targetPath;
    }


    @NonNull
    public static final void copyFileFromRawToOthers(@NonNull final Context context, @RawRes int id, @NonNull final String targetPath) {
        InputStream in = context.getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(targetPath);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Point getScreenResolution(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static String readRawTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString().trim();
    }

    public static  void saveImage(byte[] data,int counter,Camera.Parameters parameters){
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, ""+counter+".jpg");
        try{

//            YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
//                    parameters.getPreviewSize().width,  parameters.getPreviewSize().height, null);


            YuvImage image = new YuvImage(data, ImageFormat.NV21,
                   720,  1280, null);


            FileOutputStream filecon = new FileOutputStream(file);
            image.compressToJpeg(
                    new Rect(0, 0, image.getWidth(), image.getHeight()), 90,
                    filecon);

        }catch (Exception e){
           // LOG.Debug("File",""+e);
        }
    }
    public static String saveBitmapImage(Bitmap finalBitmap, Context mContext){

        String folder_main = AppUtils.getApplicationName(mContext);
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), folder_main + "/images");
        if (!f.exists()) {
            f.mkdirs();
        }
        Calendar rightNow = Calendar.getInstance();
        String fileName = "/IMG_"+rightNow.get(Calendar.YEAR)+rightNow.get(Calendar.MONTH)+rightNow.get(Calendar.DAY_OF_MONTH)+"_"+ System.currentTimeMillis()+".jpg";

        File file = new File(f, fileName);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String filepath = file.getAbsolutePath();
        return filepath;

    }

    public static  int getNumberOfCores() {

        if(Build.MODEL.equalsIgnoreCase("Nexus 5X")){
            return 4;
        }

        if(Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        }
        else {
            // Use saurabh64's answer
            return getNumCoresOldPhones();
        }
    }

    private static  int getNumCoresOldPhones() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    public  static String getTemprorayVideoFilePath(){
        final File OUTPUT_DIR = Environment.getExternalStorageDirectory();
        return new File(OUTPUT_DIR, "Video.mp4").toString();
    }

}

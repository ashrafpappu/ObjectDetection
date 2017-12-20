package pappu.com.objectdetection.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import pappu.com.objectdetection.datamodel.Orientation;
import pappu.com.objectdetection.datamodel.PreviewInfo;


/**
 * Created by ipvision on 3/14/2017.
 */

public class AppUtils {
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
    public static PreviewInfo getadjustedPreview(int parentViewWidth, int parentViewHeight, int previewWidth, int previewHeight, Orientation orientation) {
        PreviewInfo previewInfo = new PreviewInfo();
        int minScreenDimension,maxScreenDimension, minPreviewDimension,maxPreviewDimension;
        minScreenDimension = parentViewWidth < parentViewHeight ? parentViewWidth : parentViewHeight;
        minPreviewDimension = previewWidth < previewHeight ? previewWidth : previewHeight;
        maxPreviewDimension = previewWidth > previewHeight ? previewWidth : previewHeight;
        double ratio = (double) minScreenDimension / minPreviewDimension;
        maxScreenDimension = (int) (maxPreviewDimension * ratio);
        previewInfo.preiviewHeight = maxScreenDimension;
        previewInfo.previewWidth = minScreenDimension;
        previewInfo.offsetX =(parentViewWidth-previewInfo.previewWidth)/2;
        previewInfo.offsetY = (parentViewHeight-previewInfo.preiviewHeight)/2;
        return previewInfo;
    }
}

package pappu.com.objectdetection.glview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

/**
 * Created by Pappu on 8/24/2016.
 */
public class GlSurfaceView extends GLSurfaceView {



    public GlSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

    }

}

package pappu.com.objectdetection.renderer;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pappu.com.objectdetection.datamodel.GLLocationData;


/**
 * Created by pappu on 5/13/17.
 */

public class YUVGLRender {
    // y and uv texture buffers
    public ByteBuffer yBuffer, uvBuffer,texturebuffer;
    // image height and width
    private int width = 0, height = 0;
    final private float bytePerPixel = 1.5f;
    // true when a valid image data is set. default value false.
    private boolean render = false;
    // y texture handle
    private int[] yTexture = new int[1];
    // uv texture handle
    private int[] uvTexture = new int[1];
    // texture coordinate and vertices buffers

    // texture for blending
    private int[] blend_texture = new int[1];

    public YUVGLRender(Context context) {
    }

    public void updateYUVBuffers(final byte[] imageBytes, int height, int width) {


        // reinitialize texture buffers if width or height changes
        final boolean resolutionChanged = this.width != width || this.height != height;
        if (resolutionChanged) {
            this.width = width;
            this.height = height;
            final int numberOfPixels = this.height * this.width;
            this.yBuffer = ByteBuffer.allocateDirect(numberOfPixels);
            this.yBuffer.order(ByteOrder.nativeOrder());
            this.uvBuffer = ByteBuffer.allocateDirect(numberOfPixels / 2);
            this.uvBuffer.order(ByteOrder.nativeOrder());
        }

        final int numberOfPixels = this.height * this.width;
        final int numberOfExpectedBytes = (int)(numberOfPixels * this.bytePerPixel);
        if (imageBytes==null || imageBytes.length != numberOfExpectedBytes) {

            render = false;

        }
        else {
            // put image bytes into texture buffers
            yBuffer.put(imageBytes, 0, numberOfPixels);
            yBuffer.position(0);
            uvBuffer.put(imageBytes, numberOfPixels, numberOfPixels/2);
            uvBuffer.position(0);
            render = true;
        }
    }

    public boolean getRenderState(){
        return render;
    }


    public void generateYUVTexture(){


        // generate y texture
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glGenTextures(1, yTexture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture[0]);

        // generate uv texture
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glGenTextures(1, uvTexture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTexture[0]);

    }


    public void dataUpLoadAndupdateYUVTexture(GLLocationData glLocationData){
        // create and update y texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture[0]);
        GLES20.glUniform1i(glLocationData.yTextureLocation, 1);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, this.width,
                this.height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, this.yBuffer);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // create and update uv texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTexture[0]);
        GLES20.glUniform1i(glLocationData.uvTextureLocation, 2);


        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, this.width / 2,
                this.height / 2, 0, GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, this.uvBuffer);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


    }



}

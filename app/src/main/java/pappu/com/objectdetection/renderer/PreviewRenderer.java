package pappu.com.objectdetection.renderer;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pappu.com.objectdetection.R;
import pappu.com.objectdetection.datamodel.CameraData;
import pappu.com.objectdetection.datamodel.GLLocationData;
import pappu.com.objectdetection.helper.GLHelper;


/**
 * Created by pappu on 1/25/17.
 */


/**
 * IMPORTANT - Please read before changing
 *
 * This class render a NV21 (YV12) image byte array in GlSurfaceView using OpenGl.
 * NV21 image format has 12 bits per pixel out of which 8 bits are for luminance (Y) and 4 bits
 * are for chrominance (UV). So yBuffer size is same and uvBuffer size is of half
 * of number of pixels. yTexture height and widht are also same as image height and width.
 * First 2/3 of the input image array are Y values and last 1/3 are uv in the
 * order v0u0v1u1... (altering v and u values) so on. So GL_LUMINANCE and GL_LUMINANCE_ALPHA format
 * are used to pass yBuffer and uvBuffer respectively and fragment_shader1 takes U value from alpha channel
 * and V value from red channel (could be green or blue channel with same result). uvTexture height
 * and width are also 1/4 of the original image height and width
 *
 * GL_TEXTURE0 + 1 (GL_TEXTURE1) and GL_TEXTURE0 + 2 (GL_TEXTURE2) must be used for yTexture and uvTexture.
 * If GL_TEXTURE0 is used for yTexture, it doesn't work in some devices.
 */



public class PreviewRenderer {

    private int shaderProgram;
    short[] indices = new short[] {0,1,2,0,2,3};
    // texture coordinate and camera vertices buffers
    private FloatBuffer previewTexCoordBuffer, previewVertexBuffer;
    // indices buffer
    private ShortBuffer previewIndexBuffer;


    // position attribute location handle in vertex shader
    private int positionLocation;
    // texture coordinate attribute location handle in vertex shader
    private int textureCoordinateLocation;

    private GLLocationData glLocationData;

    private YUVGLRender yuvglRender;

    private Context context;


    private float[] verticesFrontCamera = new float[] {

            1.f, -1.f,
            -1.f, -1.f,
            -1.f, 1.f,
            1.f, 1.f

    };
    private float[] verticesnexus5xCamera = new float[] {
            -1.f, -1.f,
            1.f, -1.f,
            1.f, 1.f,
            -1.f, 1.f
    };
    private float[] verticesBackCamera = new float[] {
            1.f, 1.f,
            -1.f, 1.f,
            -1.f, -1.f,
            1.f, -1.f
    };

    private float[] texCoords = new float[] {
            0.f, 0.f,
            0.f, 1.f,
            1.f, 1.f,
            1.f, 0.f
    };

    public void setYuvglRender(YUVGLRender yuvglRender){
        this.yuvglRender = yuvglRender;
    }

    public PreviewRenderer(Context context){

        this.context = context;

        // initialize texture coordinate buffer
        ByteBuffer tcbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tcbb.order(ByteOrder.nativeOrder());
        previewTexCoordBuffer = tcbb.asFloatBuffer();
        previewTexCoordBuffer.put(texCoords);
        previewTexCoordBuffer.position(0);

        // initialize verticesFrontCamera buffer
        ByteBuffer vbb = ByteBuffer.allocateDirect(verticesFrontCamera.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        previewVertexBuffer = vbb.asFloatBuffer();
        previewVertexBuffer.put(verticesFrontCamera);
        previewVertexBuffer.position(0);

        // initialize indices buffer
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        previewIndexBuffer = ibb.asShortBuffer();
        previewIndexBuffer.put(indices);
        previewIndexBuffer.position(0);
    }

    public void changeCameraOrientation(int cameraId) {
        previewVertexBuffer.position(0);
        if (cameraId == CameraData.frontCamera) {
            previewVertexBuffer.put(verticesFrontCamera);
        } else if (cameraId == CameraData.backCamera) {
            previewVertexBuffer.put(verticesBackCamera);
        } else if (cameraId == CameraData.nexus5XCamera) {
            previewVertexBuffer.put(verticesnexus5xCamera);
        } else {
            // TODO
            // throw and exception. Invalid id.
        }
        previewVertexBuffer.position(0);
    }


    public void updateYUVBuffers(final byte[] imageBytes, int height, int width) {
        yuvglRender.updateYUVBuffers(imageBytes,height,width);
    }

    void intitPreviewShader(){
        updateShader();
        positionLocation = GLES20.glGetAttribLocation(shaderProgram, "a_position");
        textureCoordinateLocation = GLES20.glGetAttribLocation(shaderProgram, "a_texCoord");
        glLocationData = new GLLocationData();
        glLocationData.yTextureLocation = GLES20.glGetUniformLocation(shaderProgram, "y_texture");
        glLocationData.uvTextureLocation = GLES20.glGetUniformLocation(shaderProgram, "uv_texture");
        yuvglRender.generateYUVTexture();
    }

    private void updateShader() {
        shaderProgram = GLHelper.getShaderProgramm(context, R.raw.vertex_shader1, R.raw.fragment_shader1);
    }


    void renderPreview(){
        if (yuvglRender.getRenderState()) {

            GLES20.glUseProgram(shaderProgram);

            GLES20.glVertexAttribPointer(positionLocation, 2,
                    GLES20.GL_FLOAT, false,
                    0, previewVertexBuffer);

            GLES20.glVertexAttribPointer(textureCoordinateLocation, 2, GLES20.GL_FLOAT,
                    false,
                    0, previewTexCoordBuffer);

            GLES20.glEnableVertexAttribArray(positionLocation);
            GLES20.glEnableVertexAttribArray(textureCoordinateLocation);

            yuvglRender.dataUpLoadAndupdateYUVTexture(glLocationData);

            // render image
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                    GLES20.GL_UNSIGNED_SHORT, previewIndexBuffer);

            GLES20.glDisableVertexAttribArray(positionLocation);
            GLES20.glDisableVertexAttribArray(textureCoordinateLocation);
        }
    }

}

package pappu.com.objectdetection.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pappu.com.objectdetection.R;
import pappu.com.objectdetection.helper.GLHelper;
import pappu.com.objectdetection.utils.FileUtils;


public class DrawRect {
    private ShortBuffer mIndexBuffer;
    private FloatBuffer mVertexBuffer;
    private int mShaderProgram;
    private Context mContext;
    private int mAttribPosition;

    private short indices[] = {
            0, 1, 4,
            0, 5, 11,
            0, 3, 11,
            1, 4, 6,
            1, 2, 7,
            2, 7, 9,
            2, 3, 8,
            3, 8, 10
    };

    private float vertices[];

    public DrawRect(Context context) {
        mContext = context;
        vertices = new float[24];
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(indices.length * 2);
        bb.order(ByteOrder.nativeOrder());
        mIndexBuffer = bb.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }

    public void loadShaders() {
        final int vertShader = GLHelper.loadShader(GLES20.GL_VERTEX_SHADER,
                FileUtils.readRawTextFile(mContext, R.raw.vertex_shader_rect_drawing));
        final int fragShader = GLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER,
                FileUtils.readRawTextFile(mContext, R.raw.fragment_shader_rect_drawing));
        mShaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mShaderProgram, vertShader);
        GLES20.glAttachShader(mShaderProgram, fragShader);
        GLES20.glLinkProgram(mShaderProgram);
        GLES20.glUseProgram(mShaderProgram);

        GLHelper.checkShaderProgrammError(mShaderProgram);
        mAttribPosition = GLES20.glGetAttribLocation(mShaderProgram, "a_position");
    }

    public void render() {

        GLES20.glUseProgram(mShaderProgram);
        GLES20.glEnableVertexAttribArray(mAttribPosition);
        GLES20.glVertexAttribPointer(mAttribPosition, 2,
                GLES20.GL_FLOAT, false,
                0, mVertexBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(mAttribPosition);
    }



    public void updateRect(final long[] rect, float width, float height) {



        float topLeftX = rect[0] / width;
        float topLeftY = 1.f - (rect[1] / height);
        float bottomRightX = rect[2] / width;
        float bottomRightY = 1.f - rect[3] / height;

        topLeftX = 2 * topLeftX - 1.f;
        topLeftY = 2 * topLeftY - 1.f;
        bottomRightX = 2 * bottomRightX - 1.f;
        bottomRightY = 2 * bottomRightY - 1.f;

        final float constVal = 0.01f;
        vertices[0] = topLeftX;
        vertices[1] = bottomRightY;
        vertices[8] = topLeftX - constVal;
        vertices[9] = bottomRightY;
        vertices[10] = topLeftX;
        vertices[11] = bottomRightY - constVal;

        vertices[2] = topLeftX;
        vertices[3] = topLeftY;
        vertices[12] = topLeftX - constVal;
        vertices[13] = topLeftY;
        vertices[14] = topLeftX;
        vertices[15] = topLeftY + constVal;

        vertices[4] = bottomRightX;
        vertices[5] = topLeftY;
        vertices[16] = bottomRightX + constVal;
        vertices[17] = topLeftY;
        vertices[18] = bottomRightX;
        vertices[19] = topLeftY + constVal;

        vertices[6] = bottomRightX;
        vertices[7] = bottomRightY;
        vertices[20] = bottomRightX + constVal;
        vertices[21] = bottomRightY;
        vertices[22] = bottomRightX;
        vertices[23] = bottomRightY - constVal;

        mVertexBuffer.position(0);
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }
}

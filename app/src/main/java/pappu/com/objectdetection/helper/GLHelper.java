package pappu.com.objectdetection.helper;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import pappu.com.objectdetection.utils.FileUtils;


/**
 * Created by pappu on 5/9/17.
 */

public class GLHelper {

    public static int getShaderProgramm(Context context, int vertexShaderRes, int fragShaderRes){

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                FileUtils.readRawTextFile(context, vertexShaderRes));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                FileUtils.readRawTextFile(context, fragShaderRes));


        int shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        GLES20.glUseProgram(shaderProgram);
        checkShaderProgrammError(shaderProgram);

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return shaderProgram;
    }



    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static  void checkShaderProgrammError(int mShaderProgram){

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mShaderProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("Render", "Could not link program: ");
            Log.e("Render", GLES20.glGetProgramInfoLog(mShaderProgram));
            GLES20.glDeleteProgram(mShaderProgram);
            mShaderProgram = 0;
        }
    }



}

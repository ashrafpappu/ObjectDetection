package pappu.com.objectdetection.helper;

import android.opengl.GLES20;

import java.nio.FloatBuffer;


public class GLRenderHelper {
    private int positionLoc;
    private int texCoordLoc;
    private int projectionMtrxLoc;
    private float[] projectionMtrx;

    public GLRenderHelper(int shaderProgram, float[] projectionMtrx) {
        this.projectionMtrx = projectionMtrx;
        this.positionLoc = GLES20.glGetAttribLocation(shaderProgram, "a_vPosition");
        this.texCoordLoc = GLES20.glGetAttribLocation(shaderProgram, "a_texCoord");
        this.projectionMtrxLoc = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix");
    }

    public void updateVertexAtrrib(FloatBuffer vertexBuffer, int size) {
        GLES20.glEnableVertexAttribArray(this.positionLoc);
        GLES20.glVertexAttribPointer(this.positionLoc, size,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
    }

    public void updateTexCoordAtrrib(FloatBuffer texCoordBuffer, int size) {
        GLES20.glEnableVertexAttribArray(this.texCoordLoc);
        GLES20.glVertexAttribPointer(this.texCoordLoc, size, GLES20.GL_FLOAT,
                false,
                0, texCoordBuffer);
    }


    public void updateProjectionMtrx() {
        GLES20.glUniformMatrix4fv(this.projectionMtrxLoc, 1, false, this.projectionMtrx, 0);
    }

    public void disableVertexAttrib() {
        GLES20.glDisableVertexAttribArray(this.positionLoc);
    }

    public void disableTexCoordAttrib() {
        GLES20.glDisableVertexAttribArray(this.texCoordLoc);
    }
}

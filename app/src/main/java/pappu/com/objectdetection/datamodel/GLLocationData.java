package pappu.com.objectdetection.datamodel;

/**
 * Created by pappu on 5/13/17.
 */

public class GLLocationData {
    // y_texture sampler2D location handle in fragment shader
    public int yTextureLocation;
    // uv_texture sampler2D location handle in fragment shader
    public int uvTextureLocation;

    //create new texture for blend
    public int blendTextureLocation;

    //resolution for display
    public int resolution;
}

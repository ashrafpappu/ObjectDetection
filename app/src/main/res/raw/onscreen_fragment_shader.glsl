precision mediump float;
varying vec2 v_texCoord;
uniform sampler2D u_texture;
void main() {

      vec4 texture = texture2D(u_texture, v_texCoord );
       gl_FragColor = vec4(texture.r/texture.a,texture.g/texture.a,texture.b/texture.a,texture.a);
 }

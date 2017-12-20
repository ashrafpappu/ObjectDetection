precision mediump float;

uniform sampler2D y_texture;
uniform sampler2D uv_texture;

varying vec2 v_texCoord;

void main () {
    float r, g, b, y, u, v;
    y = texture2D(y_texture, v_texCoord).r;
    vec4 uv = texture2D(uv_texture, v_texCoord);
    u = uv.a - 0.5;
    v = uv.r - 0.5;

    y=1.1643*(y-0.0625);

	r= (y+1.5958*v);
	g= (y-0.39173*u-0.81290*v);
	b= (y+2.017*u);
    gl_FragColor = vec4(r,g,b,1.0);
}
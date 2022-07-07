#ifdef GLES
// highp is necessary to not loose texture coordinate bits
precision highp float;
#endif

uniform mat4 u_mvp;
// uniform mat4 u_vp;

// factor to increase line width relative to scale
uniform float u_width;

// xy hold position, zw extrusion vector
attribute vec4 a_pos;

// colors for each a_pos vertex
attribute vec3 aVertexColor;

// common alpha for the vertex color
uniform float   uVertexColorAlpha;

// z axis, line is above/below ground
uniform float u_height;

// fragment values
varying vec2    v_st;
varying vec4    vFragmentColor;


void main() {

    // scale extrusion to u_width pixel
    // just ignore the two most insignificant bits
    vec2 dir = a_pos.zw;
    
    gl_Position = u_mvp * vec4(a_pos.xy + (u_width * dir), u_height, 1.0);

    // last two bits hold the texture coordinates
    v_st = abs(mod(dir, 4.0)) - 1.0;
    
    // transfer colors to the fragment shader - rgb 0...255 -> 0...1
    vFragmentColor = vec4(aVertexColor / 255.0, uVertexColorAlpha);
}

$$

#ifdef GLES
precision highp float;
#endif

uniform sampler2D   u_tex;
uniform float       u_fade;
uniform int       u_mode;
uniform vec4       u_color;

varying vec2       v_st;
varying vec4       vFragmentColor;

void main() {

   float len;

   if (u_mode == 2) {

        // round cap line

#ifdef DESKTOP_QUIRKS
        len = length(v_st);
#else
        len = texture2D(u_tex, v_st).a;
#endif

    } else {

        // flat cap line
        len = abs(v_st.s);
    }

    // Antialias line-edges:
    //
    // - 'len' is 0 at center of line. -> (1.0 - len) is 0 at the edges
    // - 'u_fade' is 'pixel' / 'width', i.e. the inverse width of the line in pixel on screen
    // - 'pixel' is 1.5 / relativeScale
    // - '(1.0 - len) / u_fade' interpolates the 'pixel' on line-edge

    // between 0 and 1 (it is greater 1 for all inner pixel).
 // gl_FragColor = u_color * clamp((1.0 - len) / u_fade, 0.0, 1.0);
    gl_FragColor = vFragmentColor * clamp((1.0 - len) / u_fade, 0.0, 1.0);

    // -> nicer for thin lines
    //gl_FragColor = u_color * clamp((1.0 - (len * len)) / u_fade, 0.0, 1.0);
}

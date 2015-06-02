package Utils;


import static org.lwjgl.opengl.GL15.glGenBuffers;
 
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;
 
public class Helper {
 //public static final Helper instance = new Helper();
 public static IntBuffer makeIntBuffer(int[] ibuf) {
  IntBuffer buf = BufferUtils.createIntBuffer(ibuf.length);
  for(int i:ibuf) {
   buf.put(i);
  }
  buf.position(0);
  return buf;
 }
 
  public static ByteBuffer makeByteBuffer(byte[] bbuf) {
  ByteBuffer buf = BufferUtils.createByteBuffer(bbuf.length);
  for(byte i:bbuf) {
   buf.put(i);
  }
  buf.position(0);
  return buf;
 }
 
 public static FloatBuffer makeFloatBuffer(float[] ibuf) {
     
  FloatBuffer buf = BufferUtils.createFloatBuffer(ibuf.length);
  for(float i:ibuf) {
   buf.put(i);
  }
  buf.position(0);
  return buf;
 }
 
 public static FloatBuffer makeFloatBuffer(Matrix4f mat) {
  float[] f = {mat.m00,mat.m01,mat.m02,mat.m03,
      mat.m10,mat.m11,mat.m12,mat.m13,
      mat.m20,mat.m21,mat.m22,mat.m23,
      mat.m30,mat.m31,mat.m32,mat.m33};
  return makeFloatBuffer(f);
 }
 public static int makeBuffer(int target,IntBuffer bufferdata) {
  IntBuffer bufferid = BufferUtils.createIntBuffer(1);//IntBuffer.allocate(1);
  GL15.glGenBuffers(bufferid);
  int buffer = bufferid.get(0);
     GL15.glBindBuffer(target, buffer);
     GL15.glBufferData(target, bufferdata, GL15.GL_STATIC_DRAW);
     GL15.glBindBuffer(target, 0);
  return buffer;
 }
 
 public static int makeBuffer(int target,FloatBuffer bufferdata) {
  IntBuffer bufferid = BufferUtils.createIntBuffer(1);
  glGenBuffers(bufferid);
  int buffer = bufferid.get(0);
     GL15.glBindBuffer(target, buffer);
     GL15.glBufferData(target, bufferdata, GL15.GL_STATIC_DRAW);
     GL15.glBindBuffer(target, 0);
  return buffer;
 }
 
 //convert x pixels to x coordinate (opengl system) (top-left origin) //maps to coordinates
 public static float cX(float x){
     if(x == 0){return -1f;} // returns the top left corner in OpenGL coordinates which is -1 if 0 pixel
     int dW = Display.getWidth(); // Gets the width of the game screen
     float retX = (2f / dW)*x; // Gets the pixel to OpenGL unit ration and multiplyes it by the pixel position
     if(x > (dW / 2)){ 
        return -(1f-retX); // returns the coordinates converted in the OpenGL coordinate system
     }else{
        return -(1f-retX); 
     }
 }
 
 //convert y pixels to y coordinate (opengl system) (top-left origin) //maps to coordinates
 public static float cY(float y){
     if(y == 0){return 1f;}
     int dH = Display.getHeight();
     float retY = (2f / dH)*y;
     if(y < (dH / 2)){
        return 1f-retY;
     }else{
        return 1f-retY; 
     }
 }
 
 public static float convertUnitX(float x){
     if(x <= 0){
         return 0;
     }
     else{
         return (1f/Display.getWidth())*x;
     }
 }
 
 public static float convertUnitY(float y){
     if(y <= 0){
         return 0;
     }else{
         return (1f/Display.getHeight())*y;
     }
 }
 
 // Convert (OpenGL) map coordinates to screen coordinates
 public static float convertToScreenCoordsX(float openglXCoord){
     return (openglXCoord*Display.getWidth())/2f;
 }
 
 public static float convertToScreenCoordsY(float openglYCoord){
     return (openglYCoord*Display.getHeight())/2f;
 }
 
 //convert x pixels to u coordinate (uv system) (bottom left origin -> top left)
 public static float convtexX(int x, int image_width) { 
    
     /* Divides the coordinate width by the image width
        to get the ration of an OpenGL texture unit to a pixel
        After that it multiplyes it by the X coordinate to get the U texture coordinate*/
     
     return (1f / image_width)*x;
                                  
}
 
 //convert y pixels to v coordinate (uv system) (bottom left origin -> top left )
 public static float convtexY(int y,int image_height) {
    
     /* Divides the coordinate height by the image height
        to get the ration of an OpenGL texture unit to a pixel
        After that it multiplyes it by the Y coordinate to get the V texture coordinate*/
     
    return (1f / image_height)*y;
}
 
 public static float[] concatf(float[]... arrays) {
    int lengh = 0;
    for (float[] array : arrays) {
        lengh += array.length;
    }
    float[] result = new float[lengh];
    int pos = 0;
    for (float[] array : arrays) {
        for (Float element : array) {
            result[pos] = element;
            pos++;
        }
    }
    return result;
}
 
}

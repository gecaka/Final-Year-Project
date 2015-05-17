package Engine;

import Utils.Helper;
import static Utils.Helper.cX;
import static Utils.Helper.cY;
import static Utils.Helper.makeFloatBuffer;
import game.Level;
import game.TileMap.TileMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Background {

    int image;
    
    protected final byte num_floats_pos = 3;
    protected final byte num_floats_col = 4;
    protected final byte num_floats_tex = 2;
    protected final byte float_byte_size = 4;
    protected final byte stride_byte_size = (num_floats_pos + num_floats_col + num_floats_tex) * float_byte_size;
    
    protected float x;
    protected float y;
    
    protected float[] vertices;
    
    protected byte[] indecies = {
        
        0,1,2, //bottom triangle
        
        2,3,0  //upper tringle
        
    };
    
    protected FloatBuffer attrib_buffer;
    protected ByteBuffer index_buffer; 
    
    public int background_vao;
    public int attrib_bufferID;
    public int index_bufferID;
    
    protected Texture texture;
    
    public Background(){
        
        setTexture("PNG","res/bg.png");
        
        this.vertices = new float[]
        {-1f, -1f, 0f, //1 vertex position
        1f, 1f, 1f, 1f, //color
        0f, texture.getHeight(), //texture map bottom right

        -1f, 1f, 0f, //2
        1f, 1f, 1f, 1f,
        0f, 0f, //top right

        1f, 1f, 0f, //3
        1f, 1f, 1f, 1f,
        texture.getWidth(), 0f, //top left

        1f, -1f, 0f, //4
        1f, 1f, 1f, 1f, 
        texture.getWidth(), texture.getHeight()}; //bottom 
        
        attrib_bufferID = glGenBuffers();
        index_bufferID = glGenBuffers();
        
        
        
    }
    
    public void setTexture(String filetype,String filepath){
        try {
            texture = TextureLoader.getTexture(filetype, ResourceLoader.getResourceAsStream(filepath)); // maybe use more params for scaling ?
        } catch (IOException ex) {
            ex.printStackTrace();
        }  
    }
    
    public void render(){
        
glBindBuffer(GL_ARRAY_BUFFER,attrib_bufferID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,index_bufferID);
    
        attrib_buffer = Helper.makeFloatBuffer(vertices);
        index_buffer = Helper.makeByteBuffer(indecies);
        
        //sets up gl for vertex buffer
        glBufferData(GL_ARRAY_BUFFER ,attrib_buffer,GL_STATIC_DRAW);
        
        int pos_atrr = glGetAttribLocation(Program.getCurrentProgram(),"position"); // get the position atribute from the program
        int col_attr = glGetAttribLocation(Program.getCurrentProgram(),"desColor");
        int tex_attr = glGetAttribLocation(Program.getCurrentProgram(),"texCoords");
        int uniform_loc = glGetUniformLocation(Program.getCurrentProgram(),"translate");
        glUniform2f(uniform_loc,0f,0f);
        glEnableVertexAttribArray(pos_atrr);
        glEnableVertexAttribArray(col_attr);
        glEnableVertexAttribArray(tex_attr);
        glVertexAttribPointer(pos_atrr,num_floats_pos,GL_FLOAT,false,stride_byte_size,0); //last two atributes are calculated in bytes () //specify how the info(buffers) by the attribute should be read
        glVertexAttribPointer(col_attr,num_floats_col,GL_FLOAT,false,stride_byte_size,12); // calc goes like 3 floats(vert)+4float(color) = 7 * sizeof(float)'4' = 7*4 = 28
        glVertexAttribPointer(tex_attr,num_floats_tex,GL_FLOAT,false,stride_byte_size,28);    // offset for color is 3 floats per vertex * sizeof(float)'4' = 3*4 = 12        
        
        //set up gl for index buffer
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,index_buffer,GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0); // have to unbind element buffer before calling drawEements !!!
        
        glBindTexture(GL_TEXTURE_2D,texture.getTextureID());
        
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        
        glDrawElements(GL_TRIANGLES,index_buffer);
        
        
    }
    
    public float getX(){
        return x;
    }
    
    public float getY(){
        return y;
    }
    
    public void dX(float pixel){
      x = x + (pixel/2f);
      //System.out.println(x);
    }
    
    public void dY(float pixel){
      y = y + (pixel/2f);
      //System.out.println(y);
    }
    
    public void flipHorizontal(){
        
      vertices[7] = 1f - vertices[7];
      vertices[16] = 1f - vertices[16];
      vertices[25] = 1f - vertices[25];
      vertices[34] = 1f - vertices[34];
      
    }
    
    public void flipVertical(){
      vertices[8] = 1f - vertices[8];
      vertices[17] = 1f - vertices[17];
      vertices[26] = 1f - vertices[26];
      vertices[35] = 1f - vertices[35];  
    }
    
    public void setOpacity(float opacity){
        if(opacity < 0 || opacity > 1){
            System.out.println("Invalid value, opacity value must be in range from 0f to 1f");
        }else{
            vertices[6] = opacity;
            vertices[15] = opacity;
            vertices[24] = opacity;
            vertices[33] = opacity;
        }
    }
    
}

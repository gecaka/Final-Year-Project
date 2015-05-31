package game.Entity.structures;

import static Utils.Helper.*;
import java.nio.FloatBuffer;
import Engine.Program;
import Utils.Helper;
import game.Level;
import game.Menu;
import game.TileMap.TileMap;
import java.io.IOException;
import java.nio.ByteBuffer;
//import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.*;
import org.newdawn.slick.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class GameObject implements Intelligent{
    
    protected static boolean antiAlias = true; 
    
    protected final byte num_floats_pos = 3;
    protected final byte num_floats_col = 4;
    protected final byte num_floats_tex = 2;
    protected final byte float_byte_size = 4;
    protected final byte stride_byte_size = (num_floats_pos + num_floats_col + num_floats_tex) * float_byte_size;
    
    public boolean isOnFloor = false;
    
    protected float x;
    protected float y;
    
    public int speed = 16;
    
    protected int ts = game.Level.getTileSize(); //TileSize
    
    protected float[] vertices;
    
    protected byte[] indecies = {
        
        0,1,2, //bottom triangle
        
        2,3,0  //upper tringle
        
    };
    
    protected FloatBuffer attrib_buffer;
    protected ByteBuffer index_buffer; 
    
    public int attrib_bufferID;
    public int index_bufferID;
    
    protected Texture texture;
    
    public String name;
    private static int counter = 0;
    private boolean isFliped;
    
    protected static TrueTypeFont font1;
    protected static TrueTypeFont font2;
    
    private static boolean isFontLoaded = false;
    
    public GameObject(int xIn, int yIn){
        
        x = xIn;
        y = yIn;
        
        this.vertices = new float[]
        {cX(x), cY(y), 1f, //1 vertex position
        1f, 1f, 1f, 1f, //color
        1f, 0f, //texture map bottom right

        cX(x), cY(y + ts), 1f, //2
        1f, 1f, 1f, 1f,
        1f, 1f, //top right

        cX(x + ts), cY(y + ts), 1f, //3
        1f, 1f, 1f, 1f,
        0f, 1f, //top left

        cX(x + ts), cY(y), 1f, //4
        1f, 1f, 1f, 1f, 
        0f, 0f}; //bottom 
        
        name = counter+""+x+""+y;
        counter++;
        
        attrib_bufferID = glGenBuffers();
        index_bufferID = glGenBuffers();
        
        setTexture("PNG","res/enemy_basic.png");
        loadFont();
        isFliped = false;

        
    }
    
    private static void loadFont(){
        
        if(!isFontLoaded){
        java.awt.Font awtFont = new java.awt.Font("Lucida Console", java.awt.Font.BOLD, 14);
        font1 = new TrueTypeFont(awtFont, antiAlias);
         
        java.awt.Font awtFont1 = new java.awt.Font("Arial", java.awt.Font.BOLD, 14);
        font2 = new TrueTypeFont(awtFont1, antiAlias);
        
        isFontLoaded = true;
        }
        
    }
    
    private boolean lock_to_screen;
    
    public void lockToScreen(boolean state){
        lock_to_screen = state;
    }
    
    public void setTexture(String filetype,String filepath){
        try {
            texture = TextureLoader.getTexture(filetype, ResourceLoader.getResourceAsStream(filepath)); // maybe use more params for scaling ?
        } catch (IOException ex) {
            ex.printStackTrace();
        }  
    }
    
    public void render(){
        
            vertices[0] = cX(x); //vector 1 x
            vertices[1] = cY(y); //vector 1 y
            
            vertices[9] = cX(x); //vector 2 x
            vertices[10] = cY(y + ts); //vector 2 y 
            
            vertices[18] = cX(x + ts); //vector 3 x
            vertices[19] = cY(y + ts); //vector 3 y
            
            vertices[27] = cX(x + ts); //vector 4 x
            vertices[28] = cY(y); //vector 4 y
        
        glBindBuffer(GL_ARRAY_BUFFER,attrib_bufferID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,index_bufferID);
    
        attrib_buffer = Helper.makeFloatBuffer(vertices);
        index_buffer = Helper.makeByteBuffer(indecies);
        
        FloatBuffer trans_buffer = makeFloatBuffer(TileMap.offset);
        
        //sets up gl for vertex buffer
        glBufferData(GL_ARRAY_BUFFER ,attrib_buffer,GL_STATIC_DRAW);
        
        int pos_atrr = glGetAttribLocation(Program.getCurrentProgram(),"position"); // get the position atribute from the program
        int col_attr = glGetAttribLocation(Program.getCurrentProgram(),"desColor");
        int tex_attr = glGetAttribLocation(Program.getCurrentProgram(),"texCoords");
        int uniform_loc = glGetUniformLocation(Program.getCurrentProgram(),"translate");
        
        if(lock_to_screen){
        glUniform2f(uniform_loc, 0f, 0f);
        }else{
        glUniform2(uniform_loc,trans_buffer);  
        }
        
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
        
        glDrawElements(GL_TRIANGLES,index_buffer);
        
    }
    
    public float getX(){
        return x;
    }
    
    public float setX(float xIn){
        return x = xIn;
    }
    
    public float getY(){
        return y;
    }
    
    public float setY(float yIn){
        return y = yIn;
    }
    
    public int getSpeed(){
        return speed;
    }
    
    public void setSpeed(int speedIn){
        speed = speedIn;
    }
    
    public void dX(float pixel){
      x = x + (pixel/2f);
      //System.out.println(name+"  X:"+pixel);
      
      
    }
    
    public void dY(float pixel){
      y = y + (pixel/2f);
      //System.out.println(name+"  Y:"+pixel);
    }

    @Override
    public void AI(float delta) {
      
    }
    
    public void say(String msg){
        
     Menu.getCurrentProgram().releaseProgram();
       
       float displayX = x + Helper.convertToScreenCoordsX(TileMap.getOffsetX()); 
       float displayY = y - Helper.convertToScreenCoordsY(TileMap.getOffsetY());
       
        font1.drawString(displayX-ts, displayY-ts, msg, Color.white);
        font2.drawString(displayX-ts, displayY+ts, "", Color.yellow);
        
      Menu.getCurrentProgram().useProgram();
    }
    
    public void update(float delta){
        
    }
    
    public void turnLeft(){
        if(!isFliped){
            
            flipHorizontal();
            isFliped = true;
        }
    }
    
    public void turnRight(){
        if(isFliped){
            
            flipHorizontal();
            isFliped = false;
        }
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
    
     public float getTopBounds(){
        
        return getY() - (Level.getTileSize()/2f);
        
    }
    
    public float getBottomBounds(){
        return getY() + (Level.getTileSize()/2f);
    }
    
    public float getLeftBounds(){
        return getX() - (Level.getTileSize()/2f);
    }
        
    public float getRightBounds(){
        return getX() + (Level.getTileSize()/2f);
    }
    
}



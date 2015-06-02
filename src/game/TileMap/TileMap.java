/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.TileMap;

import Engine.Program;
import static Utils.Helper.*;
import game.Entity.Player;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class TileMap {
    
    private Scanner scanner;
    
    //position
    private float x;
    private float y;
    
    //number of tile rows and colums (UNITS 5 ROWS 6 COLUMS ... ETC)
    private int numOfRows;
    private int numOfColums;
    
    //size of the tile (Example 32 = 32x32 , 16 = 16x16)
    private int tileSize;
    
    //map array
    private int[][] map; //use map array indecies to detirmen draw position of tile (ASCII map representation)
    public Tile[][] map_tiles; //all tiles stored using load function
    
    //dimentions of the map in pixels
    private int width;
    private int height;
    
    private String img_path;
    private String imgtype;
    
    private String map_path;
    
    //tile array
    private TileSet current_tiles;
    
    public static float[] offset = new float[]{ 0f , 0f } ;
    public float[] pixeloffset;
    
    private int numRowsToDraw;
    private int numColumnsToDraw;
    
    private static int map_vao;
    private int map_vbo;
    private int map_index_vbo;
    
    public TileMap(int tileSizeIn,String path_to_tileset,String filetype, int start_draw_x,int start_draw_y){
        
        map_vao = glGenVertexArrays();
        x = start_draw_x;
        y = start_draw_y;
        tileSize = tileSizeIn;
        img_path = path_to_tileset;
        imgtype = filetype;
        current_tiles = new TileSet();
        numColumnsToDraw = (Display.getHeight() / tileSize) + 4; // the plus 4 is 4 buffer tiles which are going to be renered outside the screen
        numRowsToDraw = (Display.getWidth() / tileSize) + 4;
        offset = new float[] {
            0f,0f
        };
        
        pixeloffset = new float[] {
            0f,0f
        };
        
    }
        
        
    
    
    public void defineParamsTiles(int row1_size,int row2_size,int row3_size,int row4_size){
        current_tiles = new TileSet(imgtype,img_path,row1_size,row2_size,row3_size,row4_size);
        current_tiles.loadTileset();
        
    }
    
    public void constructMap(String map_file_in){
        map_path = map_file_in;
        
        File map_file = new File(map_path);
        
        try {
           
            scanner = new Scanner(map_file).useDelimiter("[^0-9]+");
            
            numOfRows = Integer.parseInt(scanner.nextLine());
            numOfColums = Integer.parseInt(scanner.nextLine());
            
            width = numOfColums*tileSize;
            height = numOfRows*tileSize; 
                  
            map = new int[numOfRows][numOfColums];
            map_tiles = new Tile[numOfRows][numOfColums];
            
            while(scanner.hasNext()){
            
                
                for(int x = 0;x<map.length;x++){
                    for(int y = 0;y<map[x].length;y++){
                        int nextBlockIndex = scanner.nextInt();
                        map[x][y] = nextBlockIndex;
                        map_tiles[x][y] = new Tile(y,x,nextBlockIndex-1);
                    }
                }
                
                
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TileMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //String temp = scanner.nextLine();
        
    }
    
    public void render(){
        
      glBindVertexArray(map_vao);
      map_vbo = glGenBuffers();
      map_index_vbo = glGenBuffers();
              
      glBindBuffer(GL_ARRAY_BUFFER,map_vbo);
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,map_vbo);
      
      float[] total_vertex = new float[0];
      
      for(int x = 0;x < map.length;x++){
         for(int y = 0;y < map[x].length;y++){   
                 
                 if( y < map[x].length && x < map.length){
                     
                     if( ( x*tileSize < Player.refY + (Display.getHeight() /1.9f) && x*tileSize > Player.refY - (Display.getHeight()) /1.9f) && 
                         ( y*tileSize < Player.refX + (Display.getWidth() /1.9f) && y*tileSize > Player.refX - (Display.getWidth()) /1.9f) ){
                     
                    float[] temp_pos =
                            
           {cX(y*tileSize), cY(x*tileSize), //1 vertex position
            
            cX(y*tileSize), cY((x*tileSize)+ tileSize),
            
            cX((y*tileSize )+ tileSize), cY((x*tileSize) + tileSize),
            
            cX((y*tileSize)+ tileSize), cY(x*tileSize)};
                     
                    float[] temp_tex = TileSet.getTextureCoords(map_tiles[x][y].getTileIndex());
                    
            float[] combined_attribs ={
                temp_pos[0],temp_pos[1],
                temp_tex[0],temp_tex[1],
                temp_pos[2],temp_pos[3],
                temp_tex[2],temp_tex[3],
                temp_pos[4],temp_pos[5],
                temp_tex[4],temp_tex[5],
                temp_pos[6],temp_pos[7],
                temp_tex[6],temp_tex[7],
            };        
                    
                total_vertex = concatf(total_vertex,combined_attribs);
                
                 }
                     
               }
          }
              }
      
      if(total_vertex.length > 0){
        /* byte[] indecies = {
        
        0,1,2, //bottom triangle
        
        2,3,0  //upper tringle
        
        }; */
      
      FloatBuffer map_buffer = makeFloatBuffer(total_vertex);
      //ByteBuffer index_buffer = makeByteBuffer(indecies);
      FloatBuffer trans_buffer = makeFloatBuffer(offset);
      
      
      glBufferData(GL_ARRAY_BUFFER,map_buffer,GL_DYNAMIC_DRAW);
      
      int pos_atrr = glGetAttribLocation(Program.getCurrentProgram(),"position"); // get the position atribute from the program
      int tex_attr = glGetAttribLocation(Program.getCurrentProgram(),"texCoords");
      int uniform_loc = glGetUniformLocation(Program.getCurrentProgram(),"translate");
      glUniform2(uniform_loc,trans_buffer);
      glEnableVertexAttribArray(pos_atrr);
      glEnableVertexAttribArray(tex_attr);
      glVertexAttribPointer(pos_atrr,2,GL_FLOAT,false,16,0); //last two atributes are calculated in bytes () //specify how the info(buffers) by the attribute should be read
                                                                                             // calc goes like 3 floats(vert)+4float(color) = 7 * sizeof(float)'4' = 7*4 = 28
      glVertexAttribPointer(tex_attr,2,GL_FLOAT,false,16,8);
      
        
      //glBufferData(GL_ELEMENT_ARRAY_BUFFER,index_buffer,GL_STATIC_DRAW);
      //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0); // have to unbind element buffer before calling drawEements !!!
      
                 
      glBindTexture(GL_TEXTURE_2D,TileSet.tex.getTextureID());
      
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
      
      glDrawArrays(GL_QUADS,0,(numOfColums*numOfRows)*4); // the times 4 is the number of vertices involved in the production of a quad
      //glDrawElements(GL_TRIANGLES,index_buffer); // Can ADD NUMBER OF ROWS TO DRAW AND NUMBER OF COLUMNS TO DRAW to reduce lag

       //System.out.println("X: " +offset[0] +"  Y: "+offset[1] );
      }
      } 
    
    
    public void update(){
        
    }
    
    public void moveRight(float pixels){
        offset[0] = offset[0] - convertUnitX(pixels);

    }
    public void moveLeft(float pixels){
        offset[0] = offset[0] + convertUnitX(pixels);

    }
    public void moveUp(float pixels){
        offset[1] = offset[1] - convertUnitY(pixels);

    }
    public void moveDown(float pixels){
        offset[1] = offset[1] + convertUnitY(pixels);

    }
    
    public int getNumRows(){
        return numOfRows;
    }
    
    public int getNumCols(){
        return numOfColums;
    }
    
    public static float getOffsetX(){
        return offset[0];
    }
    
    public static float getOffsetY(){
        return offset[1];
    }
    
}

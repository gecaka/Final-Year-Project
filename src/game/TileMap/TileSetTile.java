/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.TileMap;

import java.nio.FloatBuffer;
import static Utils.Helper.*;


public class TileSetTile {

    private int[] index;
    private float[] texture_coordinates;
    private byte type; // change if you get more than 255 types of blocks(doubt it)
    private int tile_size;
    
    public static byte NORMAL = 0; // normal wallk through 
    public static byte BLOCKED = 1; // solid object
    public static int counter = 0;
    
    public TileSetTile(int[] index ) {
        tile_size = game.Level.getTileSize();
        this.index = index;
        
        if(this.index[1] == 0 || this.index[1] == 1){
           type = BLOCKED;
        }else{
           type = NORMAL;
        }
        
        float[] temp_coords = {
            
            convtexX((this.index[0]*tile_size)+tile_size, TileSet.tex.getImageWidth()+1),
            convtexY(this.index[1]*tile_size, TileSet.tex.getImageHeight()+1),
            
            convtexX((this.index[0]*tile_size)+tile_size, TileSet.tex.getImageWidth()+1),
            convtexY((this.index[1]*tile_size)+tile_size, TileSet.tex.getImageHeight()+1),
            
            convtexX(this.index[0]*tile_size, TileSet.tex.getImageWidth()+1),
            convtexY((this.index[1]*tile_size)+tile_size, TileSet.tex.getImageHeight()+1),
            
            convtexX(this.index[0]*tile_size, TileSet.tex.getImageWidth()+1),
            convtexY( this.index[1]*tile_size, TileSet.tex.getImageHeight()+1)
            
        };
        
        texture_coordinates = temp_coords;
        
        counter++;
    }
    
    public byte getType(){
        return type;
    }
    
    public float[] getTextureCoords(){
        return texture_coordinates;
    }
    
    public static void setCounter(int set){
        counter = set;
    }
    

}

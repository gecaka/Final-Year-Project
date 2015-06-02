/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.TileMap;

import game.Level;

/**
 *
 * @author Anonymous
 */
public class Tile {
    
    private int pos_x; //in units (2,3)
    private int pos_y;
    
    private int x;
    private int y;
    
    private float[] position;
    
    private int tile_texture_index;
    private byte tile_type;
    
    public String block_name;
    
    
    public Tile(int pos_xIn,int pos_yIn,int texture_indexIn){
        
        pos_x = pos_xIn;
        pos_y = pos_yIn;
        x = pos_x * Level.getTileSize();
        y = pos_y * Level.getTileSize();
        tile_texture_index = texture_indexIn;
        tile_type = TileSet.getTileType(texture_indexIn);
        block_name = "B" + pos_x + "/" + pos_y + "  ||  " + pos_x*32 + "/" + pos_y*32 ;
        
    }
    
    public int getIndexPosX(){
        return pos_x;
    }
    
    public int getIndexPosY(){
        return pos_y;
    }
    
    public float getX(){
        return x;
    }
    
    public float getY(){
        return y;
    }
    
    public byte getTileType(){
        return tile_type;
    }
    
    public int getTileIndex(){
        return tile_texture_index;
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

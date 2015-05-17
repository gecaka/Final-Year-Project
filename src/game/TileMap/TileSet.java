/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.TileMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Anonymous
 */
public class TileSet {
    
    private int r1s,r2s,r3s,r4s;
    protected static Texture tex;
    private int tile_size;
    private static ArrayList<TileSetTile> tiles ; //  tile coresponding position to index
    
    public TileSet(){}
    
    public TileSet(String file_type,String path_to_image,int row1_size,int row2_size,int row3_size,int row4_size ) throws IllegalArgumentException{
        
        
        tile_size = game.Level.getTileSize();
        
        try {
            tex = TextureLoader.getTexture(file_type, ResourceLoader.getResourceAsStream(path_to_image));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if(tex.getWidth() < (r1s*tile_size) || tex.getWidth() < (r2s*tile_size)|| tex.getWidth() < (r3s*tile_size)|| tex.getWidth() < (r4s*tile_size)){
            throw new IllegalArgumentException("number of tiles specified per row greater than texture width (out of bounds) ");
        }else{
            r1s = row1_size;
            r2s = row2_size;
            r3s = row3_size;
            r4s = row4_size;  
        }
        
    }
    //tyle coordinate;
    
    public void loadTileset(){
        
        tiles = new ArrayList<TileSetTile>();
        
        loadRow(r1s,0);
        loadRow(r2s,1);
        loadRow(r3s,2);
        loadRow(r4s,3);
        
    }
    
    private void loadRow(int rowSize,int numRow){
        
        while(TileSetTile.counter < rowSize){
 
            int temp[] = {TileSetTile.counter, numRow}; //get position on tilemap (x,y) units and sends it to Tileset for calculations           
            tiles.add(new TileSetTile(temp));
        }
        
        TileSetTile.setCounter(0);
    }
    
    public static byte getTileType(int index){
        return tiles.get(index).getType();
    }
    
    public static float[] getTextureCoords(int index){
        return tiles.get(index).getTextureCoords();
    }
    
}

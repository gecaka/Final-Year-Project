package game;

import Engine.Background;
import game.Entity.FlameO;
import game.Entity.Player;
import org.lwjgl.opengl.*;
import game.Entity.structures.GameObject;
import game.Entity.structures.InteractiveNPC;
import game.Entity.structures.LandNPC;
import game.Entity.Walker;
import game.TileMap.Tile;
import game.TileMap.TileMap;
import game.TileMap.TileSetTile;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.lwjgl.openal.AL;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Level {
    
    private static int tileSize = 32;
    Audio level_music;
    
    private TrueTypeFont font;
    private TrueTypeFont font2;

    private Player pl1;
    public static ArrayList<InteractiveNPC> objects = new ArrayList<InteractiveNPC>();
    public static ArrayList<InteractiveNPC> creation_list = new ArrayList<InteractiveNPC>();
    public static TileMap map;
    
    public Background background;
    private static boolean exit;
    
    private boolean antiAlias;
    
    //Constructor //map,eventlist
    public Level(){
        
    }
    
    public Level(int tileSize) { 
        this.tileSize = tileSize;
        
        Font awtFont = new Font("Lucida Console", Font.BOLD, 24);
        font = new TrueTypeFont(awtFont, antiAlias);
        
        Font awtFont2 = new Font("Arial", Font.BOLD, 24);
        font2 = new TrueTypeFont(awtFont2, antiAlias);
        
        try {
            level_music = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/level1_music.wav"));
        } catch (IOException ex) {
            Logger.getLogger(Level.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    public static void exitGame(){
        exit = true;
    }

        //starter 
    public void start(int delta){
        exit = false;
        initGameObjects();
        gameLoop();  
    }
       
    //initializes some GL setting
    
    
    //main game loop
    
    
    public void initGameObjects(){
        map = new TileMap(tileSize,"res/test_tilesheet.png","PNG",1,1);
        map.defineParamsTiles(16, 16, 16, 16);
        map.constructMap("res/map.txt");
        background = new Background();
        
        objects.add(new Walker(100,100));
        objects.add(new Walker(200,300));
        objects.add(new Walker(75,575));
        objects.add(new Walker(1185,575));
        objects.add(new Walker(1585,575));
        objects.add(new Walker(1100,272));
        objects.add(new Walker(1122,61));
        objects.add(new Walker(1885,575));
        objects.add(new Walker(2500,575));
        objects.add(new Walker(2075,575));
        objects.add(new Walker(2360,434));
        objects.add(new Walker(2485,306));
        objects.add(new Walker(2360,170));
        objects.add(new FlameO(400,300));
        objects.add(new FlameO(830,525));
        objects.add(new FlameO(1635,400));
        objects.add(new FlameO(1315,258));
        objects.add(new FlameO(1550,70));
        objects.add(new FlameO(1952,290));
        objects.add(new FlameO(2013,530));
        
        objects.add(pl1 = new Player((Display.getWidth()/2)-(tileSize/2),(Display.getHeight()/2)-(tileSize/2)));
        
    }
   
    
    public static InteractiveNPC checkCollisionWithInteractiveObjects(InteractiveNPC obj){
        
        for(InteractiveNPC potentialInteraction : objects){
                
            if(!obj.name.equals(potentialInteraction.name)){
                float xoff = (potentialInteraction.getX());
                float yoff = (potentialInteraction.getY());
                
                if(yoff > (obj.getY()-(tileSize/1.5f)) && yoff < obj.getY()+(tileSize/1.5f) ){
                    
                    if(xoff > (obj.getX()-(tileSize/1.5f)) && xoff < obj.getX()+(tileSize/1.5f) ){
                        
                        return potentialInteraction;
                    }
                } 
        }
        }
        return null;
    }
    
    public static boolean checkCollisionWithWorld(float Dx,float Dy, GameObject obj){
        
        boolean isLand = obj instanceof LandNPC;
        
        for(Tile tile[] : map.map_tiles){
            
            for(int i=0;i<map.getNumCols();i++){

                if(tile[i].getBottomBounds() > (obj.getTopBounds())+Dy && tile[i].getTopBounds() < (obj.getBottomBounds())+Dy ){
                    if(isLand){obj.isOnFloor = true;}
                    if(tile[i].getRightBounds() > (obj.getLeftBounds())+Dx && tile[i].getLeftBounds() < (obj.getRightBounds())+Dx ){
                        
                        if(tile[i].getTileType() == TileSetTile.BLOCKED){
                               
                               adjustToEnd(Dx,Dy,obj,tile[i]);
                            return true;
                        }
                        else{
                            if(isLand){obj.isOnFloor = false;}
                        }   
                    }
                }else{
                    if(isLand){obj.isOnFloor = false;}
                }   
            }
        }
        return false;
        
    }
    
    private static void adjustToEnd(float Dx,float Dy,GameObject obj,Tile tile){
        //if(){
        float top = tile.getBottomBounds() - (obj.getTopBounds());
        //}
        float bottom = tile.getTopBounds() - (obj.getBottomBounds());
        float left = tile.getRightBounds() - (obj.getLeftBounds());
        float right = tile.getLeftBounds() - (obj.getRightBounds());
        
        if(obj instanceof Player){
        
        if(Dx != 0){    
            if(Dx > 0 && right != 0){ //dx right
                
                obj.dX(right);
                map.moveRight(right);
            }else if(Dx < 0 && left != 0){ //dx left
              
                obj.dX(left);
                map.moveLeft(-left);
            }
        }
        
        else if (Dy != 0){
            if(Dy > 0 && bottom != 0){ //dx down
                
                if(obj.isOnFloor && bottom > 2.5f){
                obj.dY(bottom);
                map.moveDown(bottom);  
                }

            }else if(Dy < 0 && top != 0){ // dy UP
                
                obj.dY(top);
                map.moveUp(-top);
            }
        }
     
        }else if(obj instanceof Walker){
     
            //HOW ABOUT A DOUBLE distace CHECK to see what is coming
            //use a different approach to getting the 
            
        if(Dx != 0){
            //if(obj.isOnFloor){
                if(Dx > 0 && right != 0){ //dx right
                    //System.out.println("Moving right: "+Dx);
                    obj.dX(right);
                    
                }if(Dx < 0 && left != 0){ //dx left

                    //System.out.println("Moving left: "+Dx);
                    obj.dX(left);

                }
            //}
        }
        
        if(Dy != 0){
            
            float padding = 0.001f;
                                        
            if(Dy > 0 && bottom != 0 && bottom > padding){ //dx down
               
                obj.dY(bottom);
            
            }if(Dy < 0 && top != 0 && top < -padding){ // dy UP
                
                obj.dY(top);
            }
        }
        }

    }

    
    public void update(float delta){
        
        //update logic goes here...
        pl1.update(delta);
        if(!paused){
        ArrayList<InteractiveNPC> removal_list = new ArrayList<InteractiveNPC>();
        
        for(InteractiveNPC obj : objects){
            if (obj instanceof Player){
                
            }else{
                obj.update(delta);
                if(obj.remove == true){
                    removal_list.add(obj);
                }
            }
        }
        
        for(InteractiveNPC obj : removal_list){
            objects.remove(obj);
        }
        
        for(InteractiveNPC obj : creation_list){
            objects.add(obj);
        }
        
        creation_list.clear();
        
    }
        }
    
    public void render(){
        
        background.render();
        map.render();
        
        for(GameObject enemy : objects){
            enemy.render();
        }
        
        pl1.render();
        renderGUI();
        
    }
    
    public void renderGUI(){
        
        Color clr = null;
        if(pl1.health<=100 && pl1.health>70){
                clr = Color.green;
        }else if(pl1.health<=70 && pl1.health>50){
                clr = Color.yellow;
        }else if(pl1.health<=50 && pl1.health>20){
                clr = Color.orange;
        }else if(pl1.health<=20 && pl1.health>0){
                clr = Color.red;
        }else{
            clr = Color.black;
        }
        
        Menu.getCurrentProgram().releaseProgram();
        
        font.drawString(1f, 1f, " Health "+pl1.health, clr);
        font.drawString(1f, 20f, " coord X: "+pl1.getX(), Color.yellow);
        font.drawString(1f, 40f, " coord Y: "+pl1.getY(), Color.yellow);
        //font.drawString(1f, 40f, " Map X: "+map.pixeloffset.getY(), Color.yellow);
        //font.drawString(1f, 40f, " Map Y: "+map.getY(), Color.yellow);
        font2.drawString(0f, 0f, "", Color.transparent);// Fixes gui display bugs
        
        Menu.getCurrentProgram().useProgram();
        
    }

    
    //misc methods--------------------------------------------------------------
    
    public static int getTileSize() {return tileSize;}
    
    public void setMusic(String path){
        try {   
            level_music = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(path));
        } catch (IOException ex) {
            Logger.getLogger(Level.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    public void gameLoop(){
        
        level_music.playAsMusic(1f, 1f, true);
        
        while(exit != true && !Display.isCloseRequested()){
            
            float delta = Menu.getDelta();
            
            if(!paused){
            update(delta);
            render();
            }else{
             pl1.inputPool();
            }
            
            Display.update();
            Display.sync(60);
            
            if(Display.isCloseRequested()){
            Display.destroy();
            AL.destroy();
            System.exit(0);
            }
            
        }
        cleanUp();

    }
    
    public static void createObject(InteractiveNPC obj){
        creation_list.add(obj);
    }
    
    public static boolean paused = false;
    
    public static void pause(boolean isPaused){
        paused = isPaused;
    }
    
    public void cleanUp(){
        
    pl1 = new Player(0,0);
    objects = new ArrayList<InteractiveNPC>();
    creation_list = new ArrayList<InteractiveNPC>();
    }
    
}

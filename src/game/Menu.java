/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import Engine.Background;
import Engine.Program;
import Engine.Shader;
import Utils.Helper;
import game.Entity.Player;
import game.Entity.Walker;
import game.Entity.structures.GameObject;
import game.Entity.structures.InteractiveNPC;
import game.Entity.structures.LandNPC;
import game.Level;
import static game.Level.creation_list;
import static game.Level.map;
import static game.Level.objects;
import game.TileMap.Tile;
import game.TileMap.TileMap;
import game.TileMap.TileSetTile;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Anonymous
 */
public class Menu {

    Audio menu_music;
    int image;
    
    protected final byte num_floats_pos = 3;
    protected final byte num_floats_col = 4;
    protected final byte num_floats_tex = 2;
    protected final byte float_byte_size = 4;
    protected final byte stride_byte_size = (num_floats_pos + num_floats_col + num_floats_tex) * float_byte_size;
    
    protected float x;
    protected float y;
    
    protected String title;
    protected String option1;
    protected float option1_x;
    protected float option1_y;
    protected String option2;
    protected float option2_x;
    protected float option2_y;
    protected String option3;
    protected float option3_x;
    protected float option3_y;
    
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
    
    public static TrueTypeFont font;
    public static TrueTypeFont font1;
    TrueTypeFont font2;
    
    
    // Boolean flag on whether AntiAliasing is enabled or not 
    private boolean antiAlias = true;
    
    protected int current_selection = 1;
    float height_unit; // Up to 10 units
    GameObject arrow;
    
    private boolean game_is_running = false;
    
    private static final String GAME_TITLE = "ChemMan";
    private static final int DISPLAY_WIDTH = 1028;
    private static final int DISPLAY_HEIGHT = 768;
    private static final int MAX_FPS = 60;
    
    private static int tileSize = 32;
    
    private static long lastFrame;
    private static long lastFPS;
    private static int fps; // returns the current time in miiseconds
    
    private static Program current_program;
    private int VAO;
    private static long getTime(){  return (Sys.getTime() * 1000) / Sys.getTimerResolution(); } 
    Level current_level = new Level();
    
    
    public Menu(){
        
        initDisplay();
        initGL();
        
        setGameIsRunning(false);
        
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
        
        try {
            menu_music = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/menu_music.wav"));
        } catch (IOException ex) {
            Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }
    
    public void setTexture(String filetype,String filepath){
        try {
            texture = TextureLoader.getTexture(filetype, ResourceLoader.getResourceAsStream(filepath)); // maybe use more params for scaling ?
        } catch (IOException ex) {
            ex.printStackTrace();
        }  
    }
    
    public void render(){
        
        if(!getGameIsRunning()){ // Check if the game is running
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, Display.getWidth(),Display.getHeight());
            
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
        
        getCurrentProgram().releaseProgram();
        
        font2.drawString(Display.getWidth()/2-(font2.getWidth(title)/2), 3*height_unit , title, Color.white);
        font.drawString(option1_x, option1_y, option1, Color.white);
        font.drawString(option2_x, option2_y, option2, Color.white);
        font.drawString(option3_x, option3_y, option3, Color.white);
        font2.drawString(0,0, "", Color.red);
        
        getCurrentProgram().useProgram();
        
        if(arrow == null){
            arrow = new GameObject((int)option1_x,(int)option1_y);
        }
      
        arrow = new GameObject((int)arrow.getX(),(int)arrow.getY());
        arrow.setTexture("PNG", "res/flask_arrow.png");
        arrow.lockToScreen(true);
        
        arrow.render();
        }
    }
    
     public void inputPool(){
            
         if(!getGameIsRunning()){
         
            if(Mouse.next()){
                
                if(Mouse.getEventButton() == 0){


                
                if(Mouse.getEventButton() == 1){


                }
              
            }
            }
            
            if(Keyboard.next()){
                
                if(Keyboard.getEventKey() == Keyboard.KEY_UP){
                    if(!Keyboard.getEventKeyState()){
                        moveSelectionUp();
                    }
                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_DOWN){
                    if(!Keyboard.getEventKeyState()){
                        moveSelectionDown();
                    }
                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_RETURN){
                    if(!Keyboard.getEventKeyState()){
                        
                    }else{
                        
                        if(current_selection == 1 ){
                            startLevel(level1);
                            level1 = new Level(32);
                        }else if(current_selection == 2){
                            
                        }else{
                            AL.destroy();
                            Display.destroy();
                            System.exit(0);
                        }
                        
                    }
                }
                
                
                
            }
          
       }
     }
    
    public float getX(){
        return x;
    }
    
    public float getY(){
        return y;
    }
    
    public void dX(float pixel){
      x = x + (pixel/2f);
    }
    
    public void dY(float pixel){
      y = y + (pixel/2f);
    }
   
    public void moveSelectionDown(){
        if(current_selection < 3){
            current_selection++;
            setCurrentSelection(current_selection);
            
        }
    }
    
    public void moveSelectionUp(){
        if(current_selection > 1){
            current_selection--;
            setCurrentSelection(current_selection);
            
        }
    }
    
    public void setCurrentSelection(int selection){
        current_selection = selection;
    }
    
    public void setTitle(String titleIn){
        title = titleIn;
    }
    
    public void setOption(int number,String option_text){
        
        if(number == 1){
            option1 = option_text;
        }else if(number == 2){
            option2 = option_text;
        }else if(number == 3){
            option3 = option_text;
        }
    }
    
    public void flipHorizontal(){
        
      vertices[7] = 1f - vertices[7];
      vertices[16] = 1f - vertices[16];
      vertices[25] = 1f - vertices[25];
      vertices[34] = 1f - vertices[34];
      
    }  
      //============================================================================================================
      
      public static void reloadProgram(){
        Shader vertex_shader = new Shader("src/game/shader/vertex_shade.txt",GL_VERTEX_SHADER); // loads the vertex shader from file
        Shader fragment_shader = new Shader("src/game/shader/fragment_shade.txt",GL_FRAGMENT_SHADER); // same for fragment shader
        
        Program load_program = new Program(vertex_shader,fragment_shader); // links them into a program
        if(load_program.isReadyToLoad()){
            load_program.useProgram();
            current_program = load_program;
        }
      }
    
      public void initGL(){
        
        glClearColor(0f,0f,0.7f,0f); // clears the screen to a certain color (RGBA)
        glEnable(GL_BLEND);   // enable blend mode - transparent sprites might not render as expected without this (enables Alpha)
        glEnable(GL11.GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // sets the blend mode
        GL11.glOrtho(0, Display.getWidth(),Display.getHeight(), 0, 1, -1); // USED FOR TEXT DISPLAYING !!! DEPRC
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        
        Shader vertex_shader = new Shader("src/game/shader/vertex_shade.txt",GL_VERTEX_SHADER); // loads the vertex shader from file
        Shader fragment_shader = new Shader("src/game/shader/fragment_shade.txt",GL_FRAGMENT_SHADER); // same for fragment shader
        
        current_program = new Program(vertex_shader,fragment_shader); // links them into a program
        
        current_program.useProgram(); // uses the program
        
         // load a default java font
        Font awtFont = new Font("Lucida Console", Font.BOLD, 24);
        font = new TrueTypeFont(awtFont, antiAlias);
         
        Font awtFont1 = new Font("Arial", java.awt.Font.BOLD, 24);
        font1 = new TrueTypeFont(awtFont1, antiAlias);
        
        // load font from file
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream("res/myfont.ttf");

            Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont2 = awtFont2.deriveFont(72f); // set font size
            font2 = new TrueTypeFont(awtFont2, antiAlias);
        
            setTitle(" CHEM MAN ");
            setOption(1,"Start Game");
            setOption(2,"Instructions");
            setOption(3,"Exit");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        option1_x = Display.getWidth()/2-(font.getWidth(option1)/2);
        option1_y = 4.5f*height_unit;
        option2_x = Display.getWidth()/2-(font.getWidth(option2)/2);
        option2_y = 5*height_unit;
        option3_x = Display.getWidth()/2-(font.getWidth(option3)/2);
        option3_y = 5.5f*height_unit;
        
        setTexture("JPEG","res/menu_bg.jpg");
    }
    
    //main game loop
    
    public void update(float delta){
        
        updateFPS();
        render();
     
    }
        
    
    //misc methods--------------------------------------------------------------
    
    public static int getWindowWidth() {return DISPLAY_WIDTH; }
    public static int getWindowHeight() {return DISPLAY_HEIGHT; }
    public static int getTileSize() {return tileSize;}
    
    public static TrueTypeFont getFont(){return font;}
    public static TrueTypeFont getFontAlt(){return font1;}
    
    public static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle(GAME_TITLE + " FPS: " + fps);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }
    
    //initiate the display
    public void initDisplay(){
        try{    
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH,DISPLAY_HEIGHT));
        Display.create();
        } catch(LWJGLException e){
            AL.destroy();
            System.exit(0);
        }
        height_unit = Display.getHeight()/10;
    }
    
    //Destroys screen and context
    private void cleanUp(){
     
        Display.destroy();
    }
    
    //main game loop
    public void gameLoop(){
        
        levelInitialization();
        
        //startLevel(level1);
        
       
        
        while(!Display.isCloseRequested() && !game_is_running){
            
            if(!menu_music.isPlaying()){
                 menu_music.playAsMusic(1f, 1f, true);
            }
            
            float delta = getDelta(); // gets delta every frame;
            
            inputPool();
            update(delta);
            checkOption();

            Display.update();
            Display.sync(MAX_FPS);
            
        }
        
        AL.destroy();
    }
    
    public void checkOption(){
        
        if(current_selection == 1){
            arrow.setX(option1_x - tileSize*2);
            arrow.setY(option1_y);
        }else if(current_selection == 2){
            arrow.setX(option2_x - tileSize*2);
            arrow.setY(option2_y);
        }else{
            arrow.setX(option3_x - tileSize*2);
            arrow.setY(option3_y);
        }
            
    }
        //Returns the delta time 
        public static float getDelta() {
        long time = getTime(); // get the current time
        float delta = (time - lastFrame); // subtract the last time from current time and store delta
        lastFrame = time; //set the current time to be the last time
        return delta/100; // return delta (/100 to adjust to system needs)
    }
        
    public static Program getCurrentProgram(){
        return current_program;
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
    
    private void setGameIsRunning(boolean isRunning){
        game_is_running = isRunning;
    }
    
    private boolean getGameIsRunning(){
        return game_is_running;
    }
    
    public void startLevel(Level level){
        
        setGameIsRunning(true);
    
        getDelta(); // bla
        lastFPS = getTime();
        level.start(tileSize);
        
        setGameIsRunning(false);
    
    }
    
    Level level1;
    Level level2;
    Level level3;
    
    public void levelInitialization(){
        
        level1 = new Level(tileSize);
        level1.setMusic("res/level1_music.wav");
        level2 = new Level(tileSize);
        level3 = new Level(tileSize);
        
    }
    
    public void start(){
        
        gameLoop();
        
    }
    
        //program main method !
    public static void main(String[] argv){
        Menu current_game = new Menu();
        current_game.start();
    }
    
}

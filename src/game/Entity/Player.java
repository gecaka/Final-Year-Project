
package game.Entity;

import game.Entity.structures.LandNPC;
import Engine.Program;
import Utils.Helper;
import game.Entity.structures.Projectile;
import game.Level;
import static game.Level.map;
import game.Menu;
import java.io.IOException;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

public class Player extends LandNPC{
    
    public short weapon = 1;
    private Audio normal_attack_effect;
    private Audio fire_attack_effect;
    private Audio ice_attack_effect;
    
    public Player(int xIn, int yIn){
        
        super(xIn,yIn);
        try {
            normal_attack_effect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/normal_shoot.wav"));
            fire_attack_effect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/fire_shoot.wav"));
            ice_attack_effect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/ice_shoot.wav"));
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        setTexture("PNG","res/main_char.png");
        setSpeed(30);
        
    }
    
@Override
    public void render(){
        
        checkDirection();
        
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
        
        glDrawElements(GL_TRIANGLES,index_buffer);
        
    }
    
    @Override
    public void update(float delta){
        
        inputPool();
        moveCharacter(delta);
        checkCharacterGravity(delta);
        
    }
    
    @Override
    public void checkCharacterGravity(float delta){
        if(!Level.checkCollisionWithWorld(0,(getSpeed()+velocity_y)*delta,this) && fall != false){
            
            map.moveDown((getSpeed()+velocity_y)*delta);
            dY((getSpeed()+velocity_y)*delta);
            
            if(velocity_y < max_velocity){
                velocity_y = velocity_y + acceleration;
                
            }
        }else{
            velocity_y = 0;
        }
        
    }
    
        @Override    
    public void moveCharacter(float delta){
        
        if(move_up){
            float jump_distance = 96;            
                    fall = false;
                    
                    if(getY() >= jump_y - jump_distance){
                    
                        if(!Level.checkCollisionWithWorld(0,-getSpeed()*delta,this)){
                        map.moveUp(getSpeed()*delta);
                            dY(-getSpeed()*delta);
                        }else{
                           
                            move_up = false;
                            fall = true;
                            //break;
                        }
                    }
                    else{
                        move_up = false;
                        fall = true;
                    }
                    
        }
        if(move_down){
            if(!Level.checkCollisionWithWorld(0,getSpeed()*delta,this)){
                    map.moveDown(getSpeed()*delta);
                    dY(getSpeed()*delta);
            }
        }
        if(move_left){
            if(!Level.checkCollisionWithWorld(-getSpeed()*delta,0,this)){
                map.moveLeft(getSpeed()*delta);
                dX(-getSpeed()*delta);
            }
        }
        if(move_right){
            if(!Level.checkCollisionWithWorld(getSpeed()*delta,0,this)){
                map.moveRight(getSpeed()*delta);
                dX(getSpeed()*delta);
            }
        }
    }
    
        public void inputPool(){
            
            if(Mouse.next()){
                
                if(Mouse.getEventButton() == 0){

                    if(Mouse.getEventButtonState()){
                    shoot=true;
                    }else{
                    shoot=false;
                    }
                }
                
                if(Mouse.getEventButton() == 1){

                    if(Mouse.getEventButtonState()){
                    shoot=true;
                    }else{
                    shoot=false;
                    }
                }
              
            }
            
            if(Keyboard.next()){
                
                if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
                    if(Keyboard.getEventKeyState()){
                        Level.exitGame();
                    }
                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_1){
                    if(Keyboard.getEventKeyState()){
                        weapon = 1;
                    }
                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_2){
                    if(Keyboard.getEventKeyState()){
                        weapon = 2;
                    }
                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_3){
                    if(Keyboard.getEventKeyState()){
                        weapon = 3;
                    }
                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_A){
                    
                    if(Keyboard.getEventKeyState()){
                        moveLeft(true);
                    }else{
                        moveLeft(false);
                    }
                }
                if(Keyboard.getEventKey() == Keyboard.KEY_D){
                    if(Keyboard.getEventKeyState()){
                        moveRight(true);
                    }else{
                        moveRight(false);
                    }
                }  
                
                if(Keyboard.getEventKey() == Keyboard.KEY_S){
                    if(Keyboard.getEventKeyState()){
                        moveDown(true);
                    }else{
                        moveDown(false);
                    }
                }
                if(Keyboard.getEventKey() == Keyboard.KEY_W){
                    if(isOnFloor){
                        if(Keyboard.getEventKeyState()){
                            jump_y = getY();
                            moveUp(true);
                        }else{
                            fall = true;
                            moveUp(false);
                        }
                    }
                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_P){
                    if(Keyboard.getEventKeyState()){
                         Level.pause(!Level.paused);
                    }else{
                        
                         if(Level.paused == true){    
                            Level.pause(true);
                         }else{
                            Level.pause(false);
                        }
                    }

                }
                
                if(Keyboard.getEventKey() == Keyboard.KEY_R){
                    if(Keyboard.getEventKeyState()){
                         Menu.reloadProgram();
                    }else{
                        
                    }

                }
            }
            
            SoundStore.get().poll(0);
            
            if(shoot==true){
                shoot(Mouse.getX(),Mouse.getY());
            }
        }
    
    public void shoot(float mouseX, float mouseY){
        
        Projectile usedProj = null;
        
        if(weapon == 1){
            
            normal_attack_effect.playAsSoundEffect(1f, 1f, false);
            usedProj = new Projectile(x,y-10,name);
            Level.createObject(usedProj);
            usedProj.setSpread(10);
            usedProj.move(mouseX, mouseY);
            
        }else if(weapon == 2){
            
            ice_attack_effect.playAsSoundEffect(1f, 1f, false);
            usedProj = new Projectile(x,y,name);
            usedProj.setSpeed(9);
            usedProj.setTexture("PNG", "res/projectile_ice.png");
            usedProj.setSpread(20);
            Level.createObject(usedProj);
            usedProj.move(mouseX, mouseY);
            
        }else if(weapon == 3){
            
            fire_attack_effect.playAsSoundEffect(1f, 1f, false);
            usedProj = new Projectile(x,y,name);
            usedProj.setSpeed(9);
            usedProj.setTexture("PNG", "res/projectile_fire.png");
            usedProj.setSpread(40);
            Level.createObject(usedProj);
            usedProj.move(mouseX, mouseY);
            
        }
       
    }
    
    public void teleport(float coordx,float coordy){
        float translate_x = x-coordx;
        float translate_y = y-coordy;
        dX(translate_x);
        map.moveUp(translate_x);
        dY(translate_y);
        map.moveLeft(translate_y);
    }
    
}



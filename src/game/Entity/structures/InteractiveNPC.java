/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.Entity.structures;

import Engine.Program;
import Utils.Helper;
import static Utils.Helper.cX;
import static Utils.Helper.cY;
import static Utils.Helper.makeFloatBuffer;
import game.TileMap.TileMap;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 *
 * @author Anonymous
 */
public abstract class InteractiveNPC extends GameObject implements Interactive {
    
    protected boolean move_up = false;
    protected boolean move_down = false;
    protected boolean move_left = false;
    protected boolean move_right = false;
    protected boolean shoot = false;
    
    protected boolean fall = true;
    
    public float health = 100;
    public float attack_dmg = 2;
    
    public boolean remove;
    
    public InteractiveNPC(int xIn, int yIn) {
        super(xIn, yIn);
        remove = false;
    }
    
    @Override
        public void render(){
            
            vertices[0] = cX(x);
            vertices[1] = cY(y);
            
            vertices[9] = cX(x);
            vertices[10] = cY(y + ts);
            
            vertices[18] = cX(x + ts);
            vertices[19] = cY(y + ts);
            
            vertices[27] = cX(x + ts);
            vertices[28] = cY(y);
            
            checkDirection();
        
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
        glUniform2(uniform_loc,trans_buffer);
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
        
        //("Wazaaap bro");
    }
    
    public abstract void moveCharacter(float delta);
    
    public void checkDirection(){
        if(move_left){
            turnLeft();
        }else if(move_right){
            turnRight();
        }
    }
    
    public void moveLeft(boolean state){
        move_left = state;
    }
    
    public void moveRight(boolean state){
        move_right = state;
    }
    
    public void moveUp(boolean state){
        move_up = state;
    }
    
    public void moveDown(boolean state){
        move_down = state;
    }
    
    public void shoot(boolean state){
        shoot = state;
    }
    
    public void attack(InteractiveNPC obj){
        obj.reduceHealth(attack_dmg);
    }
    
    public void reduceHealth(float dmg){
        health = health - dmg;
    }
    public void regainHealth(float regainAmount){
        health = health + regainAmount;
    }
    
    public float getDamage(){
        return attack_dmg;
    }
    
    public void setDamage(int damageIn){
        attack_dmg = damageIn;
    }
    
    public abstract void checkDeath();
    public abstract void die();
    
    public abstract void checkCharacterGravity(float delta);
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.Entity.structures;

import game.Level;
import static game.Level.map;

/**
 *
 * @author Anonymous
 */
public abstract class LandNPC extends InteractiveNPC {
    
    protected float jump_y;
    protected float velocity_y = 0;
    protected float acceleration = 1.5f;
    protected float max_velocity = 100;

    public LandNPC(int xIn, int yIn) {
        super(xIn, yIn);
        speed = 20;
    }
   
    @Override
    public void checkCharacterGravity(float delta){
        if(!Level.checkCollisionWithWorld(0,(getSpeed()+velocity_y)*delta,this) && fall != false){
            
            dY((getSpeed()+velocity_y)*delta);
            
            if(velocity_y < max_velocity){
                velocity_y = velocity_y + acceleration;                
            }
                
        }else{
            velocity_y = 0;
        }
    }
    
    public void moveCharacter(float delta){
        
        if(move_up){
            
            float jump_distance = 96;            
                    fall = false;
                    
                    if(getY() >= jump_y - jump_distance){
                    
                        if(!Level.checkCollisionWithWorld(0,-getSpeed()*delta,this)){
                        
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
                    dY(getSpeed()*delta);
            }
        }
        if(move_left){
            if(!Level.checkCollisionWithWorld(-getSpeed()*delta,0,this)){
                dX(-getSpeed()*delta);
            }
        }
        if(move_right){
            if(!Level.checkCollisionWithWorld(getSpeed()*delta,0,this)){
                dX(getSpeed()*delta);
            }
        }
    }
    
    @Override
    public void update(float delta){
        if(!Level.paused){
        checkDeath();
        moveCharacter(delta);
        checkCharacterGravity(delta);
        AI(delta);
        }
    }

    @Override
    public void interact(InteractiveNPC hitObject) {
        
    }

    @Override
    public void checkInteraction() {
         
    }

    @Override
    public void checkDeath() {
        if(health <= 0){
            this.die();
        }
    }

    @Override
    public void die() {
      this.remove = true;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.Entity.structures;

import game.Level;

/**
 *
 * @author Anonymous
 */
public abstract class AirNPC extends InteractiveNPC {

    public AirNPC(int xIn, int yIn) {
        super(xIn, yIn);
        fall = false;
        
    }

    @Override
    public void interact(InteractiveNPC hitObject) {
        
    }

    @Override
    public void moveCharacter(float delta) {
        if (move_up) {
            fall = false;

            if (!Level.checkCollisionWithWorld(0, -getSpeed(), this)) {
                //map.moveUp(getSpeed());
                dY(-getSpeed()*delta);
            } else {
                move_up = false;
                fall = true;
                //break;
            }
        } else {
            move_up = false;
        }

        if(move_down){
            if(!Level.checkCollisionWithWorld(0,getSpeed(),this)){
                    //map.moveDown(getSpeed());
                    dY(getSpeed()*delta);
            }
        }
        if(move_left){
            if(!Level.checkCollisionWithWorld(-getSpeed(),0,this)){
                //map.moveLeft(getSpeed());
                dX(-getSpeed()*delta);
            }
        }
        if(move_right){
            if(!Level.checkCollisionWithWorld(getSpeed(),0,this)){
                //map.moveRight(getSpeed());
                dX(getSpeed()*delta);
            }
        }
    }

    @Override
    public void checkCharacterGravity(float delta) {
        if(health<=0){
            fall=true;
        }
        
        if(!Level.checkCollisionWithWorld(0,getSpeed(),this) && fall != false){
            short speed_boost = 5;
            dY(getSpeed()+speed_boost*delta);
        }
    }
    
    @Override
    public void update(float delta){
        moveCharacter(delta);
        checkCharacterGravity(delta);
        AI(delta);
    }

    @Override
    public void checkInteraction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkDeath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void die() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

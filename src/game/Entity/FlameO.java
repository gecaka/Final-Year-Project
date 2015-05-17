/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.Entity;

import game.Entity.structures.AirNPC;
import game.Entity.structures.InteractiveNPC;
import game.Level;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Anonymous
 */
public class FlameO extends AirNPC{

    public FlameO(int xIn, int yIn) {
        super(xIn, yIn);
        setTexture("PNG","res/fire_sprite.png");
        moveRight(true);
    }
    
     @Override
    public void AI(float delta) {
        
        //if(Game.)
        
        if(Level.checkCollisionWithWorld(-getSpeed(), 0, this)){
            moveLeft(false);
            moveRight(true);
        }else if(Level.checkCollisionWithWorld(getSpeed(), 0, this)){
            moveRight(false);
            moveLeft(true);
        }
        
        checkInteraction();
        
    }

    @Override
    public void interact(InteractiveNPC hitObject) {
        if(hitObject instanceof Player){
            //System.out.println(name+" hit the Player");
            attack(hitObject);
            if(hitObject.health <= 0){
                Display.destroy();
                System.exit(0);
            }
        }else{
            //System.out.println(name+" hit another game object "+ hitObject.name);
        }
    }
    
    @Override
    public void checkInteraction(){
        InteractiveNPC temp = Level.checkCollisionWithInteractiveObjects(this);
 
        if(temp != null){
            interact(temp);
        }
    }
    
}

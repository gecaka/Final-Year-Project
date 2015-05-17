package game.Entity;

import game.Entity.structures.Intelligent;
import game.Entity.structures.LandNPC;
import game.Entity.structures.InteractiveNPC;
import game.Level;
import org.lwjgl.opengl.Display;

public class Walker extends LandNPC {

    public Walker(int xIn, int yIn) {
        super(xIn, yIn);
        setTexture("PNG","res/enemy_basic.png");
        speed = 40;
        moveRight(true);
    }

    public void AI(float delta) {
        
        if(Level.checkCollisionWithWorld(-getSpeed()*delta, 0, this)){
            moveLeft(false);
            moveRight(true);
        }else if(Level.checkCollisionWithWorld(getSpeed()*delta, 0, this)){
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

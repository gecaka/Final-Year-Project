package game.Entity;

import Engine.Background;
import game.Entity.structures.Intelligent;
import game.Entity.structures.LandNPC;
import game.Entity.structures.InteractiveNPC;
import game.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

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
                Level.exitGame();
                
                /*boolean exit = false;
                
                Background bg = new Background();
                //bg.setTexture("PNG", "res/lost_scr.png");
                
                while(!exit){
                    
                if(Keyboard.next()){
                
                        if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
                            if(Keyboard.getEventKeyState()){
                                exit = true;
                  
                }
                
                        }
                
                }    
                    
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);    
                
                bg.render();
                
                }
                Level.exitGame();*/
                
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

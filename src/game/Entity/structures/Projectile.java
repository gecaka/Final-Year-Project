/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.Entity.structures;
import game.Entity.Player;
import game.Level;
import java.util.Random;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
/**
 *
 * @author Anonymous
 */
public class Projectile extends AirNPC {

    private String owner;
    public float velX = 0;
    public float velY = 0;
    public int spread = 0;
    
    public Projectile(int xIn, int yIn,String ownerName) {
        super(xIn, yIn);
        owner = ownerName;
        setTexture("PNG","res/bullet.png");
        speed = 16;
    }

    public Projectile(float xIn, float yIn,String ownerName) {
        super((int)xIn, (int)yIn);
        owner = ownerName;
        setTexture("PNG","res/bullet.png");
        speed = 16;
    }
    
    public void move(float mX, float mY){
        
        int shot_spread = 0;
        
        if(spread != 0){
            
            int min = -spread;
            int max = spread;
            
            Random rand = new Random();
            shot_spread = rand.nextInt((max - min) + 1) + min;
            
        }

    //Then, I try to get the angle.
        
        
        mX = (mX + (x - (Display.getWidth() / 2))) + shot_spread;
        
        if(Mouse.getY() > Display.getHeight()/2){ //bottom quadrants
            
            if(Mouse.getX() > Display.getWidth()/2){ //bottom right quadrant
                mY = (mY - (y - (Display.getHeight()/2)));
                mY = (Display.getHeight()- mY) + shot_spread;

            }else{ // bottom left quadrant
                mY = (mY - (y - (Display.getHeight()/2))) + shot_spread;
                mY = (Display.getHeight()- mY);  
            }
            
        }else{ // top quadrants
            
            if(Mouse.getX() >Display.getWidth()/2){ //top right quadrant
                mY = (mY - (y - (Display.getHeight()/2))) + shot_spread;
                mY = (Display.getHeight()- mY); 
                

            }else{ //top left quadrant
                mY = (mY - (y - (Display.getHeight()/2))); 
                mY = (Display.getHeight()- mY) + shot_spread; 
            }
            
        }

    double angle = Math.atan2(mY - y, mX - x);

    float scaleX = (float)Math.cos(angle);
    float scaleY = (float)Math.sin(angle);

    velX = scaleX * speed; //speed is already defined
    velY = scaleY * speed;

    }
    
    public void moveLoop(){
 
    x += velX;
    y += velY;
    
    }
    
    public void setSpread(int sprd){
        spread = sprd;
    }
    
    public void destroy(){
        remove = true;
    }
    
    @Override
    public void AI(float delta){
        
        moveLoop();
        checkInteraction();
        if(Level.checkCollisionWithWorld(0, 0, this)){
            destroy();
        }
    }

    @Override
    public void interact(InteractiveNPC hitObject) {
        if(!(hitObject instanceof Projectile) && owner != hitObject.name){
            attack(hitObject);
            destroy();
        }
    }

    @Override
    public void checkInteraction() {
        InteractiveNPC temp = Level.checkCollisionWithInteractiveObjects(this);
 
        if(temp != null){
            interact(temp);
        }
    }
    
}

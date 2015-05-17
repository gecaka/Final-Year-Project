/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author Anonymous
 */
public class Program {
    
    private static int current_program_id;
    private int program_id;
    private int link_status = GL11.GL_FALSE;
    
    public Program(Shader vertex_shader,Shader fragment_shader){
        
        program_id = glCreateProgram();
        
        glAttachShader(program_id,vertex_shader.getShaderID());
        glAttachShader(program_id,fragment_shader.getShaderID());
        
        glLinkProgram(program_id);
        
        link_status = glGetProgram(program_id,GL_LINK_STATUS);
        
        if( link_status == GL11.GL_FALSE)
		{
			System.out.println("Couldnt link the program. Reason: " + glGetProgramInfoLog(program_id, 1000));
		}
        
    }
    
    public void useProgram(){
        glUseProgram(program_id);
        current_program_id = program_id;
    }
    
    public void releaseProgram(){
        glUseProgram(0);
        current_program_id = 0;
    }
    
    public int getProgram(){
        return program_id;
    }
    
    public static int getCurrentProgram(){
        return current_program_id;
    }
    
    public boolean isReadyToLoad(){
        if (link_status == GL11.GL_FALSE){
            return false;
        }
        else{
            return true;
        }
    }
    
}

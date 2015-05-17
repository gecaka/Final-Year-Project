package Engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    
    private int shader_id = 0;
    private String filename;
    private int shader_status;
    
    private String source = "";
    private boolean ready_to_load = true;
    
    public Shader(String var_filename,int var_shader_type){
        
        compileShader(var_filename,var_shader_type);
                
    }
    
    public Shader(){
        
    }
    
    private void compileShader(String var_filename,int var_shader_type){
        int check_shader_id = glCreateShader(var_shader_type);
        
        
        if(check_shader_id == 0){
            throw new RuntimeException("could not created shader of type "+var_shader_type+" for file "+var_filename+".");
        }
        
        source = loadFile(var_filename);
        
        glShaderSource(check_shader_id,source);
        
        glCompileShader(check_shader_id);
        
        shader_status = glGetShader(check_shader_id,GL_COMPILE_STATUS);
        
        if( shader_status == GL11.GL_FALSE){
                        System.out.println("compilation error for shader ["+var_filename+"]. Reason: " + glGetShaderInfoLog(shader_id, 1000));
                        ready_to_load = false;
        }else{
            shader_id = check_shader_id;
        }
    }
    
    private String loadFile(String var_filename){
        
        StringBuilder code = new StringBuilder();
        String line = null;
        
        try{
            
            File shader_file = new File(var_filename);
            filename = shader_file.getName();
            
            BufferedReader reader = new BufferedReader(new FileReader(shader_file));
		    
            while( (line = reader.readLine()) !=null )
		    {
		    	code.append(line);
		    	code.append('\n');
                    }
            
                    
        }catch(Exception ex){
            throw new IllegalArgumentException("unable to load shader from file ["+var_filename+"]", ex);
        }   
        
        return code.toString();
    }
    
    //Return the shader source code.
    public String getShaderCode(){
        
        return source;
    }
    
    //return shader ID;
    public int getShaderID(){
        return shader_id;
    }
    
    public boolean isReadyToLoad(){
        return ready_to_load;
    }
    
}

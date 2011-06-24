package nl.secondfloor.mojo.jsduck;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Says "Hi" to the user.
 * 
 * @goal sayhi
 */
public class GreetingMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException {
        getLog().info("Hello, world.");
        
        String engineName = "jruby";

        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine jRubyEngine = manager.getEngineByName(engineName);

        jRubyEngine.put("engine", engineName);
        
        try {
            getLog().info(jRubyEngine.eval("return 'Hello, ' + $engine + '!'").toString());
        } catch (ScriptException e) {
            getLog().error(e);
        }
    }
}
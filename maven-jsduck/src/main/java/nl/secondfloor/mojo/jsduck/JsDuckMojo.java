package nl.secondfloor.mojo.jsduck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.jruby.embed.ScriptingContainer;

/**
 * Executes jsduck on the configured javascript directory to produce api documentation.
 * 
 * @goal jsduck
 */
public class JsDuckMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException {
        getLog().info("Producing JavaScript API documentation using jsduck.");
        getLog().info(String.format("Using javascript directory: %s.", ""));
        getLog().info(String.format("Using target directory: %s.", ""));

        ScriptingContainer jruby = new ScriptingContainer();

        File templateDir = new File("target/jsduck_template");
        if (!templateDir.exists()) {
            final File templates;
            try {
                templates = new File(this.getClass().getClassLoader().getResource("template").toURI());
            } catch (URISyntaxException e) {
                throw new MojoExecutionException("Failed to prepare template directory.", e);
            }

            try {
                FileUtils.copyDirectory(templates, templateDir);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to copy templates.", e);
            }
        }

        try {
            InputStreamReader scriptInputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("jsduck.rb"));
            BufferedReader scriptReader = new BufferedReader(scriptInputStreamReader);
            String script = IOUtils.toString(scriptReader);
            
//            input_path = "src/main/webapp/js"
//                    output_path = "src/main/webapp/api"
//                    verbose = true
//            jruby.put("input_path", "");
//            jruby.put("output_path", "");
//            jruby.put("verbose", "");

            jruby.runScriptlet(script);
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoExecutionException("Failed to read script.", e);
        }
    }
}
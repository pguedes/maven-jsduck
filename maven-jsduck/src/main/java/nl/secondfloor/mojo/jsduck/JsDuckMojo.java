package nl.secondfloor.mojo.jsduck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.jruby.embed.ScriptingContainer;

import de.schlichtherle.truezip.file.TFile;

/**
 * Executes jsduck on the configured javascript directory to produce api
 * documentation.
 * 
 * @goal jsduck
 */
public class JsDuckMojo extends AbstractMojo {

    /**
     * The javascript source directory to generate documentation for.
     * 
     * @parameter expression="${jsduck.javascriptDirectory}"
     *            default-value="src/main/webapp/js"
     */
    private String javascriptDirectory;
    /**
     * The target directory to generate the API documentation in.
     * 
     * @parameter expression="${jsduck.targetDirectory}"
     *            default-value="src/main/webapp/api"
     */
    private String targetDirectory;
    /**
     * Set to <code>true</code> to print feedback while running.
     * 
     * @parameter expression="${jsduck.verbose}" default-value="false"
     */
    private boolean verbose;

    public void execute() throws MojoExecutionException {
        getLog().info("Producing JavaScript API documentation using jsduck.");
        getLog().info(String.format("Using javascript directory: %s.", javascriptDirectory));
        getLog().info(String.format("Using target directory: %s.", targetDirectory));

        ScriptingContainer jruby = new ScriptingContainer();

        File templateDir = new File("target/jsduck_template");
        if (!templateDir.exists()) {
            try {
                TFile tFile = new TFile(this.getClass().getClassLoader().getResource("template").toURI());
                tFile.cp_rp(templateDir);
            } catch (URISyntaxException e) {
                throw new MojoExecutionException("Failed to prepare template directory.", e);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to populate template directory.", e);
            }
        }

        try {
            InputStreamReader scriptInputStreamReader = new InputStreamReader(getClass().getClassLoader()
                    .getResourceAsStream("maven_jsduck.rb"));
            BufferedReader scriptReader = new BufferedReader(scriptInputStreamReader);
            String script = IOUtils.toString(scriptReader);

            jruby.put("input_path", javascriptDirectory);
            jruby.put("output_path", targetDirectory);
            jruby.put("verbose", verbose);

            jruby.runScriptlet(script);
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoExecutionException("Failed to read script.", e);
        }
    }
}
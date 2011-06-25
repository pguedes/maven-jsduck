package nl.secondfloor.mojo.jsduck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.jruby.embed.ScriptingContainer;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.fs.FsSyncException;

/**
 * Executes jsduck on the configured javascript directory to produce api
 * documentation.
 * <p>
 * The ruby part is in <code>src/main/resources/maven_jsduck.rb</code>
 * </p>
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
        if (verbose) {
            getLog().info(String.format("Using javascript directory: %s.", new File(javascriptDirectory).getAbsolutePath()));
            getLog().info(String.format("Using target directory: %s.", new File(targetDirectory).getAbsolutePath()));
        }

        File templateDir = new File("target/jsduck_template");
        if (!templateDir.exists()) {
            prepareTemplates(templateDir);
        }

        runJsDuck();
    }

    /**
     * Prepare the templates used by jsduck by copying from the plugin jar to the target directory.
     * @param templateDir The target directoy.
     * @throws MojoExecutionException
     */
    private void prepareTemplates(File templateDir) throws MojoExecutionException {
        templateDir.mkdirs();
        try {
            URI templateUri = this.getClass().getClassLoader().getResource("template").toURI();
            if (verbose) {
                getLog().info(String.format("Copying templates from %s to %s.", templateUri, templateDir.getAbsolutePath()));
            }
            
            copyTemplates(templateUri, templateDir);
        } catch (URISyntaxException e) {
            throw new MojoExecutionException("Failed to prepare template directory.", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to populate template directory.", e);
        }
    }

    /**
     * Recursively copy the templates.
     * @param from the source.
     * @param to the destination.
     * @throws IOException
     * @throws FsSyncException
     */
    private void copyTemplates(URI from, File to) throws IOException, FsSyncException {
        TFile tFile = new TFile(from);
        try {
            tFile.cp_rp(to);
        } finally {
            TFile.umount();
        }
    }

    /**
     * Runs the maven_jsduck.rb script in JRuby with the plugin configuration.
     * @throws MojoExecutionException When an IOException occurs.
     */
    private void runJsDuck() throws MojoExecutionException {
        try {
            InputStreamReader scriptInputStreamReader = new InputStreamReader(getClass().getClassLoader()
                    .getResourceAsStream("maven_jsduck.rb"));
            BufferedReader scriptReader = new BufferedReader(scriptInputStreamReader);
            String script = IOUtils.toString(scriptReader);

            ScriptingContainer jruby = new ScriptingContainer();
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
package nl.secondfloor.mojo.jsduck;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import de.schlichtherle.truezip.file.TFile;

/**
 * Cleans up the generated jsduck documentation.
 * 
 * @goal clean-jsduck
 */
public class CleanJsDuckMojo extends AbstractMojo {

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
        if (verbose) {
            getLog().info("Cleaning generated JavaScript API documentation.");
            getLog().info(String.format("Using target directory: %s.", targetDirectory));
        }

        try {
            TFile.rm_r(new File(targetDirectory));
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to remove targetDirectory.", e);
        }
    }
}
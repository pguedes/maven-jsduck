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
     *            default-value="target/jsduck-api"
     */
    private String targetDirectory;
    /**
     * Set to <code>true</code> to print feedback while running.
     * 
     * @parameter expression="${jsduck.verbose}" default-value="true"
     */
    private boolean verbose;

    public void execute() throws MojoExecutionException {
        if (verbose) {
            getLog().info("Cleaning generated JavaScript API documentation.");
        }
        File targetDir = new File(targetDirectory);
        if (targetDir.exists()) {
            getLog().info(String.format("Deleting %s", targetDir.getAbsolutePath()));

            try {
                TFile.rm_r(targetDir);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to remove targetDirectory.", e);
            }
        }
    }
}
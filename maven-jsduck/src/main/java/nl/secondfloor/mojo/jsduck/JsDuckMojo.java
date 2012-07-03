package nl.secondfloor.mojo.jsduck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
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
 * 
 * @goal jsduck
 */
public class JsDuckMojo extends AbstractMojo {

	/**
	 * The source directories to generate documentation for.
	 * 
	 * @parameter
	 */
	private String[] sources;

	/**
	 * The target directory to generate the API documentation in.
	 * 
	 * @parameter property="targetDirectory" default-value="target/jsduck-api"
	 */
	private String targetDirectory;
	
	/**
	 * @parameter default-value="src/main/webapp/js"
	 */
	private String javascriptDirectory;

	/**
	 * @parameter
	 */
	private String guides;

	/**
	 * Set to <code>true</code> to print feedback while running.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean verbose;
	
	/**
	 * The welcome page for the API documentation
	 * 
	 * @parameter default-value="src/main/jsduck/welcome.html"
	 */
	private String welcome;
	
	/**
	 * The directory for any resources used by the welcome page.
	 * This is relative to the welcome page itself.
	 * 
	 * @parameter default-value="resources"
	 */
	private String welcomeResources;
	
	/**
	 * The title for the documentation
	 * 
	 * @parameter default-value="${project.name} ${project.version}" 
	 */
	private String title;	
	
	/**
	 * The header for the documentation
	 * 
	 * @parameter default-value="${project.name} ${project.version} API"
	 */
	private String header;
	
	/**
	 * Whether to document built in classes (Array, object etc)
	 * 
	 * @parameter default-value="false"
	 */
	private boolean builtinClasses;

	public void execute() throws MojoExecutionException {
		getLog().info("Producing JavaScript API documentation using jsduck.");
		if (verbose) {
			for (String f : getJavascriptDirectories()) {
				getLog().info(
						String.format("Using javascript directory: %s.",
								new File(f).getAbsolutePath()));
			}
			getLog().info(
					String.format("Using target directory: %s.", new File(
							targetDirectory).getAbsolutePath()));
		}

		File templateDir = new File("target/jsduck_template");
		if (!templateDir.exists()) {
			prepareTemplates(templateDir);
		}

		if(builtinClasses){
			File jsClasses = new File("target/js-classes");
			if (!jsClasses.exists()) {
				prepareBuiltinClasses(jsClasses);
			}
		}
		
		if (!new File(targetDirectory).exists()) {
			new File(targetDirectory).mkdirs();
		}
		
		runJsDuck();
		
		copyResources();
	}

	/**
	 * Prepare the templates used by jsduck by copying from the plugin jar to
	 * the target directory.
	 * 
	 * @param templateDir
	 *            The target directoy.
	 * @throws MojoExecutionException
	 */
	private void prepareTemplates(File templateDir)
			throws MojoExecutionException {
		templateDir.mkdirs();
		try {
			URI templateUri = this.getClass().getClassLoader()
					.getResource("template").toURI();
			if (verbose) {
				getLog().info(
						String.format("Copying templates from %s to %s.",
								templateUri, templateDir.getAbsolutePath()));
			}

			copyDirectories(templateUri, templateDir);
		} catch (URISyntaxException e) {
			throw new MojoExecutionException(
					"Failed to prepare template directory.", e);
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Failed to populate template directory.", e);
		}
	}
	
	/**
	 * Prepare jsclasses directory used by jsduck to document the builtin classes
	 * 
	 * @param jsClasses The target directoy.
	 * @throws MojoExecutionException
	 */
	private void prepareBuiltinClasses(File jsClasses)
			throws MojoExecutionException {
		jsClasses.mkdirs();
		try {
			URI jsUri = this.getClass().getClassLoader().getResource("js-classes").toURI();
			if (verbose) {
				getLog().info(
						String.format("Copying js-classes from %s to %s.", jsUri, jsClasses.getAbsolutePath()));
			}

			copyDirectories(jsUri, jsClasses);
		} catch (URISyntaxException e) {
			throw new MojoExecutionException(
					"Failed to prepare js-classes directory.", e);
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Failed to populate js-classes directory.", e);
		}
	}
	
	/**
	 * copy the resource for the welcome page into the target directory.
	 * 
	 * @throws MojoExecutionException
	 */
	private void copyResources() throws MojoExecutionException
	{
		File srcResDir = new File(new File(welcome).getParentFile(),  welcomeResources);
		
		if(!srcResDir.exists())
		{
			getLog().info(
					String.format("No resources to copy from: %s.", 
							new File(welcomeResources).getAbsolutePath()));
			
			return;
		}
				
		File targetResDir = new File(targetDirectory);
		
		if(!targetResDir.exists())
		{
			targetResDir.mkdirs();
		}
		
		try 
		{
			copyResources(srcResDir, targetResDir);
		} 
		catch (IOException e) 
		{
			throw new MojoExecutionException(
					"Failed to copy welcome resources directory.", e);
		}
	}
	
	/**
	 * copy files from one directory to another.
	 * @param srcDir
	 * @param parentTargetDir
	 * @throws IOException
	 */
	private void copyResources(File srcDir, File parentTargetDir) throws IOException
	{
		File targetDir = new File(parentTargetDir, srcDir.getName());
		targetDir.mkdirs();
		
		if(verbose)
		{
			getLog().info(String.format("Copying resources: %s to %s.", 
						srcDir.getAbsolutePath(), targetDir.getAbsolutePath()));
		}
		
		FileUtils.copyDirectory(srcDir, targetDir);
	}

	/**
	 * Recursively copy the templates.
	 * 
	 * @param from
	 *            the source.
	 * @param to
	 *            the destination.
	 * @throws IOException
	 * @throws FsSyncException
	 */
	private void copyDirectories(URI from, File to) throws IOException,
			FsSyncException {
		TFile tFile = new TFile(from);
		try {
			tFile.cp_rp(to);
		} finally {
			TFile.umount();
		}
	}

	/**
	 * Runs the maven_jsduck.rb script in JRuby with the plugin configuration.
	 * 
	 * @throws MojoExecutionException
	 *             When an IOException occurs.
	 */
	private void runJsDuck() throws MojoExecutionException {
		try {
			InputStreamReader scriptInputStreamReader = new InputStreamReader(
					getClass().getClassLoader().getResourceAsStream(
							"maven_jsduck.rb"));
			BufferedReader scriptReader = new BufferedReader(
					scriptInputStreamReader);
			String script = IOUtils.toString(scriptReader);

			ScriptingContainer jruby = new ScriptingContainer();
			jruby.put("input_path", getJavascriptDirectories());
			jruby.put("output_path", targetDirectory);
			jruby.put("verbose", verbose);
			jruby.put("guides", guides);
			jruby.put("welcome_path", welcome);
			jruby.put("title", title);
			jruby.put("header", header);	
			jruby.put("builtin_classes", builtinClasses);	

			jruby.runScriptlet(script);
		} catch (IOException e) {
			getLog().error(e);
			throw new MojoExecutionException("Failed to read script.", e);
		}
	}

	/**
	 * Get the input source directories
	 */
	private String[] getJavascriptDirectories() {
		Set<String> l = new LinkedHashSet<String>();
		
		if (sources != null)
		{
			l.addAll(Arrays.asList(sources));
		}
		else if (javascriptDirectory != null)
		{
			l.add(javascriptDirectory);
		}
				
		return l.toArray(new String[l.size()]);
	}
}
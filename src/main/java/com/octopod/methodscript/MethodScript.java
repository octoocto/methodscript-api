package com.octopod.methodscript;

import com.laytonsmith.core.MethodScriptCompiler;
import com.laytonsmith.core.MethodScriptComplete;
import com.laytonsmith.core.ParseTree;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.ConfigCompileException;
import com.laytonsmith.core.exceptions.ConfigCompileGroupException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Octopod Created on 5/24/14
 */

/**
 * Represents the compiled MethodScript.
 */
public class MethodScript
{
	/**
	 * The compiled MethodScript.
	 */
	private final ParseTree parseTree;

	/**
	 * The Target representing where the source of the script is.
	 */
	private final Target source;

	/**
	 * Compiles MethodScript from a string.
	 *
	 * @param str the script to compile
	 * @throws ConfigCompileException
	 * @throws ConfigCompileGroupException
	 */
	public MethodScript(String str)
			throws ConfigCompileException, ConfigCompileGroupException
	{
		this(str, false);
	}

	/**
	 * Compiles MethodScript from a string.
	 *
	 * @param str the script to compile
	 * @throws ConfigCompileException
	 * @throws ConfigCompileGroupException
	 */
	public MethodScript(String str, boolean autorun)
			throws ConfigCompileException, ConfigCompileGroupException
	{
		this(str, null, autorun);
	}

	/**
	 * Compiles MethodScript from a file.
	 *
	 * @param file
	 * @throws ConfigCompileException
	 * @throws ConfigCompileGroupException
	 * @throws IOException
	 */
	public MethodScript(File file)
			throws ConfigCompileException, ConfigCompileGroupException, IOException
	{
		this(file, false);
	}

	/**
	 * Compiles MethodScript from a file.
	 *
	 * @param file
	 * @throws ConfigCompileException
	 * @throws ConfigCompileGroupException
	 * @throws IOException
	 */
	public MethodScript(File file, boolean autorun)
			throws ConfigCompileException, ConfigCompileGroupException, IOException
	{
		this(read(file), file, autorun);
	}

	private MethodScript(String str, File file, boolean autorun)
			throws ConfigCompileException, ConfigCompileGroupException
	{
		//Sets the source to UNKNOWN if null
		if (file == null)
		{
			file = Target.UNKNOWN.file();
		}

		this.parseTree = MethodScriptCompiler.compile(MethodScriptCompiler.lex(str, file, true));
		Target t;
		try
		{
			//The IDE thinks this might throw an NPE?
			t = this.parseTree.getTarget();
		}
		catch(NullPointerException e)
		{
			t = Target.UNKNOWN;
		}

		this.source = t;

		if(autorun) execute();
	}

	private static String read(File file) throws IOException
	{
		final StringBuilder sb = new StringBuilder();

		BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));

		int n;
		while ((n = input.read()) != -1)
		{
			sb.append((char) n);
		}

		input.close();

		return sb.toString();
	}

	public String getSource()
	{
		return source.file().toString();
	}

	public Construct execute()
	{
		return execute(null, null);
	}

	public Construct execute(MSEnvironment environment)
	{
		return execute(environment, null);
	}

	public Construct execute(MethodScriptComplete post)
	{
		return execute(null, post);
	}

	/**
	 * Executes this MethodScript.
	 * If <code>environment</code> is null, a new one will be created, which may be
	 * desirable if you intend to not access the environment after executing it.
	 * If <code>done</code> isn't null, it will be executed after the MethodScript execution.
	 *
	 * @param post this will run after the code is done, can be null
	 *
	 * @return the Construct that results from this code
	 */
	public Construct execute(MSEnvironment environment, MethodScriptComplete post)
	{
		if(environment == null)
		{
			environment = new MSEnvironment();
		}

		//executes root auto_includes.ms
		MethodScriptCompiler.registerAutoIncludes(environment.getHandle(), null);

		return MethodScriptCompiler.execute(parseTree, environment.getHandle(), post, null);
	}

	/**
	 * Executes this MethodScript on a different thread.
	 *
	 * @return the thread this MethodScript is executing on
	 */
	public Thread executeAsync(final MSEnvironment environment, final MethodScriptComplete done)
	{
		Thread thread = new Thread() {
			public void run() {execute(environment, done);}
		};
		thread.start();
		return thread;
	}

	public Thread executeAsync()
	{
		return executeAsync(null, null);
	}
}

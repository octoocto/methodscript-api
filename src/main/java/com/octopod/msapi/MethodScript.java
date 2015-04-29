package com.octopod.msapi;

import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.core.MethodScriptCompiler;
import com.laytonsmith.core.MethodScriptComplete;
import com.laytonsmith.core.ParseTree;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
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
 * An object representing the compiled MethodScript.
 */
public class MethodScript
{
	/**
	 * The environment that the script will use.
	 */
	private final MScriptEnvironment env;

	/**
	 * If false, a clone of the original environment will be used instead of the environment directly.
	 */
	private boolean saveEnvironmentChanges = true;

	/**
	 * The compiled MethodScript.
	 */
	private ParseTree compiled;

	/**
	 * Compiles MethodScript from a string.
	 *
	 * @param str the script to compile
	 * @throws ConfigCompileException
	 * @throws ConfigCompileGroupException
	 */
	public MethodScript(String str) throws ConfigCompileException, ConfigCompileGroupException
	{
		this(str, null, null, false);
	}

	/**
	 * Compiles MethodScript from a string with an environment.
	 *
	 * @param script the script to compile
	 * @param env the environment to use
	 * @return the compiled script
	 *
	 * @throws ConfigCompileException
	 * @throws ConfigCompileGroupException
	 */
	@Deprecated
	public MethodScript(String script, Environment env) throws ConfigCompileException, ConfigCompileGroupException
	{
		this(script, null, new MScriptEnvironment(env), false);
	}

	public MethodScript(String script, MScriptEnvironment env) throws ConfigCompileException, ConfigCompileGroupException
	{
		this(script, null, env, false);
	}

	public MethodScript(String script, File source, MScriptEnvironment env, boolean autorun)
			throws ConfigCompileException, ConfigCompileGroupException
	{
		//Sets the source to UNKNOWN if null
		if (source == null)
		{
			source = Target.UNKNOWN.file();
		}

		this.compiled = MethodScriptCompiler.compile(MethodScriptCompiler.lex(script, source, true));
		Target t;
		try
		{
			//The IDE thinks this might throw an NPE?
			t = this.compiled.getTarget();
		}
		catch(NullPointerException e)
		{
			t = Target.UNKNOWN;
		}

		//Sets the environment to a default environment if null
		if (env == null)
		{
			env = new MScriptEnvironment(t);
		}

		this.env = env;

		if(autorun) execute();
	}

	public MethodScript(MethodScript other, MScriptEnvironment env)
	{
		this.compiled = other.compiled;
		this.env = other.env;
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

	public MScriptEnvironment environment()
	{
		return env;
	}

	public void setSaveEnvironmentChanges(boolean b)
	{
		this.saveEnvironmentChanges = b;
	}

	public String getSource()
	{
		return getTarget().file().toString();
	}

	public Target getTarget()
	{
		return compiled.getTarget();
	}

	public Construct execute()
	{
		return execute(null, null);
	}

	public Construct execute(MCCommandSender executor)
	{
		env.setExecutor(executor);
		return execute();
	}

	public Construct execute(MethodScriptComplete done)
	{
		return execute(done, null);
	}

	/**
	 * Executes this code.
	 *
	 * @param done        this will run after the code is done, can be null
	 * @param externalEnv the execution will use this environment if provided, can be null
	 *
	 * @return the Construct that results from this code
	 */
	public Construct execute(MethodScriptComplete done, Environment externalEnv)
	{
		Environment env;
		if (externalEnv == null)
			//Use our environment
			env = this.env.getHandle();
		else
			//Use the external environment; don't save procedures if this happens
			env = externalEnv;

		MethodScriptCompiler.registerAutoIncludes(this.env.getHandle(), null);

		return MethodScriptCompiler.execute(compiled, env, done, null);
	}

	public Thread executeAsync(final MethodScriptComplete done)
	{
		Thread thread = new Thread() {
			public void run() {execute(done);}
		};
		thread.start();
		return thread;
	}

	public Thread executeAsync()
	{
		return executeAsync(null);
	}
}
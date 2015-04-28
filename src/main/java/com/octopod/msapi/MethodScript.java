package com.octopod.util.commandhelper;

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
	private MethodScriptEnvironment environment = null;

	/**
	 * If false, a clone of the environment will be used in place of the real one so as to not save potential
	 * environment changes.
	 */
	private boolean dynamicEnv = false;

	/**
	 * The compiled CompiledMethodScript.
	 */
	private ParseTree compiled;

	private void compile(String script, File source, MethodScriptEnvironment env, boolean autorun)
	throws ConfigCompileException, ConfigCompileGroupException
	{
		//Sets the source to UNKNOWN if null
		if (source == null) source = Target.UNKNOWN.file();

		compiled = MethodScriptCompiler.compile(MethodScriptCompiler.lex(script, source, true));

		//Sets the environment to a default environment if null
		if (env == null)
			this.environment = new MethodScriptEnvironment(compiled.getTarget());
		else
			this.environment = env;

		if(autorun) execute();
	}

	@Deprecated
	/**
	 * Compiles MethodScript using a File as a source
	 * @deprecated Use the constructor with MethodScriptEnvironment instead (<code>new MethodScriptEnvironment(Environment)</code>)
	 */
	public MethodScript(String script, Environment env) throws ConfigCompileException, ConfigCompileGroupException
	{
		compile(script, null, new MethodScriptEnvironment(env), false);
	}


	/**
	 * Compiles CompiledMethodScript using UNKNOWN as the source. (just like the interpreter)
	 *
	 * @param script The CompiledMethodScript to compile
	 *
	 * @throws ConfigCompileException
	 */
	public MethodScript(String script) throws ConfigCompileException, ConfigCompileGroupException
	{
		compile(script, null, null, false);
	}

	public MethodScript(String script, MethodScriptEnvironment env) throws ConfigCompileException, ConfigCompileGroupException
	{
		compile(script, null, env, false);
	}

	public MethodScript(String script, boolean autorun) throws ConfigCompileException, ConfigCompileGroupException
	{
		compile(script, null, null, autorun);
	}

	/**
	 * Compiles CompiledMethodScript from a File, using the File as the source.
	 *
	 * @param file The file to read CompiledMethodScript from.
	 *
	 * @throws java.io.IOException
	 * @throws com.laytonsmith.core.exceptions.ConfigCompileException
	 */
	public MethodScript(File file, MethodScriptEnvironment env) throws IOException, ConfigCompileException, ConfigCompileGroupException
	{
		compile(read(file), file, env, false);
	}

	public MethodScript(File file) throws IOException, ConfigCompileException, ConfigCompileGroupException
	{
		compile(read(file), file, null, false);
	}

	public MethodScript(File file, boolean autorun) throws IOException, ConfigCompileException, ConfigCompileGroupException
	{
		compile(read(file), file, null, autorun);
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

	public MethodScriptEnvironment getEnvironment()
	{
		return environment;
	}

	public void setDynamicEnv(boolean dyn)
	{
		dynamicEnv = dyn;
	}

	public boolean isEnvironmentDynamic()
	{
		return dynamicEnv;
	}

	public String getSource()
	{
		return getTarget().file().toString();
	}

	public Target getTarget()
	{
		return compiled.getTarget();
	}

	public Construct execute(MCCommandSender executor)
	{
		environment.setExecutor(executor);
		return execute();
	}

	public Construct execute(MCCommandSender executor, Environment env)
	{
		environment.setExecutor(executor);
		return execute(env);
	}

	public Construct execute()
	{
		return execute((MCCommandSender)null, null);
	}

	public Construct execute(MethodScriptComplete done)
	{
		return execute(done, null);
	}

	public Construct execute(Environment env)
	{
		return execute((MCCommandSender) null, env);
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
			env = environment.getHandle();
		else
			//Use the external environment; don't save procedures if this happens
			env = externalEnv;

		MethodScriptCompiler.registerAutoIncludes(environment.getHandle(), null);

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
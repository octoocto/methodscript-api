package com.octopod.util.commandhelper;

import com.laytonsmith.core.exceptions.ConfigCompileException;
import com.laytonsmith.core.exceptions.ConfigCompileGroupException;

import java.io.File;
import java.io.IOException;

/**
 * @author Octopod - octopodsquad@gmail.com
 */
public class MethodScriptBuilder
{
	private MethodScriptEnvironment env;

	public MethodScriptBuilder()
	{
		this.env = new MethodScriptEnvironment();
	}

	public MethodScriptBuilder(MethodScriptEnvironment env)
	{
		this.env = new MethodScriptEnvironment(env);
	}

	public MethodScriptEnvironment getEnvironment()
	{
		return env;
	}

	public MethodScript compile(String script) throws ConfigCompileException, ConfigCompileGroupException
	{
		MethodScript ms = new MethodScript(script);
		return ms;
	}

	public MethodScript compile(File file) throws IOException, ConfigCompileException, ConfigCompileGroupException
	{
		MethodScript ms = new MethodScript(file, env);
		return ms;
	}
}

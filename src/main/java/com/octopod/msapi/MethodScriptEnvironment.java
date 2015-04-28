package com.octopod.util.commandhelper;

import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.core.Procedure;
import com.laytonsmith.core.constructs.*;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.environments.Environment.EnvironmentImpl;
import com.laytonsmith.core.environments.GlobalEnv;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Octopod - octopodsquad@gmail.com
 */

/**
 * A wrapper for the Environment object.
 * A Target is paired with the Environment for getting variables/procedures and such.
 * If no Target is given, Target.UNKNOWN will be used.
 */
public class MethodScriptEnvironment
{
	private Environment env;
	private Target t;

	public MethodScriptEnvironment()
	{
		this.env = CommandHelper.newEnvironment();
		this.t = Target.UNKNOWN;
	}

	public MethodScriptEnvironment(Target t)
	{
		this.env = CommandHelper.newEnvironment();
		this.t = t;
	}

	/**
	 * Creates a MethodScriptEnvironment (wrapper) from an Environment.
	 * Target will be UNKNOWN.
	 *
	 * @param env the environment
	 */
	public MethodScriptEnvironment(Environment env)
	{
		this.env = env;
		this.t = Target.UNKNOWN;
	}

	/**
	 * Creates a MethodScriptEnvironment (wrapper) from an Environment.
	 *
	 * @param env the environment
	 * @param t the target to use
	 */
	public MethodScriptEnvironment(Environment env, Target t)
	{
		this.env = env;
		this.t = t;
	}

	/**
	 * Creates a copy of a MethodScriptEnvironment.
	 *
	 * @param msEnv the original environment
	 */
	public MethodScriptEnvironment(MethodScriptEnvironment msEnv)
	{
		try
		{
			this.env = msEnv.env.clone();
			this.t = msEnv.t;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Unable to copy this MethodScriptEnvironment");
		}
	}

	/**
	 * Gets the internal Environment.
	 *
	 * @return the internal environment
	 */
	public Environment getHandle()
	{
		return env;
	}

	/**
	 * Gets an EnvironmentImpl from inside the Environment.
	 *
	 * @param impl the implementation class
	 * @return the implementation
	 */
	public EnvironmentImpl getModule(Class<? extends EnvironmentImpl> impl)
	{
		return env.getEnv(impl);
	}

	/**
	 * Adds an EnvironmentImpl to the Environment.
	 *
	 * @param impls the implementations to add
	 */
	public MethodScriptEnvironment addModule(EnvironmentImpl... impls)
	{
		env.cloneAndAdd(impls);
		return this;
	}

	/**
	 * Clones the Environment (not the MethodScriptEnvironment)
	 * @return a clone of the environment
	 */
	public Environment cloneEnvironment()
	{
		try
		{
			return env.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException("Unable to copy this Environment");
		}
	}

	private CommandHelperEnvironment CommandHelperEnvironment()
	{
		return env.getEnv(CommandHelperEnvironment.class);
	}

	private GlobalEnv GlobalEnvironment()
	{
		return env.getEnv(GlobalEnv.class);
	}

	public MethodScriptEnvironment clearVariables()
	{
		GlobalEnvironment().SetVarList(new IVariableList());
		return this;
	}

	private String fixVariableName(String name)
	{
		if(name.charAt(0) != '@')
			return '@' + name;
		return name;
	}

	public MethodScriptEnvironment setVariable(String name, Construct con)
	{
		IVariableList vars = GlobalEnvironment().GetVarList();
		vars.set(new IVariable(CClassType.AUTO, fixVariableName(name), con, t));
		return this;
	}

	public Construct getVariable(String name)
	{
		name = fixVariableName(name);
		IVariableList vars = GlobalEnvironment().GetVarList();
		if(vars.has(name))
			return vars.get(name, t);
		else
			return null;
	}

	/**
	 * Adds variables into the internal environment's variable list.
	 *
	 * @param map a map of variable names and constructs
	 */
	public MethodScriptEnvironment setVariables(Map<String, Construct> map)
	{
		IVariableList vars = GlobalEnvironment().GetVarList();
		for(String name: map.keySet())
		{
			vars.set(new IVariable(CClassType.AUTO, fixVariableName(name), map.get(name), t));
		}
		return this;
	}

	public MethodScriptEnvironment setVariables(IVariableList vars)
	{
		GlobalEnvironment().SetVarList(vars);
		return this;
	}

	public Map<String, Construct> getVariables()
	{
		IVariableList vars = GlobalEnvironment().GetVarList();
		Map<String, Construct> map = new HashMap<>();
		for(String name: vars.keySet())
		{
			map.put(name, vars.get(name, t));
		}
		return map;
	}

	/**
	 * Singular method of including procedures in this script. All procedure names must start with an underscore.
	 *
	 * @param name the name of the procedure (must be prefixed with '_')
	 * @param proc the procedure
	 */
	public MethodScriptEnvironment include(String name, Procedure proc)
	{
		Map<String, Procedure> procs = GlobalEnvironment().GetProcs();
		procs.put(name, proc);
		return this;
	}

	public Procedure getProcedure(String name)
	{
		return getProcedures().get(name);
	}

	/**
	 * Include procedures into this script.
	 *
	 * @param newProcs a map of procedures to include in this script.
	 */
	public MethodScriptEnvironment include(Map<String, Procedure> newProcs)
	{
		Map<String, Procedure> procs = GlobalEnvironment().GetProcs();
		procs.putAll(newProcs);
		return this;
	}

	public Map<String, Procedure> getProcedures()
	{
		return GlobalEnvironment().GetProcs();
	}

	public MethodScriptEnvironment setExecutor(MCCommandSender executor)
	{
		CommandHelperEnvironment().SetCommandSender(executor);
		return this;
	}

}

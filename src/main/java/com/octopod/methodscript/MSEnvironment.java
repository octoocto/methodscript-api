package com.octopod.methodscript;

import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.commandhelper.CommandHelperFileLocations;
import com.laytonsmith.commandhelper.CommandHelperPlugin;
import com.laytonsmith.core.Profiles;
import com.laytonsmith.core.constructs.*;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.environments.GlobalEnv;
import com.laytonsmith.core.taskmanager.TaskManager;

import java.io.IOException;

/**
 * @author Octopod - octopodsquad@gmail.com
 */

/**
 * A wrapper for the Environment object.
 * A Target is paired with the Environment for getting variables/procedures and such.
 * If no Target is given, Target.UNKNOWN will be used.
 */
public class MSEnvironment
{
	private static Environment defaultEnvironment = null;

	/**
	 * Gets CommandHelper's default environment.
	 * @return Environment
	 */
	private static Environment newEnvironment()
	{
		if(defaultEnvironment != null) {
			return cloneEnvironment(defaultEnvironment);
		}

		CommandHelperPlugin plugin = CommandHelperPlugin.self;
		GlobalEnv gEnv;

		try
		{
			gEnv = new GlobalEnv(
					plugin.executionQueue,
					plugin.profiler,
					plugin.persistenceNetwork,
					CommandHelperFileLocations.getDefault().getConfigDirectory(),
					new Profiles(CommandHelperFileLocations.getDefault().getProfilesFile()),
					new TaskManager()
			);
		}
		catch (Profiles.InvalidProfileException | IOException e)
		{
			return cloneEnvironment(defaultEnvironment);
		}

		gEnv.SetDynamicScriptingMode(true);
		CommandHelperEnvironment cEnv = new CommandHelperEnvironment();

		return defaultEnvironment = Environment.createEnvironment(gEnv, cEnv);
	}

	/**
	 * Attempts to clone the Environment, and if it fails, returns null.
	 * @param env
	 * @return
	 */
	private static Environment cloneEnvironment(Environment env)
	{
		try {
			return env.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	private final Environment env;
	private final Target t;

	/**
	 * The (wrapper) list of variables in this environment.
	 */
	public final MSVariableList variables;

	/**
	 * The (wrapper) list of procedures in this environment.
	 */
	public final MSProcedureList procedures;

	public MSEnvironment()
	{
		this(newEnvironment(), Target.UNKNOWN, null, null);
	}

	public MSEnvironment(MSVariableList variables)
	{
		this(newEnvironment(), Target.UNKNOWN, variables, null);
	}

	public MSEnvironment(Target t)
	{
		this(newEnvironment(), t, null, null);
	}

	/**
	 * Creates a MethodScriptEnvironment (wrapper) from an Environment.
	 * Target will be UNKNOWN.
	 *
	 * @param env the environment
	 */
	public MSEnvironment(Environment env)
	{
		this(env, Target.UNKNOWN, null, null);
	}

	/**
	 * Creates a MethodScriptEnvironment (wrapper) from an Environment.
	 *
	 * @param env the environment
	 * @param t the target to use
	 */
	public MSEnvironment(Environment env, Target t)
	{
		this(env, t, null, null);
	}

	public MSEnvironment(Environment env, Target t, MSVariableList variables, MSProcedureList procedures)
	{
		this.env = env;
		this.t = t;
		if(variables != null)
		{
			GlobalEnvironment().SetVarList(variables.handle);
		}
		this.variables = new MSVariableList(GlobalEnvironment().GetVarList(), t);
		if(procedures != null)
		{
			GlobalEnvironment().SetProcs(procedures.handle);
		}
		this.procedures = new MSProcedureList(GlobalEnvironment().GetProcs());
	}

	/**
	 * Copy constructor. Clones the given Environment.
	 *
	 * @param env the original environment
	 */
	public MSEnvironment(MSEnvironment env)
	{
		try
		{
			this.env = env.env.clone();
			this.t = env.t;
			this.variables = new MSVariableList(GlobalEnvironment().GetVarList(), t);
			this.procedures = new MSProcedureList(GlobalEnvironment().GetProcs());
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Unable to copy this MethodScriptEnvironment");
		}
	}

	public MSEnvironment setExecutor(MCCommandSender executor)
	{
		CommandHelperEnvironment().SetCommandSender(executor);
		return this;
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


//	/**
//	 * Gets an EnvironmentImpl from inside the Environment.
//	 *
//	 * @param impl the implementation class
//	 * @return the implementation
//	 */
//	private <T extends EnvironmentImpl> T getModule(Class<T> impl)
//	{
//		return environment.getEnv(impl);
//	}
//
//	/**
//	 * Adds an EnvironmentImpl to the Environment.
//	 *
//	 * @param impls the implementations to add
//	 */
//	private MScriptEnvironment addModule(EnvironmentImpl... impls)
//	{
//		environment.cloneAndAdd(impls);
//		return this;
//	}

	/**
	 * Gets the CommandHelperEnvironment implementation.
	 * The CommandHelperEnvironment contains information related
	 * to the Minecraft plugin, such as:
	 *  - which player (or console) executed the script
	 *
	 * @return the CommandHelperEnvironment implementation
	 */
	private CommandHelperEnvironment CommandHelperEnvironment()
	{
		return env.getEnv(CommandHelperEnvironment.class);
	}

	/**
	 * Gets the GlobalEnvironment implementation.
	 * The GlobalEnvironment contains information related
	 * to MethodScript, such as:
	 *  - variables
	 *  - procedures
	 *
	 * @return the GlobalEnvironment implementation
	 */
	private GlobalEnv GlobalEnvironment()
	{
		return env.getEnv(GlobalEnv.class);
	}
}

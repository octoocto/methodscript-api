package com.octopod.msapi;

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
public class MScriptEnvironment
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
	private final MScriptVariableList varList;

	/**
	 * The (wrapper) list of procedures in this environment.
	 */
	private final MScriptProcedureList procList;

	public MScriptEnvironment()
	{
		this(newEnvironment(), Target.UNKNOWN);
	}

	public MScriptEnvironment(Target t)
	{
		this(newEnvironment(), t);
	}

	/**
	 * Creates a MethodScriptEnvironment (wrapper) from an Environment.
	 * Target will be UNKNOWN.
	 *
	 * @param env the environment
	 */
	public MScriptEnvironment(Environment env)
	{
		this(env, Target.UNKNOWN);
	}

	/**
	 * Creates a MethodScriptEnvironment (wrapper) from an Environment.
	 *
	 * @param env the environment
	 * @param t the target to use
	 */
	public MScriptEnvironment(Environment env, Target t)
	{
		this.env = env;
		this.t = t;
		this.varList = new MScriptVariableList(GlobalEnvironment().GetVarList(), t);
		this.procList = new MScriptProcedureList(GlobalEnvironment().GetProcs());
	}

	/**
	 * Copy constructor. Clones the given Environment.
	 *
	 * @param env the original environment
	 */
	public MScriptEnvironment(MScriptEnvironment env)
	{
		try
		{
			this.env = env.env.clone();
			this.t = env.t;
			this.varList = new MScriptVariableList(GlobalEnvironment().GetVarList(), t);
			this.procList = new MScriptProcedureList(GlobalEnvironment().GetProcs());
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Unable to copy this MethodScriptEnvironment");
		}
	}

	public MScriptEnvironment setExecutor(MCCommandSender executor)
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
//		return env.getEnv(impl);
//	}
//
//	/**
//	 * Adds an EnvironmentImpl to the Environment.
//	 *
//	 * @param impls the implementations to add
//	 */
//	private MScriptEnvironment addModule(EnvironmentImpl... impls)
//	{
//		env.cloneAndAdd(impls);
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

	/**
	 * Gets the variables in this environment.
	 *
	 * @return
	 */
	public MScriptVariableList variables()
	{
		return varList;
	}

	/**
	 * Gets the procedures in this environment.
	 *
	 * @return
	 */
	public MScriptProcedureList procedures()
	{
		return procList;
	}

}

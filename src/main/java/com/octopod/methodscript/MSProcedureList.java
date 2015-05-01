package com.octopod.methodscript;

import com.laytonsmith.core.Procedure;

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper class for CommandHelper's procedure map.
 */
public class MSProcedureList
{
	/**
	 * The handle for this class.
	 */
	public final Map<String, Procedure> handle;

	public MSProcedureList(Map<String, Procedure> handle)
	{
		this.handle = handle;
	}

	/**
	 * Singular method of including procedures in this script. All procedure names must start with an underscore.
	 *
	 * @param name the name of the procedure (must be prefixed with '_')
	 * @param proc the procedure
	 */
	public void set(String name, Procedure proc)
	{
		handle.put(name, proc);
	}

	/**
	 * Include procedures into this script.
	 *
	 * @param procs a map of procedures to include in this script.
	 */
	public void set(Map<String, Procedure> procs)
	{
		handle.putAll(procs);
	}

	public Procedure get(String name)
	{
		return handle.get(name);
	}

	public Map<String, Procedure> toMap()
	{
		return new HashMap<>(handle);
	}
}

package com.octopod.methodscript;

import com.laytonsmith.core.constructs.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for the IVariableList object.
 */
public class MScriptVariableList
{
	/**
	 * The handle of this class.
	 */
	public final IVariableList handle;

	private final Target t;

	public MScriptVariableList(IVariableList handle, Target t)
	{
		this.handle = handle;
		this.t = t;
	}

	public MScriptVariableList()
	{
		this.handle = new IVariableList();
		this.t = Target.UNKNOWN;
	}

	/**
	 * Fixes <code>name</code> if it doesn't match the correct format for a MethodScript variable.
	 * For example, if you ran <code>fixVariableName("var")</code>, it would return "@var".
	 * If you ran <code>fixVariableName("@var")</code>, it would do nothing, and return "@var".
	 *
	 * @param str the variable name to fix
	 * @return the fixed variable name
	 */
	private static String fixVariableName(String str)
	{
		if(str.charAt(0) != '@')
			return '@' + str;
		return str;
	}

	public void merge(MScriptVariableList other)
	{
		other.toList().stream().forEach(handle::set);
	}

	/**
	 * Clears all cached variables in this environment.
	 *
	 */
	public void clear()
	{
		handle.keySet().stream().forEach(handle::remove);
	}

	/**
	 * Clears all cached variables in this environment.
	 *
	 * @param name the name of the variable
	 * @param c the construct to set the variable to
	 */
	public void set(String name, Construct c)
	{
		handle.set(new IVariable(CClassType.AUTO, fixVariableName(name), c, t));
	}

	/**
	 * Gets the value of a variable name. If no variable exists, CNull.NULL is returned.
	 *
	 * @param name the name of the variable
	 * @return the value of the variable
	 */
	public Construct get(String name)
	{
		name = fixVariableName(name);

		if(handle.has(name))
			return handle.get(name, t);
		else
			return CNull.NULL;
	}

	/**
	 * Adds variables into the variable list from a map of string keys and construct values.
	 *
	 * @param map a map of variable names and constructs
	 */
	public void set(Map<String, Construct> map)
	{
		map.keySet().stream().forEach(
				(name) -> handle.set(new IVariable(CClassType.AUTO, fixVariableName(name), map.get(name), t))
		);
	}

	/**
	 * Converts this variable list into a map of string keys and construct values.
	 *
	 * @return the variable list as a map
	 */
	public Map<String, Construct> toMap()
	{
		Map<String, Construct> map = new HashMap<>();
		handle.keySet().stream().forEach(
				(name) -> map.put(name, handle.get(name, t).ival())
		);
		return map;
	}

	public List<IVariable> toList()
	{
		List<IVariable> list = new ArrayList<>();
		handle.keySet().stream().forEach(
				(name) -> list.add(handle.get(name, t))
		);
		return list;
	}

}

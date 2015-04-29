package com.octopod.msapi;

import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.core.MethodScriptCompiler;
import com.laytonsmith.core.ParseTree;
import com.laytonsmith.core.Procedure;
import com.laytonsmith.core.constructs.*;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.environments.GlobalEnv;
import com.laytonsmith.core.exceptions.ConfigCompileException;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.exceptions.ConfigCompileGroupException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Octopod
 *         Created on 5/24/14
 */
public class CommandHelper
{
	public static ParseTree parse(String script) throws ConfigCompileException, ConfigCompileGroupException
	{
		return MethodScriptCompiler.compile(MethodScriptCompiler.lex(script, Target.UNKNOWN.file(), true));
	}

	/**
	 * Compiles MethodScript from a string.
	 *
	 * @param script the script to compile
	 * @return the compiled script
	 *
	 * @throws ConfigCompileException
	 * @throws ConfigCompileGroupException
	 */
	public static MethodScript compile(String script) throws ConfigCompileException, ConfigCompileGroupException
	{
		return new MethodScript(script);
	}

	/**
	 * Runs CompiledMethodScript, and returns the resultant Construct. (as the console by default)
	 * May throw ConfigCompileException during the compiling stage.
	 *
	 * @param script The CompiledMethodScript to run.
	 * @return Construct
	 *
	 * @throws ConfigCompileException
	 */
	public static Construct eval(String script) throws ConfigCompileException, ConfigCompileGroupException
	{
		return eval(script, null, null);
	}

	public static Procedure proc(String name, List<IVariable> vars, String script) throws ConfigCompileException, ConfigCompileGroupException
	{
		return new Procedure(name, CClassType.AUTO, vars, parse(script), Target.UNKNOWN);
	}

	/**
	 * Runs CompiledMethodScript, and returns the resultant Construct.
	 * May throw ConfigCompileException during the compiling stage.
	 * @param script The CompiledMethodScript to run.
	 * @param executor Who this script is going to be executed by.
	 * @return Construct
	 * @throws ConfigCompileException
	 * @throws ConfigRuntimeException
	 */
	public static Construct eval(String script, MCCommandSender executor) throws ConfigCompileException, ConfigCompileGroupException
	{
		return eval(script, null, executor);
	}

	public static Construct eval(String script, MScriptVariableList vars, MCCommandSender executor) throws ConfigCompileException, ConfigCompileGroupException
	{
		MethodScript ms = new MethodScript(script);

		if(vars != null) {
			ms.environment().variables().merge(vars);
		}
		if(executor != null) {
			ms.environment().setExecutor(executor);
		}

		return ms.execute();
	}

	/**
	 * Extracts the ParseTree variable from a Procedure
	 * @param procedure
	 * @return
	 */
	public static ParseTree getProcedureTree(Procedure procedure)
	{
		try
		{
			Field field = procedure.getClass().getDeclaredField("tree");
			field.setAccessible(true);
			return (ParseTree)field.get(procedure);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Extracts the original variables from a Procedure
	 * @param procedure
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Construct> getProcedureVars(Procedure procedure) {
		try {
			Field field = procedure.getClass().getDeclaredField("originals");
			field.setAccessible(true);
			return (Map<String, Construct>)field.get(procedure);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	private static <T> T newInstance(Class<T> type)
	{
		try {
			return type.getConstructor().newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts a Java object into a Construct
	 *
	 * @param object the Java object
	 * @return the CommandHelper construct
	 */
	public static Construct toConstruct(Object object)
	{
		Target t = Target.UNKNOWN;

		if(object == null) return CNull.NULL;

		if(object instanceof Byte) return toConstruct(((Byte) object).longValue());
		if(object instanceof Integer) return toConstruct(((Integer) object).longValue());
		if(object instanceof Short) return toConstruct(((Short) object).longValue());
		if(object instanceof Long) return new CInt((long)object, t);

		if(object instanceof Float) return toConstruct(((Float) object).doubleValue());
		if(object instanceof Double) return new CDouble((double)object, t);

		if(object instanceof Boolean) return CBoolean.get((boolean)object);

		if(object instanceof Character) return new CString((char)object, t);
		if(object instanceof String) return new CString((String)object, t);

		if(object instanceof int[])
		{
			CArray array = new CArray(t);
			for(int o: (int[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof byte[])
		{
			CArray array = new CArray(t);
			for(byte o: (byte[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof short[])
		{
			CArray array = new CArray(t);
			for(short o: (short[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof long[])
		{
			CArray array = new CArray(t);
			for(long o: (long[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof float[])
		{
			CArray array = new CArray(t);
			for(float o: (float[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof double[])
		{
			CArray array = new CArray(t);
			for(double o: (double[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof boolean[])
		{
			CArray array = new CArray(t);
			for(boolean o: (boolean[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof char[])
		{
			CArray array = new CArray(t);
			for(char o: (char[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof Object[])
		{
			CArray array = new CArray(t);
			for(Object o: (Object[])object) array.push(toConstruct(o));
			return array;
		}

		if(object instanceof Collection)
		{
			return toConstruct(((Collection) object).toArray());
		}

		if(object instanceof Map)
		{
			Map map = (Map)object;
			CArray array = new CArray(t);
			for(Object key: map.keySet()) array.set(key.toString(), toConstruct(map.get(key)), t);
			return array;
		}

		CArray array = new CArray(t);

		for(Field field: object.getClass().getDeclaredFields())
		{
			boolean wasUnlocked = field.isAccessible();
			field.setAccessible(true);
			try {
				array.set(field.getName(), toConstruct(field.get(object)), t);
			} catch (IllegalAccessException e) {}
			field.setAccessible(wasUnlocked);
		}
		return array;
	}

	/**
	 * Converts a Construct into a Java object.
	 *
	 * @param construct the commandhelper construct
	 * @param type the type to convert to
	 *
	 * @return the object matching type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromConstruct(Construct construct, Class<T> type)
	{
		Object object = null;

		if(construct instanceof CInt) object = ((CInt)construct).getInt();
		if(construct instanceof CDouble) object = ((CDouble)construct).getDouble();
		if(construct instanceof CBoolean) object = ((CBoolean)construct).getBoolean();
		if(construct instanceof CString) object = construct.val();

		if(object instanceof Boolean)
		{
			Boolean x = (Boolean)object;
			if(type == Boolean.class || type == boolean.class) return (T)x;
		}
		if(object instanceof Long)
		{
			Long x = (Long)object;
			if(type == Integer.class || type == int.class) 		return (T)(Integer)x.intValue();
			if(type == Short.class || type == short.class) 		return (T)(Short)x.shortValue();
			if(type == Byte.class || type == byte.class) 		return (T)(Byte)x.byteValue();
			if(type == Long.class || type == long.class)		return (T)x;
		}
		if(object instanceof Double)
		{
			Double x = (Double)object;
			if(type == Float.class || type == float.class)		return (T)(Float)x.floatValue();
			if(type == Double.class || type == double.class)	return (T)x;
		}
		if(object instanceof String)
		{
			String x = (String)object;
			if(type == Character.class || type == char.class)	return (T)(Character)x.charAt(0);
			if(type == String.class)	return (T)x;
		}

		if(construct instanceof CArray)
		{
			//Deserialize class fields???
			CArray array = (CArray)construct;
			Target t = Target.UNKNOWN;
			T obj = newInstance(type);
			if(obj == null) {return null;}

			for(Construct key: array.keySet())
			{
				Field field;
				boolean wasUnlocked;
				try {
					field = type.getDeclaredField(key.val());
					wasUnlocked = field.isAccessible();

					field.setAccessible(true);
					field.set(obj, fromConstruct(array.get(key, t), field.getType()));
					field.setAccessible(wasUnlocked);
				} catch (NoSuchFieldException | IllegalAccessException e) {}
			}
			return obj;
		}

		return nonNullObject(type);


	}

	@SuppressWarnings("unchecked")
	public static <T> T nonNullObject(Class<T> type)
	{
		if(type == Integer.class || type == int.class) 		return (T)(Integer)(int)-1;
		if(type == Short.class || type == short.class) 		return (T)(Short)(short)-1;
		if(type == Byte.class || type == byte.class) 		return (T)(Byte)(byte)-1;
		if(type == Long.class || type == long.class)		return (T)(Long)(long)-1;
		if(type == Boolean.class || type == boolean.class) 	return (T)(Boolean)false;
		if(type == Float.class || type == float.class)		return (T)(Float)(float)-1;
		if(type == Double.class || type == double.class)	return (T)(Double)(double)-1;
		if(type == Character.class || type == char.class)	return (T)(Character)' ';
		if(type == String.class)							return (T)"";
		return null;
	}
}

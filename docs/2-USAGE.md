2 - MethodScript UAPI Usage
===

This page will show you how to use this UAPI.
Please note that you might have to use a combination of this API and the API found in [CommandHelper][1].

Notable folders in CommandHelper:

 - [Minecraft Server API][2]

    CommandHelper provides its own abstraction of the Minecraft Server API in order to be compatible with multiple Minecraft Server API implementations.

 - [Constructs][3]

    All types in MethodScript derive from the `Construct` class in Java. Take note of this as you convert Constructs to Java objects and vice versa.

2.1 - "Hello World"
---
This is a simple example of how to use the UAPI.

```java
import com.octopod.methodscript.CommandHelper;

// Compiles the string, creates a new environment, then executes.
CommandHelper.eval("print('Hello World!')");
```

There are two parts to executing MethodScript: compiled `MethodScript` and an `MSEnvironment`. As you may have noticed, `CommandHelper.eval()` both compiles the provided string into `MethodScript` and creates a new `MSEnvironment` in order to execute it.

The following code achieves the same result:
```java
import com.octopod.methodscript.MethodScript;
import com.octopod.methodscript.MSEnvironment;

// Compiles the string
MethodScript script = new MethodScript("print('Hello World!')");
// Creates a new environment
MSEnvironment env = new MSEnvironment();
// Executes the script using the environment
script.execute(env);
```

2.2 - Creating and Modifying the Environment
---
You can also create your own `MSEnvironment` in order to control environmental information that is seen by the script.

```java
import com.octopod.methodscript.MSEnvironment;
import com.octopod.methodscript.CommandHelper;

MSEnvironment env = new MSEnvironment();
```

In the context of Minecraft, you can set who (console or player) executes a script via `MSEnvironment`.
Using Bukkit as an example:

```java
// 'player' is a Bukkit Player
// 'BukkitMCPlayer' is provided by the CommandHelper plugin
env.setExecutor(new BukkitMCPlayer(player));
```

You can then execute a script using the environment:

```java
CommandHelper.eval("print(player())", env);
```

which will print the player's name.

Note that executing a script using an environment may modify that environment, so if that is something you want to avoid, try running a clone of the environment instead:

```java
// 'new MSEnvironment(MSEnvironment)' is a copy constructor
CommandHelper.eval("print(player())", new MSEnvironment(env));
```

2.3 - Injecting and Extracting Variables
---
A benefit to saving an `MSEnvironment` is that you can use it to listen for changes that scripts may make to it. For example, a script can set a variable, which will modify the environment allowing you to take that variable out and translate it into Java. On the other hand, you can modify the environment yourself allowing you to insert variables into an environment before executing a script.

Some knowledge on how [Constructs](3) work may be required beforehand.

Inserting variables can be done like so:

```java
import com.octopod.methodscript.MSEnvironment;
import com.octopod.methodscript.CommandHelper;

MSEnvironment env = new MSEnvironment();

env.variables.set("@i", new CInt(5, Target.UNKNOWN));

// prints "5"
CommandHelper.eval("print(@i)", env);

env.variables.set("@i", new CInt(2, Target.UNKNOWN));

// prints "2"
CommandHelper.eval("print(@i)", env);
```

Extracting variables can be done like so:
```java
import com.octopod.methodscript.MSEnvironment;
import com.octopod.methodscript.CommandHelper;

MSEnvironment env = new MSEnvironment();

// sets @int to 5
CommandHelper.eval("@i = 5;", env);

// this is a CInt
int i = ((CInt)env.variables.get("@i")).getInt();
```

<!--- CommandHelper GitHub --->
[1]: https://github.com/sk89q/CommandHelper

<!--- laytonsmith/abstraction --->
[2]: https://github.com/theoctopod/commandhelper/tree/master/src/main/java/com/laytonsmith/abstraction

<!--- laytonsmith/constructs --->
[3]: https://github.com/theoctopod/commandhelper/tree/master/src/main/java/com/laytonsmith/constructs

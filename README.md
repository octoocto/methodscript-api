MethodScript Unofficial API [![License][1]](LICENSE.txt) ![Stars][2]
======
```java
CommandHelper.eval("broadcast('Hello World!')");
```
##### MethodScript implementation made incredibly easy.

MethodScript is a scripting language created by the Minecraft plugin CommandHelper.

This project aims to provide an easy way to interface with CommandHelper's MethodScript compiler and executor.

_Credits to_: LadyCailin and all of CommandHelper's contributors.

- [CommandHelper GitHub](https://github.com/sk89q/CommandHelper)
- [CommandHelper Minecraft Server Abstraction](https://github.com/theoctopod/commandhelper/tree/master/src/main/java/com/laytonsmith/abstraction)

Obviously, CommandHelper and the server API (ex. Bukkit) that loaded it must be loaded.

MethodScript evaluation/compiling
------
Execute MethodScript (as console):
```java
import com.octopod.commandhelper.CommandHelper;

CommandHelper.eval("broadcast('Hello World!')");
```

Execute MethodScript (as player):
```java
import com.octopod.commandhelper.CommandHelper;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;

BukkitMCPlayer sender = new BukkitMCPlayer(bukkitPlayer);
CommandHelper.eval("msg('hello')", sender);
```

Compiled MethodScript as a Java Object:
```java
String s = "broadcast('Hello World!')";
//method A
MethodScript A = CommandHelper.compile(s);
//method B
MethodScript B = new MethodScript(s);
```

Execute MethodScript from its Java Object:
```java
import com.octopod.commandhelper.CommandHelper;
import com.octopod.commandhelper.MethodScript;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;

MethodScript script = CommandHelper.compile("broadcast('hello')");

A.execute();
B.execute(new BukkitMCPlayer(bukkitPlayer));
```

MethodScript environment access
------
Convert MethodScript values into Java values:
```java
String s =
    "@int = 1;" +
    "@boolean = true;" +
    "@string = 'string';"

MethodScript script = new MethodScript(script);
script.execute();

MethodScriptEnvironment environment = script.getEnvironment();

Construct cint = script.getVariable("@int");
Construct cboolean = script.getVariable("@boolean");
Construct cstring = script.getVariable("@string")

int i = ((CInt)cint).getInt();
boolean bool = ((CBoolean)cboolean).getBoolean();
String str = ((CString)cstring).val();
```

Set variable in MethodScript A (all variable names start with '@'):
```java
MethodScript mscript = new MethodScript("broadcast(@a)");

mscript.environment.variables.set("@a", new CInt(5, Target.UNKNOWN));
```

Set procedure in a MethodScript (all procedure names start with '\_'):
```java
MethodScript A = new MethodScript("_something('hello')");
Procedure proc = CommandHelper.proc("_something", new ArrayList<IVariable>(), "broadcast('hello')");
A.getEnvironment().include(proc);
```

Setting a variable before executing CompiledMethodScript A:
```java
MethodScript A = new MethodScript("broadcast(@hello)");
A.getEnvironment().setVariable("@hello", new CString("hello", A.getTarget()));
```

Including procedures from CompiledMethodScript A in CompiledMethodScript B:
```java
MethodScript A = new MethodScript("proc _hello() {broadcast('hello')}");
MethodScript B = new MethodScript("_hello()");

//The script needs to be executed at least once to get the procedures
A.execute();
B.include(A.getEnvironment().getProcedures());
```

<!--- GitHub License Badge --->
[1]: https://img.shields.io/github/license/hyperfresh/methodscript-api.svg

<!--- GitHub Star Count Badge --->
[2]: https://img.shields.io/github/stars/hyperfresh/methodscript-api.svg

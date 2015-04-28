MethodScript Unofficial API [![License](https://img.shields.io/github/license/hyperfresh/methodscript-api.svg)](LICENSE.txt) [![Stars](https://img.shields.io/github/stars/hyperfresh/methodscript-api.svg)]()
======
```java
CommandHelper.eval("broadcast('Hello World!')");
```
#### MethodScript implementation made incredibly easy.

MethodScript is a scripting language created by the Minecraft plugin CommandHelper.

This project aims to provide an easy way to interface with CommandHelper's MethodScript compiler and executor.

_Credits to_: LadyCailin and all of CommandHelper's contributors.

- [CommandHelper GitHub](https://github.com/sk89q/CommandHelper)

Obviously, CommandHelper and the server API (ex. Bukkit) that loaded it must be loaded.

MethodScript evaluation/compiling
------
Execute MethodScript (as console):
```java
import com.octopod.msapi.CommandHelper;

CommandHelper.eval("broadcast('Hello World!')");
```

Execute MethodScript (as player):
```java
import com.octopod.msapi.CommandHelper;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;

BukkitMCPlayer sender = new BukkitMCPlayer(bukkitPlayer);
CommandHelper.eval("msg('hello')", sender);
```

Compiled MethodScript as a Java Object:
```java
String script = "broadcast('Hello World!')";
//method A
MethodScript A = CommandHelper.compile(script);
//method B
MethodScript B = new MethodScript(script);
```

Execute MethodScript from its Java Object:
```java
import com.octopod.msapi.CommandHelper;
import com.octopod.msapi.MethodScript;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;

MethodScript script = CommandHelper.compile("broadcast('hello')");

A.execute();
B.execute(new BukkitMCPlayer(bukkitPlayer));
```

MethodScript environment access
------
Set variable in MethodScript A (all variable names start with '@'):
```java
MethodScript A = new MethodScript("broadcast(@a)");
A.getEnvironment().setVariable("@a", new CInt(5, Target.UNKNOWN));
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

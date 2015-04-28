CommandHelper
======
Classes relating to the Minecraft plugin CommandHelper (and its MethodScript language).
Credits to LadyCailin and the rest of CommandHelper's contributors.
[CommandHelper Builds](http://builds.enginehub.org/job/commandhelper)
[CommandHelper GitHub](https://github.com/sk89q/CommandHelper)

Obviously, CommandHelper and whatever API (like Bukkit) that loaded it must be loaded.

MethodScript evaluation/compiling
------
Evaluate code (statically as console):
```java
CommandHelper.eval("broadcast('hello')");
```

Evaluate code (statically as player):
```java
CommandHelper.eval("msg('hello')", new BukkitMCPlayer(player));
```

Execute code (from MethodScript):
```java
MethodScript A = CommandHelper.compile("broadcast('hello')");
A.execute();
//with player
MethodScript B = CommandHelper.compile("broadcast('goodbye')");
B.execute(new BukkitMCPlayer(player));
```

Compile code:
```java
//with static CommandHelper.compile() method:
MethodScript A = CommandHelper.compile("broadcast('hello')");
//or using MethodScript's constructor:
MethodScript B = new MethodScript("broadcast('goodbye')");
```

MethodScript environment access
------
Set variable in MethodScript A (all variable names start with '@'):
```java
MethodScript A = new MethodScript("broadcast(@a)");
A.getEnvironment().setVariable("@a", new CInt(5, Target.UNKNOWN));
```

Set procedure in a MethodScript (all procedure names start with '_'):
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

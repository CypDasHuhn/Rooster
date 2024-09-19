# Arguments

An abstraction of the usual way of creating commands,
similar to Brigardier. Your Command is a tree split into multiple Arguments,
which represent the control flow. You can either manually create an Argument,
or use one of the many custom constructors specially designed for one task,
like a list argument, number argument or any other component you may want.
Create your own constructors and use them later on,
or recommend them to the Rooster Repos! The API is very flexible.

## Usage

To Register a new command, simply create an annotated field anywhere.

```kotlin
@RoosterCommand
val yourCommand = RootArgument(
        /* further information here */
    )
```

Rooster will pick it up for you and register it. <br>
Remember to include your new command in the plugin.yml,
else Paper won't be able to detect it.

## Argument Types

The ArgumentAPI provides four different types of arguments.
Root-, Central-, Modifier- and regular Argument.
These classes can mutate to each other, and you can also create an
UnsafeArgument which doesn't have constructor limitations.

### Regular Argument

This is the standard argument, the other types are mirrors of this type
with modifications. Which fields it specifically holds, can be viewed
at [Core Fields](#core-fields) / [Other Fields](#other-fields).

### Central Argument

The Central Argument is identical to the Regular Argument, except that the [Error Missing](#error-missing)
Field is forced to be filled by the constructor. You can use a CentralizedArgumentList
in the [Following Arguments](#following-arguments) of another Argument, and then fill the List
with a Central Argument. Generally, if your child-parent relationship is that an Argument is the Main Child, use a
CentralArgument
as the logic for handling Missing Arguments will be encapsulated at the correct place.

### Root Argument

The Root Argument is the one which you use at the top to create
a new command. It gets two new fields, the [Label](#label) which is
the name of the command, for example with `/tp @s 200 70 200` 'tp' is the label here.
And the [Starting Unit](#starting-unit) field, which is invoked at the complete beginning
of the command parsing, and will determine whether the command should be executed. Take for example that you dont allow
CommandSenders which aren't
players, or based on the sender you load and cache some specific data.

### Modifier Argument

The Modifier Argument is the most different from the other Arguments.
It gets treated by the Parser differently then the others, with other
Argument instances, when you enter an argument, the parser dives into the sub-tree
of that argument. With the Modifier Argument it doesn't change your position in the
Argument Tree, but rather if present it provides extra information. Take flags as an example,
like `/customCommand customArgument` could be also used with `/customCommand -flag customArgument`,
the tree isn't changed, but you can access the values later on (
Invocation/Completion).

## Core-Fields

### Key

The Key of a command. Every single Argument needs a Key. <br>
You can access values of an Argument by using the Key.

### Tab Completions

The Key of a command. Every single Argument needs a Key.

### Following Arguments

### Invoke

### Is Valid

## Other Fields

### Is Valid Completer

### Is Argument

### Error Missing

### Error Missing Child Arg

### Argument Handler

### Is Modifier

### Starting Unit

Root Only

### Label

Root Only
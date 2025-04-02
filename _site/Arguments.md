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

## Core Fields

### Key
`String`<br>

The unique identifier for each command argument. This key allows access to specific values associated with the argument during command execution. Every argument must have a `key`.

### Tab Completions
`ArgumentInfo => List<String>` ([ArgumentInfo](#argumentinfo)) <br> 

A lambda function that provides the tab completions based on the current context. Every Argument needs a Tab Completion.
### Following Arguments
`ArgumentList`([argumentList](#argumentlist))

A list of arguments that can follow the current argument in the command structure. 
This field enables the creation of a hierarchical command structure where each argument can lead to more 
specific sub-arguments.
### Invoke
`InvokeInfo => Unit` ([InvokeInfo](#invokeinfo))

A lambda function that defines the action to be executed when the command is invoked with valid arguments. 
This allows for customizable behavior when a command is successfully parsed. 
An Invoke function is always invoked with parsed arguments.

### `Is Valid`

A validation function that checks whether the current argument is valid in the given context.
This can be used to enforce rules about what arguments can be used together or under certain conditions.

## Other Fields

### `Is Valid Completer`

A validation function specifically for tab completion contexts. 
This checks if the argument is valid when the user is requesting completions, 
allowing for dynamic suggestions based on the argument state.

### `Is Argument`

A boolean flag indicating whether the current instance is a valid argument in the command structure. 
This can be useful for differentiating between argument types during parsing. Use this before checking validness and potentially sending an error.

### `Error Missing`

A lambda function that defines the action to be taken when a required argument is missing. This allows for custom error handling to inform the user about the specific arguments that were not provided.

### `Error Missing Child Arg`

A lambda function that handles the situation where a required child argument of the current argument is missing. This can provide more granular error messages for nested arguments.

### `Argument Handler`

A lambda function that processes the argument value when it is validated and invoked. This function defines how to handle the data associated with the argument, enabling custom processing logic. Often used together with [Is Valid](#is-valid) to handle the processed result.

### `Starting Unit` (Root Only)

A lambda function that is called at the beginning of command parsing to determine whether the command should proceed. This is typically used to enforce preconditions based on the command sender or other contextual factors.

### `Label` (Root Only)

The name of the command as it appears in user input. For example, in the command `/tp @s 200 70 200`, the label is `tp`. This field is essential for matching user input to the correct command handler.

## Data Classes
### ArgumentInfo

### InvokeInfo

### ArgumentList


# Arguments

An abstraction of the usual way of creating commands,
similar to Brigardier. Your Command is a tree split into multiple Arguments,
which represent the control flow. You can either manually create an Argument,
or use one of the many custom constructors specially designed for one task,
like a list argument, number argument or any other component you may want.
Create your own constructors and use them later on,
or recommend them to the Rooster Repos! The API is very flexible.

## Usage

To Register a new command, simply create an object / property which has
`RoosterCommand` as a parent. Usually, we do this via invocation
of the `roosterCommand` method. Here is an example of
a command saying "hi" when you execute it.

```kotlin
val yourCommand = roosterCommand("yourLabel") {
    onExecute { player.sendMessage("hi") }
}
```

The Rooster compiler will pick it up for you and register it,
and include it inside the generated `plugin.yml`.

## Argument Types

Arguments must be valid in Rooster.
For an argument to be valid, it needs to either have execution logic
or be followed by more valid arguments (which also therefore are either middle arguments,
or arguments with execution logic. <br>
For this logic to be seperated, there is the `Argument` and `UnfinishedArgument` class,
which both extend the `BaseArgument`. They don't work differently, the `Argument`
class just has more options and is more often accepted than the `UnfinishedArgument` class because we
need to restrict the argument tree being valid.

## Core Fields

---
**Key** <br>

Type: `string` <br>
Description: The stuff is cool and yeah

---

### Key Type: `String`

<br>

The unique identifier for each command argument. This key allows access to specific values associated with the argument
during command execution. Every argument must have a `key`.

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
This can be useful for differentiating between argument types during parsing. Use this before checking validness and
potentially sending an error.

### `Error Missing`

A lambda function that defines the action to be taken when a required argument is missing. This allows for custom error
handling to inform the user about the specific arguments that were not provided.

### `Error Missing Child Arg`

A lambda function that handles the situation where a required child argument of the current argument is missing. This
can provide more granular error messages for nested arguments.

### `Argument Handler`

A lambda function that processes the argument value when it is validated and invoked. This function defines how to
handle the data associated with the argument, enabling custom processing logic. Often used together
with [Is Valid](#is-valid) to handle the processed result.

### `Starting Unit` (Root Only)

A lambda function that is called at the beginning of command parsing to determine whether the command should proceed.
This is typically used to enforce preconditions based on the command sender or other contextual factors.

### `Label` (Root Only)

The name of the command as it appears in user input. For example, in the command `/tp @s 200 70 200`, the label is `tp`.
This field is essential for matching user input to the correct command handler.

## Data Classes

### ArgumentInfo

### InvokeInfo

### ArgumentList


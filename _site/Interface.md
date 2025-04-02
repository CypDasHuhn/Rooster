# Interfaces
The InterfaceAPI uses the basic principle that every
view you want to create is an Object, and items inside that item
are abstracted into other Objects, InventoryItems. InventoryItems
arent bound to Slots, but to conditions (the condition might be specific slot).
An Interface Item is composed of three fields: Condition, ItemStackCreator and Action.
The Condition determines whether something should be visible and clickable,
and the other fields declare what should be shown and what should be done.

## Continued later, WIP
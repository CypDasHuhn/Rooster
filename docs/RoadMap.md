Information: This is mostly for myself but i thought i'd might also share,
so if you don't understand stuff: sorry lol ^^'

Todo:

- More Opt-In DB-providers
    - Players (UUID, Name, lastLogin)
    - Items (Serialized ItemStack, Upsert logic)
    - Locations (X, Y, Z, World, angle variables (forgot name), name/key)
    -
- Interface
    - Item constructors
        - Router (mostly done)
        - Context Modifier (mostly done)
        - Filler (needs more constructors)
    - Interface Constructors
        - Page Interface (mostly done)
        - Scroll Interface (needs work)
        - Filter Builder
            - NOT (FIELD OPERATOR VALUE) COMBINATOR (FIELD OPERATOR VALUE)
                - logic when allowed
                    - (:
                        - Before Field &
                            - After Start
                            - After Combinator
                            - Before (
                    - ):
                        - if ( amount is more then ) :
                            - After Value
                            - After )
                    - NOT
                        - Before Field
                        - Before (
                    - FIELD
                        - Before Operator
                            - After NOT
                            - After (
                    - OPERATOR
                        - After Field
                        - Before Value
                    - VALUE
                        - After Operator
        - 2D Graph
- Commands
    - Argument Constructors
        - Filter Builder (not started)
        - Entity Selector (needs work)
        - Measurement Argument (needs work)
        - Attribute Argument (not started)
            - i mean f.e: stone_sword\[minecraft-syntax-attributes]
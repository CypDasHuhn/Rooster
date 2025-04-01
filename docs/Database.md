# Database

Rooster also helps you with Persistent Data.  
The framework is designed with Exposed SQL by Jetbrains together with SQLite,  
but if you personally don't want to use Exposed, the Framework  
doesn't force that on you. <br>  
[!warning] if you focus on having your own storage method,  
you need to replace the internal providers: [LocaleProvider] and [InterfaceContextProivder].  
Here you can find more information for them respecitvly:  
[LinkToLocaleProv], [LinkToContextProv]
---  

## How to create Tables

### New to SQL?

If you aren't familiar with ORM's or SQL in general,  
I'd suggest you to look at these first. You don't need to study, but a quick glance will help. <br>  
Exposed Info: () <br>  
Basic SQL Info: () (You don't need to know SQL, but it's helpful!) <br>

### Registering Tables

Rooster follows the decentral design for Tables too,  
and to create a Table that is picked up by Rooster you  
just need to create a Table, the processor finds it for you.
If you dont want the Table to be registered, use '@RoosterIgnore'.

```kotlin  
object YourTable : IntIdTable() {
    // id already there  ^^^^  
    val yourString = varchar("field_name", 50 /* length */)
    val yourNumber = integer("field_name")

    val yourReference = reference("field_name", otherTable)
}  
```
![styled renaming: your items, named your way!](https://github.com/rosebudmods/styled-renaming/raw/main/.assets/banner.png)

## overview

styled renaming allows you to use colours and other formatting features when renaming items in an anvil through the power of patbox's placeholder api! you can learn all about how to use it [in its documentation](https://placeholders.pb4.eu/user/quicktext/)!

## what's supported?

all players can use the builtin colour tags (`<red>`, `<blue>`, etc), and also use any hex colour code with the `<color>` (or `<c>`) tag!

bold, italic, underlined, strikethrough, and obfuscated tags can also be used!

gradients can also be created using `<gradient>` (or `<gr>`) and `<hard_gradient>` (or `<hgr>`). `<rainbow>` comes builtin!

examples:

- `<red>My Sword` becomes "<font color="#FF5555">My Sword</font>"
- `<c #d4c8ff><b>styled</> <c #f580ad>renaming` becomes "<font color="#d4c8ff"><b>styled</b></font> <font color="#f580ad">renaming</font>"

the italics seen when renaming an item is also removed by default when using text formatting.

(there are also more tags, which can only be used by operators.)

## where do i install the mod?

the mod only needs to be installed on the server! however, installing it on the client will result in some additional quality-of-life features:

- the maximum length for the anvil text box has been increased to account for tags
- putting a renamed item into an anvil will show its name with any tags
- other small fixes

also note that the mod depends on [polymer](https://modrinth.com/mod/xGdtZczs) and [text placeholder api](https://modrinth.com/mod/eXts2L7r), and you'll need to download them.

## credits

- [colorful anvils](https://modrinth.com/mod/Di6KteGa) - basis for some of this mod's code!
- [Patbox](https://modrinth.com/user/L8RLwrF2) - made the placeholder api mod, and a bunch of other cool mods too!

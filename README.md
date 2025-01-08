# Birthday Plugin

A Minecraft plugin to celebrate players' birthdays with customizable features and integrations.

---

## Features

1. **Placeholders**  
   - Use `%birthday_prefix%` and `%birthday_date%` placeholders in other plugins to display a player's birthday prefix and date.

2. **Integration with Oraxen and ItemAdder**  
   - Customize the birthday prefix using Oraxen or ItemAdder glyphs.  
   - Example: Use `(%oraxen_id%)` or directly insert a glyph symbol in the prefix.

3. **Birthday Presents**  
   - Configure special gifts for players, delivered automatically on their birthdays.  
   - Gifts can include custom items, materials, or other rewards.

4. **LuckPerms Integration**  
   - Assign temporary permissions or perks as birthday rewards.  
   - Example: Grant a donation rank or special privileges for a limited time.

5. **Discord Support**  
   - Connect a Discord channel to receive birthday notifications.  
   - Example message: "🎉 Happy Birthday to [Player]! 🎂"

---

## Commands and Permissions

### Commands:
- **/birthday**  
  View and interact with your birthday data.  
  **Permission:** `birthday.use`  

- **/birthday reload**  
  Reload the plugin’s configuration files.  
  **Permission:** `birthday.reload`  

- **/birthday delete**  
  Delete a player's birthday data.  
  **Permission:** `birthday.delete`  

- **/birthday list**  
  List all stored birthday data on the server.  
  **Permission:** `birthday.list`  

- **/birthday present**  
  Manage the birthday present system.  
  **Permission:** `birthday.present`  

  - **/birthday present open**  
    Add items to the birthday present configuration.  
    **Permission:** `birthday.present.open`  

  - **/birthday present give [player]**  
    Give the configured birthday present to a player.  
    **Permission:** `birthday.present.give`  

---

## Integration Tips

- **Placeholders:** Use placeholders with scoreboard or chat formatting plugins to dynamically display birthday-related information.  
- **Oraxen/ItemAdder:** Add custom glyphs to make birthday prefixes unique and visually appealing.  
- **LuckPerms:** Automatically grant temporary perks such as XP boosts, ranks, or permissions as part of the birthday celebration.  
- **Discord Notifications:** Share birthday announcements with your community in a linked Discord channel.

---

## Support

For assistance, feature requests, or bug reports, please reach out to us on Discord. Bring joy to your server with the Birthday Plugin! 🎉

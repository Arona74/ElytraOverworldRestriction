# ElytraOverworldRestriction

A Minecraft Fabric mod that restricts elytra behavior in configured dimensions to gliding only, while maintaining normal functionality in other dimensions.

## Features

### **Gliding Modes**
- **Classic Gliding**(default): Natural elytra physics without artificial speed restrictions, but still no powered flight
- **Realistic Gliding**: Restrictive physics with speed caps and momentum limits for true gliding simulation

### **Dimension Control**
- **Overworld**: Always restricted (core functionality)
- **Nether**: Optional restrictions (disabled by default)
- **Other Dimensions**: Normal elytra behavior maintained (End, custom dimensions)

### **Elytra rendering**
- **Hide Elytra on ground** (default): Make the elytra invisible while on ground for nice visual when using chestplate at same time

### **Universal Restrictions** (Both Modes)
- No rocket-powered boosts (fireworks blocked while flying)
- No ground takeoffs (must jump from height to start gliding)
- Chest slot and trinket slot elytra support

### **Mode Differences**
**Classic Gliding:**
- Natural horizontal speeds allowed
- Natural momentum physics
- More responsive flight feel

**Realistic Gliding:**
- Maximum horizontal speed: 0.6 blocks/tick
- No upward momentum allowed
- Feels restrictive and realistic

## Installation

### Prerequisites
- Minecraft Java Edition 1.20.1
- [Fabric Loader](https://fabricmc.net/use/installer/) 0.14.21 or higher
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.87.0+1.20.1 or higher

### Optional Dependencies
- [ModMenu](https://modrinth.com/mod/modmenu) 7.0.0+ - For in-game configuration
- [Cloth Config](https://modrinth.com/mod/cloth-config) 11.0.0+ - For configuration GUI
- [Trinkets](https://modrinth.com/mod/trinkets) 3.7.0+ - For elytra slot support

### Steps
1. Download the latest release from the [Releases](../../releases) page
2. Place the `.jar` file in your Minecraft `mods` folder
3. Ensure Fabric API is also installed in your `mods` folder
4. Launch Minecraft with the Fabric profile

## Configuration

### **In-Game Configuration** (Requires ModMenu + Cloth Config)
1. Open the Mods menu from the main screen or pause menu
2. Find "ElytraOverworldRestriction" and click the config button
3. Adjust settings:
   - **Enable in Nether**: Extend restrictions to Nether dimension
   - **Realistic Gliding**: Toggle between realistic and classic gliding modes
   - **Hide Elytra on ground**: Toggle rendering of Elytra when on ground
4. Changes apply immediately without restart

### **Manual Configuration**
Edit `config/elytraoverworldrestriction.json`:
```json
{
  "enableInNether": false,
  "realisticGliding": false,
  "invisibleOnGround" : true
}
```

Configuration changes are detected automatically and apply immediately.

## Usage

### **Restricted Dimensions** (Overworld + optionally Nether)
- Jump from heights to begin gliding
- Use firework rockets for visual effects only (no boost)
- Experience either realistic or classic gliding physics based on config

### **Unrestricted Dimensions** (End, custom dimensions)
- Full elytra functionality as normal
- Rocket boosts work as expected
- Ground takeoffs allowed
- No speed limitations

## Compatibility

### **Elytra Slot Support**
Automatically detects elytra in both:
- Vanilla chest armor slot
- Trinket slots (via Trinkets mod)

### **Technical Compatibility**
- **Client/Server**: Works on both single-player and multiplayer
- **Performance**: Minimal impact using efficient server tick events
- **Mod Compatibility**: Should work with most other mods
- **Version Support**: Minecraft 1.20.1, Fabric Loader 0.14.21+

### Technical Details
- Uses Fabric's `ServerTickEvents` for velocity monitoring
- Uses `UseItemCallback` for firework blocking
- Employs reflection for optional Trinkets integration
- Implements live config reloading via file timestamp monitoring

## Changelog

### Version 1.0.0
- Initial release
- Configurable gliding modes (realistic vs classic)
- Optional Nether restrictions
- Trinkets API support for elytra slots
- ModMenu integration with live config reloading
- Full compatibility with vanilla and modded elytra

## Known Issues

- None currently reported

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you encounter any issues or have suggestions:
1. Check the [Issues](../../issues) page for existing reports
2. Create a new issue with detailed information about the problem
3. Include your Minecraft version, Fabric Loader version, and mod version

## Author

Created by **Arona74**

---

*Transform your elytra experience with configurable gliding physics while preserving the freedom to soar in other dimensions!*
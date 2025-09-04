# ElytraOverworldRestriction

A Minecraft Fabric mod that restricts elytra behavior in the Overworld to gliding only, while maintaining normal functionality in other dimensions.

## Features

- **Overworld Restrictions**: 
  - No rocket-powered takeoffs or boosts
  - No ground takeoffs (must jump from height to start gliding)
  - Limited horizontal speed for realistic gliding
  - Prevents upward momentum gain

- **Other Dimensions**: 
  - Normal elytra behavior in the Nether, End, and custom dimensions
  - Full rocket boost functionality maintained
  - Standard takeoff mechanics preserved

## Installation

### Prerequisites
- Minecraft Java Edition 1.20.1
- [Fabric Loader](https://fabricmc.net/use/installer/) 0.14.21 or higher
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.87.0+1.20.1 or higher

### Steps
1. Download the latest release from the [Releases](../../releases) page
2. Place the `.jar` file in your Minecraft `mods` folder
3. Ensure Fabric API is also installed in your `mods` folder
4. Launch Minecraft with the Fabric profile

## Usage

Once installed, the mod automatically applies restrictions:

- **In the Overworld**: Elytra functions as a pure glider
  - Jump from heights to begin gliding
  - Use firework rockets for visual effects only (no boost)
  - Natural physics for realistic flight

- **In other dimensions**: Full elytra functionality as normal
  - Rocket boosts work as expected
  - Ground takeoffs allowed
  - No speed limitations

## Configuration

Currently, the mod uses hardcoded values for optimal gameplay:
- Maximum horizontal speed: 2.0 blocks/tick
- Upward velocity dampening: 90% reduction

Future versions may include a configuration file for customization.

## Compatibility

- **Client/Server**: Works on both single-player and multiplayer
- **Performance**: Minimal impact using efficient server tick events
- **Mod Compatibility**: Built-in Trinket compatibility (for Elytra Slot), should work with most other mods.

### Technical Details
The mod uses Fabric's `ServerTickEvents` to monitor player movement and applies velocity modifications when:
- Player is in the Overworld dimension
- Player is currently fall-flying (using elytra)
- Player has an elytra equipped in chest slot or Trinket slot

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

*Enjoy more balanced elytra gameplay in the Overworld while keeping the freedom to soar in other dimensions!*
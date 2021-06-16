## Fabric Version 1.0.0
Starting with Minecraft 1.17, this mod will support Fabric. The Forge branch will continue once a 1.17 build is ready. There are no new features in this version, other than the mod being capable of running on 1.17.

# Forge (MC 1.12 & 1.16)
All versions listed below are only available for Forge.
## Version 2.2.1
This is a small update that should significantly increase compatibility with other mods.
- Changed the core codebase to utilize injections which should significantly increase mod compatibility.
- Modified reequip animation logic to help with mod compatibility.

## Version 2.2.0
This is a major feature update with a focus on configuration menus. The mod now comes with a graphical user interface that allows changing mod configurations without commands. The default key to access this menu is 'o' and can be opened in multiplayer. This key can be changed in the control settings menu.
- Added a configuration menu.
    - This can be accessed via the mods menu, the 'o' key, or `/oldswing config`
    - The config menu key can be used in multiplayer.
- Added a mod state toggle button.
- Added an animations sub-menu.
- Added a categorical swings sub-menu.
- Added a custom swing sub-menu.
- Added tooltips to all configuration buttons.
- All sub-menus can be reset to their default state.
- Language support is now in development.
- Increased compatibility with other mods.

To see this new major feature update in action, please see the new demo video.

## Version 2.1.0
This is a feature update that adds two new features and fixes a crash with other mods.
```diff
- IMPORTANT -
! This update changes the way custom swings are saved.
! Backup your config if you have customized swing speeds.
```
- Added a new "block" swing speed animation which lets you change how fast the placement animation is.
- Added the ability to set/get the swing speed of the item your character is currently holding.
- Fixed a crash with the whisperwoods mod.
- Redid numerous aspects of the command system.

## Version 2.0.2
Minor update that adds a couple of new features.
- Changed valid swing speed range to 0-16.
    - Setting a swing speed to 0 will cancel the swinging animation altogether.
- Added a new set swing speed command parameter "all".
    - Using the "all" parameter will change all the swing speeds set in the config (including custom speeds).
    - Example: `/oldswing set swing all 0` will cancel every swinging animation in the game.
- Cleaned up the codebase a bit.

## Version 2.0.1
Minor update that fixes a crash and couple bugs.
- The mod is now compatible with Optifine/Optiforge
- Changed the custom swing "items" command parameter to "else" to make it more clear what the command is about to do.
- Fixed a bug that would allow an out of range config value to be saved even after command rejection.

## Version 2.0.0
Old swing has been updated to 1.16! Hooray!
- Every change in the game is now configurable.
- Since Forge does not have an in-game config GUI yet, you can change these values with the `/oldswing` command.
    - You can also use the config file. After you save your changes, the game will automatically update without a restart.
- More old-school animations have been implemented.

### Version 1.1.1
This is a small update that should significantly increase compatibility with other mods.
- Changed the core codebase to utilize injections which should significantly increase mod compatibility.
- Modified reequip animation logic to help with mod compatibility.

### Version 1.1.0
This update backports some features from 2.0 to 1.0.
- Added arm sway animation prevention.
- Added sword, block, and tool swing speed categories.
- Added global swing speed.
- Changed swing speed range to 0-16.
- Updated configuration menu.
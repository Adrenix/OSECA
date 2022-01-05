# Development Branches
- [1.18 Universal](https://github.com/Adrenix/OldSwing/tree/1.18)
- [1.17 Fabric](https://github.com/Adrenix/OldSwing/tree/Fabric-1.17)
- [1.17 Forge](https://github.com/Adrenix/OldSwing/tree/Forge-1.17)
- [1.16 Forge](https://github.com/Adrenix/OldSwing/tree/1.16.5)
- [1.12 Forge](https://github.com/Adrenix/OldSwing/tree/1.12.2)

#### Changelogs
To see the changes made across all versions, please see the [changelog](https://github.com/Adrenix/OldSwing/blob/master/changelog.md) file.

# OSECA (Old Sound, Eye Candy, & Animations) Mod 
[
![CurseForge Downloads](http://cf.way2muchnoise.eu/old-swing.svg)
![CurseForge Versions](http://cf.way2muchnoise.eu/versions/old-swing.svg)
](https://www.curseforge.com/minecraft/mc-mods/old-swing)
[
![Discord](https://img.shields.io/discord/452988045252100107?label=Discord)
](https://discordapp.com/invite/jWdfVh3)

This mod brings back old sounds, eye candy, and animations throughout various old Minecraft versions. All changes can be toggled and swing speeds are heavily customizable. While this mod is intended for nostalgic enthusiasts, it may also be useful for those who are photosensitive.

## What Changes?
Every change this mod makes to the game is configurable.

### Sounds
- Disables all attacking sounds.
 
### Eye Candy
- Added current Minecraft version in the top left of the HUD similar to how it was Minecraft Beta/Alpha.
- Added back the old button hovering graphics.
- Added back the old tooltips.
- Added back the old item durability bar colors.
- Redid the item holding animations similar to how it was back in Minecraft Beta/Alpha.
- Disabled light flickering from light emitting sources. (It is recommended to combine this feature with an old lighting resource pack.)
- Disabled item merging that occurs when dropping items on the ground, drops from mobs, and drops from blocks.
- Brought back the old 2D floating items.
 

### Animations
- Disabled the cooldown modifier animation for items that have a cooldown.
- Disabled the reequip animation when an item gets damaged while swinging.
- Disabled the sweeping particles that appear when attacking entities.
- Disabled the arm sway animation that occurs when looking around.
- Disabled smooth sneaking.
- Disabled the item dropping animations.
- Disabled the tool breaking animation and sounds.
- Disabled the collision bobbing animation that occurs when trying to move while colliding with a block.
- Added back vertical bobbing.
- Added back the old reequipping logic.
 

### Swings
- The swinging animation has been slowed down.
- The swinging animation speed can be changed by item category, by individual item, or by potion effects that impact swing speeds.
- Swinging animations can be disabled entirely if the swing speed is set to 0.
 

More old school eye candy animations and visuals are also in-development.

#### Important
None of the changes the mod makes to the game will impact block breaking speed or combat. Moreover, these changes are only visible to the client. Meaning only you can see the changes in animation and is server safe.

## Photosensitivity
If you are photosensitive, then this mod may be helpful. All swing speeds are configurable, which means you can disable the swing speed animation entirely if that is what you need. Additional changes will also be made by the mod when it detects photosensitivity mode is enabled. More information on how to enable this mode is discussed in the configuration section below.

The changes made to the item holding and swinging rotation animations have not been seen since Minecraft Beta 1.7. The result of these animation changes provide a smoother swinging experience which is easier on the eyes.

## Configuration
Everything that this mod changes is configurable. That means if you prefer sword swinging speeds to be fast and tool swinging speeds to be slow, you can do that. Want to turn off the swing animation when placing blocks? You can do that. Want to turn off the vertical bobbing and arm sway? You can do that. If motion sickness is of concern for you - or for an audience - I recommend setting the animation speed to 10 or higher and disabling vertical bobbing and arm sway (or turn off view bobbing in the vanilla Minecraft settings). This will result in a slower and smoother swinging animation.

You can open the configuration menu by pressing the O (for Old swing) key in-game. The configuration menu can also be accessed through Forge's mod menu. If you are using Fabric, Old Swing does provide support for the mod menu API.

If you are photosensitive, open the configuration menu and click on the General Settings button. Click on the plus (+) icon to expand the Swings settings section. At the bottom of this section, change the Global Swing Speed slider to 0. This will disable the swinging animation entirely for everything. The mod will also make additional changes to other animations to help better your experience.

## Compatibility
If you come across a compatibility issue, please let me know! It is best to create an issue if you come across one. You might also experience animation issues when using modded items that utilize the re-equip animation. If this is the case, you can turn off the oldswing reequip animation in the config or through the /oldswing command.

## FAQ
### Does this mod have a dedicated discord server?
While this discord server isn't dedicated to the mod itself, this is definitely a place where you can come by to share ideas, report issues, and/or just hangout to see what's new.
https://discordapp.com/invite/jWdfVh3

### Can I include this mod in my pack?
If your mod pack is distributed on Curse Forge, then yes! :)

### Is this mod server safe?
Yes. This mod only changes visual animations and is client-side only.

### I ran into an issue running this mod.
Please submit an [issue](https://github.com/Adrenix/OldSwing/issues).

### The mod loaded, but some features are not working.
This means there is a mod conflict. A possible solution is to make sure the Old Swing loads last (or first) and see if that fixes the issue.

## CurseForge Link
https://www.curseforge.com/minecraft/mc-mods/old-swing

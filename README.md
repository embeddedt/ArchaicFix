# ArchaicFix

This mod implements a number of bugfixes, tweaks, and optimizations for Minecraft 1.7.10.

The latest development version can be downloaded here: https://nightly.link/embeddedt/ArchaicFix/workflows/gradle/main/Package.zip

## Dependencies

Newer version of ArchaicFix only require a GTNHMixins-compatible mixin loader. I recommend [UniMixins](https://github.com/LegacyModdingMC/UniMixins).

## Credits

* Forge, for making this possible.
* Sponge, for providing the Mixin framework.
* TheMasterCaver, for writing very informative posts on several vanilla issues.
* makamys, for their assistance with the occlusion culling tweak and various other fixes.
* LoliASM/Rongmario
* MemoryLeakFix, for the Mixin cache clearing code. This is backported with credit since the mod is unlikely to be ported to 1.7.10.
* FalsePattern, for backporting the new Forge config annotations to 1.7.
* Countless other individuals on the Mojang bug tracker who took the time to document the reasons for bugs in the game.

## Compatibility notes

* ArchaicFix is not compatible with versions of GG Util that include threaded lighting. You may use the [fixed version](https://www.curseforge.com/minecraft/mc-mods/gilded-game-utils-fix) instead.
* Crucible/Thermos/etc. are not supported. (https://github.com/embeddedt/ArchaicFix/issues/34)

## List of tweaks/bugfixes

Some tweaks are configurable and I recommend reading the config file to learn more about them.

Non-configurable changes:

* New worlds use the modern naming scheme "New World (n)" rather than continuously appending dashes
* Backport of the Phosphor lighting engine (fixes many lighting issues in vanilla)
* Better LongHashMap implementation (together these two changes fix the infamous 1.7 stuttering)
* Optional 1.8-style occlusion culling (improves FPS, disabled by default)
* Hacks to prevent CME crashes during world generation
* [MC-30845](https://bugs.mojang.com/browse/MC-30845)
* [MC-2025](https://bugs.mojang.com/browse/MC-2025) (partially fixed, full fix is in progress)
* Villagers no longer lock all their trades
* ChickenChunks should no longer crash in combination with chunkloading from other mods like Extra Utilities
* Various memory leak fixes (most noticeable when changing dimensions or exiting/rejoining the world)
* Matter Overdrive only recomputes its registry file if one does not already exist in config/MatterOverdrive

------------

![YourKit logo](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>,
<a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>,
and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.

Thanks to YourKit for providing a free license for this project.

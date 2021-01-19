# McScanKt

McScanKt is a software for analyzing a minecraft map and to show items with a changed name and / or a description to do a ranking

This is a refactoring with better external nbt library from my old java project : https://github.com/fhebuterne/mc-scan

This project use [Kotlin](https://kotlinlang.org/) (typesafe and modern language).

## Requirements

- Java 11

## Build

McScanKt use Gradle 6, to build use this command :

```
gradlew clean build
```

## Usage

McScanKt jar is in `./build/libs/` folder, to use :
```
java -jar McScanKt-1.0.0.jar --help
```

Arguments :
```
--worldFolder or -w ./path/to/world : Analyze Minecraft map
--regionFile or -r ./path/to/region.mca : Analyze one region file
--playerData or -p ./path/to/player.dat : Analyze one player file
```

## Community Discord

Discord : https://discord.gg/gWe5u3A

## Licence

[GPLv3](LICENSE)

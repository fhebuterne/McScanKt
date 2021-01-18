# McScanKt

McScan is a software for analyzing a minecraft map and to show items with a changed name and / or a description to do a ranking

## Requirements

- Java 11

## Build

McScan use Gradle 6, to build use this command :

```
gradlew clean build
```

## Usage

McScan jar is in `./build/libs/` folder, to use :
```
java -jar McScanKt-1.0.0.jar --help
```

Arguments :
```
--world ./path/to/world : Analyze Minecraft map
--region ./path/to/region.mca : Analyze one region file
--player ./path/to/player.dat : Analyze one player file
```

## Community Discord

Discord : https://discord.gg/gWe5u3A

## Licence

[GPLv3](LICENSE)

# McScanKt

![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/fhebuterne/McScanKt/McScanKt%20CI%20-%20Java%2011%20with%20Kotlin/master?style=flat-square)
![GitHub repo size](https://img.shields.io/github/repo-size/fhebuterne/McScanKt?style=flat-square)
![Lines of code](https://img.shields.io/tokei/lines/github/fhebuterne/McScanKt?style=flat-square)
[![GitHub license](https://img.shields.io/github/license/fhebuterne/McScanKt?style=flat-square)](https://github.com/fhebuterne/McScanKt/blob/master/LICENSE)

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

## Wiki

Don't forget to read wiki before ask questions.

[Wiki Home](https://github.com/fhebuterne/McScanKt/wiki)

## Analyzed elements

<table>
    <thead>
    <tr>
        <th>Type</th>
        <th>Elements</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>World</td>
        <td>
          - All regions files
          <br/>- All players data
        </td>
    </tr>
    <tr>
        <td>Region file</td>
        <td>
          - All chunks
        </td>
    </tr>
    <tr>
        <td>Chunk</td>
        <td>
          - All TileEntities
          <br/>- Entities contains Items (itemframes etc...)
        </td>
    </tr>
    <tr>
        <td>Player data</td>
        <td>
          - Inventory
          <br/>- Enderchest
        </td>
    </tr>
</table>


## Supported Minecraft versions

| MC Version     | Supported    |
|:----------------:|:--------------:|
| 1.16.5         |✅            |
| 1.15.2         |⚠ (WIP)       |
| 1.14.4         |⚠ (WIP)       |
| 1.13.2         |⚠ (WIP)       |
| <= 1.12.2      |❌            |

⚠ **1.12.2 and lower versions are not yet tested but may work !** ⚠

## Community Discord

Discord : https://discord.gg/gWe5u3A

## License

[GPLv3](LICENSE)

# Hytale Discord Integration

This is a (currently) small mod to link your Hytale server's Chat to a Discord channel.


Messages can be sent in both directions. 
Additionally there will be messages for players joining, leaving and the server starting or stopping.

This mod will be extended in the future to have plenty of additional features! Stay tuned.

## How to build
Download the hytale server jar using a known method, like the official hytale server downloader.

Then you have the following options:

- Copy the hytale server jar to `./libs/HytaleServer.jar`.
- Set the environment variable `HT_SERVER_JAR` to the path of the hytale server jar.
- Change the default value of `./libs/HytaleServer.jar` in `./build.gradle` to point to your local hytale server jar file.

After that everything gradle (like gradlew build) should work.

### Build with nix

To initialize the `gradle.lock` file use:
```bash
nix run .#gradle2nix
```

To build the jar use:
```bash
nix build .#htdcintegration --impure --include hytale-server-jar=/path/to/HytaleServer.jar
```

You can find the jar file at `./result/libs/*.jar`.

To format `**/*.nix` and `**/*.py` files use:

```bash
nix fmt
```

Note that you have to enable the following experimental features in nix: `nix-command` and `flakes`.

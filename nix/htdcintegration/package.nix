{
  read-properties,
  buildGradlePackage,
  gradle_9-unwrapped,
  hytale-server-jar,
  lib,
  withBuildJdk,
  withSrc,
}:
let
  properties = read-properties "${withSrc}/gradle.properties";
in
buildGradlePackage {
  buildJdk = withBuildJdk;
  pname = properties.name;
  gradle = gradle_9-unwrapped;
  inherit (properties) version;
  lockFile = "${withSrc}/gradle.lock";
  gradleBuildFlags = [ "build" ];
  src = withSrc;
  outputs = [
    "out"
    "devdoc"
  ];
  env = {
    # always copy the server jar to this location within the nix build folder
    HT_SERVER_JAR = "./libs/HytaleServer.jar";
    HT_SKIP_SOURCES = "true";
  };
  meta = {
    description = "Mod for integrating Discord with the Hytale Server";
    sourceProvenance = with lib.sourceTypes; [
      fromSource
      binaryBytecode
    ];
    licenses = with lib.licenses; [ mit ];
    homepage = "https://github.com/ErdbeerbaerLP/HT-DiscordIntegration";
    downloadPage = "https://www.curseforge.com/hytale/mods/discord-bridge";
    platforms = lib.platforms.all;
  };
  preBuild = ''
    mkdir -p ./libs
    cp ${hytale-server-jar} $HT_SERVER_JAR
  '';
  installPhase = ''
    runHook preInstall
    mkdir -p $out/libs
    cp -r build/libs/* $out/libs/
    mkdir -p $devdoc/share/devhelp/htdcintegration
    mv $out/libs/*-javadoc.jar $devdoc/share/devhelp/htdcintegration/
    runHook postInstall
  '';
}

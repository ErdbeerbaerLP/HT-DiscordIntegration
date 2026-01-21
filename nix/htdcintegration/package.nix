{
  read-properties,
  buildGradlePackage,
  gradle_9-unwrapped,
  lib,
  withBuildJdk,
  withSrc,
  withHytaleServerJar,
  withSourcesJar ? false,
  withJavadocJar ? false,
}:
let
  properties = read-properties "${withSrc}/gradle.properties";
  copyAllJars = ''
    mkdir -p $out/libs
    cp -r build/libs/* $out/libs/
  '';
  moveJars =
    {
      output,
      suffix,
      skip,
    }:
    lib.optionalString (!skip) ''
      mkdir -p ''$${output}/share/java
      for f in $out/libs/*${suffix}.jar; do
        mv "$f" ''$${output}/share/java/
      done
    '';
in
(buildGradlePackage {
  buildJdk = withBuildJdk;
  pname = properties.name;
  gradle = gradle_9-unwrapped;
  inherit (properties) version;
  lockFile = "${withSrc}/gradle.lock";
  gradleBuildFlags = [ "build" ];
  src = withSrc;
  outputs = [
    "out"
  ]
  ++ lib.optionals withSourcesJar [ "dev" ]
  ++ lib.optionals withJavadocJar [ "devdoc" ];
  env = {
    HT_SERVER_JAR = "./libs/HytaleServer.jar";
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
    cp ${withHytaleServerJar} $HT_SERVER_JAR
  '';
  installPhase = ''
    runHook preInstall
    ${copyAllJars}
    ${moveJars {
      output = "dev";
      suffix = "-sources";
      skip = !withSourcesJar;
    }}
    ${moveJars {
      output = "devdoc";
      suffix = "-javadoc";
      skip = !withJavadocJar;
    }}
    runHook postInstall
  '';
}).overrideAttrs
  # overriding attributes instead of just setting them above to keep the default value of gradleFlags
  (
    oldAttrs: {
      gradleFlags =
        oldAttrs.gradleFlags
        ++ lib.optionals (!withSourcesJar) [
          "-x"
          "sourcesJar"
        ]
        ++ lib.optionals (!withJavadocJar) [
          "-x"
          "javadocJar"
        ];
    }
  )

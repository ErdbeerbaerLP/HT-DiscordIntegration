{
  stdenvNoCC,
  gradle2nix,
  makeWrapper,
  withBuildJdk,
  lib,
}:
stdenvNoCC.mkDerivation (finalAttrs: {
  pname = "${finalAttrs.unwrapped.pname}-with-${withBuildJdk.name}";
  inherit (finalAttrs.unwrapped) version;
  unwrapped = gradle2nix;
  nativeBuildInputs = [ makeWrapper ];
  phases = [ "installPhase" ];
  installPhase = ''
    runHook preInstall
    echo "mkdir" >&2
    mkdir -p $out/bin
    echo "cp" >&2
    cp -r $unwrapped/* $out/
    echo "post" >&2
    runHook postInstall
  '';
  postInstall = ''
    ls -la $unwrapped >&2
    ls -la $out >&2
    wrapProgram $out/bin/${finalAttrs.unwrapped.meta.mainProgram} --set JAVA_HOME ${lib.escapeShellArg withBuildJdk}
  '';
  meta = {
    inherit (finalAttrs.unwrapped.meta) platforms mainProgram homepage;
    description = "Wrap Gradle builds with Nix and JDK ${withBuildJdk.name}";
    license = lib.flatten [
      withBuildJdk.meta.license
      finalAttrs.unwrapped.meta.license
    ];
  };
})

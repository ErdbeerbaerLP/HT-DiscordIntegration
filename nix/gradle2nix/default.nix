{ inputs, ... }:
{
  # there is no upstream overlay for gradle2nix
  flake.overlays.gradle2nix =
    final: prev:
    let
      inherit (prev.stdenv.hostPlatform) system;
      builders = inputs.gradle2nix.builders.${system};
      packages = inputs.gradle2nix.packages.${system};
    in
    {
      gradleSetupHook = final.callPackage packages.gradleSetupHook.override { };
      gradle2nix = final.callPackage packages.gradle2nix.override { };
      buildGradlePackage = final.callPackage builders.buildGradlePackage.override { };
      buildMavenRepo = final.callPackage builders.buildMavenRepo.override { };
    };
  perSystem =
    { pkgs, config, ... }:
    {
      packages.gradle2nix = pkgs.callPackage ./package.nix {
        withBuildJdk = config.jdk;
      };
    };
}

{ rootPath, ... }:
{
  perSystem =
    { pkgs, config, ... }:
    {
      packages.htdcintegration = pkgs.callPackage ./package.nix {
        inherit (config) hytale-server-jar;
        withBuildJdk = config.jdk;
        withSrc = rootPath;
      };
    };
}

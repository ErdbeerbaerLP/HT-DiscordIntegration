# To initialize the `gradle.lock` file use `nix run .#gradle2nix`
# To build the jar use `nix build .#htdcintegration --impure --include hytale-server-jar=/path/to/HytaleServer.jar`
# To format this file use `nix fmt`
{
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  inputs.flake-parts.url = "github:hercules-ci/flake-parts";
  inputs.gradle2nix.url = "github:tadfisher/gradle2nix/v2";
  inputs.gradle2nix.inputs.nixpkgs.follows = "nixpkgs";
  inputs.treefmt-nix.url = "github:numtide/treefmt-nix";
  inputs.treefmt-nix.inputs.nixpkgs.follows = "nixpkgs";

  outputs =
    inputs:
    inputs.flake-parts.lib.mkFlake { inherit inputs; } (
      { self, ... }:
      {
        _module.args.rootPath = ./.;

        imports = [
          inputs.treefmt-nix.flakeModule
          ./nix/gradle2nix
          ./nix/htdcintegration
          ./nix/read-properties
        ];

        systems = [
          "aarch64-darwin"
          "aarch64-linux"
          "x86_64-darwin"
          "x86_64-linux"
        ];

        perSystem =
          {
            self',
            pkgs,
            system,
            lib,
            ...
          }:
          {
            options = {
              jdk = lib.mkOption {
                default = pkgs.javaPackages.compiler.temurin-bin.jdk-25;
                type = with lib.types; either package path;
              };
              hytale-server-jar = lib.mkOption {
                default = builtins.addErrorContext ''
                  Please build this package with
                  $ nix build .#htdcintegration --impure --include hytale-server-jar=/path/to/HytaleServer.jar
                  or by using:
                  htdcintegration.override { hytale-server-jar = pkgs.fetchurl { url = "https://invalid/url/to/HytaleServer.jar"; hash = ""; }; }
                '' <hytale-server-jar>;
                type = with lib.types; either package path;
              };
            };

            config = {
              _module.args.pkgs = import inputs.nixpkgs {
                inherit system;
                overlays = [
                  self.overlays.gradle2nix
                  self.overlays.read-properties
                ];
              };
              packages = {
                default = self'.packages.htdcintegration;
              };
              treefmt = {
                projectRootFile = ".git/config";
                programs = {
                  nixfmt.enable = true;
                  deadnix.enable = true;
                  statix.enable = true;
                  black.enable = true;
                };
                settings.formatter.black.options = [
                  "--line-length"
                  "79"
                ];
                settings.walk = "git";
              };
            };
          };
      }
    );
}

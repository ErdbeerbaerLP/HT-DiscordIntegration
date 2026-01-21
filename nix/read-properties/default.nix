{
  flake.overlays.read-properties = final: _prev: {
    read-properties = final.callPackage ./package.nix {
      withPython = final.python314;
    };
  };
}

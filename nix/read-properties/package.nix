{
  runCommand,
  withPython,
}:

path:
let
  json = runCommand "read-properties" {
    inherit path;
    nativeBuildInputs = [
      (withPython.withPackages (ps: with ps; [ javaproperties ]))
    ];
  } "python3 ${./read-properties.py}";
in
builtins.fromJSON (builtins.readFile "${json}")

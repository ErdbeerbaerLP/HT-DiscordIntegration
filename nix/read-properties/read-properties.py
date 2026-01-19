import json
import javaproperties
from os import getenv

with open(getenv("path"), mode="r", encoding="utf-8") as r:
    data = javaproperties.load(r)
    with open(getenv("out"), mode="w", encoding="utf-8") as w:
        json.dump(data, w)

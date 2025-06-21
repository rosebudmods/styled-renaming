#!/bin/bash

cd build
rm -f *.jar
rm -rf 1.*

for name in ../versions/*; do
    version=$(basename "$name")

    # copy built jar
    built=$(ls -Art $name/build/libs | tail -n1)
    cp "$name/build/libs/$built" "$version.jar"

    # unzip jar
    unzip -q "$version.jar" -d "$version"

    # remove minecraft version metadata
    sed -E -i "s/^(\s*)\"version\":\s*\"(.+?)\+1(\.[0-9]+)+\"(,?)\$/\1\"version\": \"\2\"\4/" "$version/fabric.mod.json"
    sed -i "/^Fabric-Minecraft-Version/d" "$version/META-INF/MANIFEST.MF"

    if [[ -z "$last_version" ]]; then
        last_version=$version
        continue
    fi

    # diff versions
    diff -r "$last_version" "$version" > /dev/null
    identical=$?

    if [[ "$identical" == "0" ]]; then
        echo $last_version and $version are identical
    else
        echo $version is NOT compatible with $last_version
    fi

    last_version=$version
done

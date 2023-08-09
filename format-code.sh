#!/usr/bin/env sh
mkdir -p .cache
cd .cache
if [ ! -f google-java-format-1.17.0-all-deps.jar ]
then
    curl -LJO "https://github.com/google/google-java-format/releases/download/v1.17.0/google-java-format-1.17.0-all-deps.jar"
    chmod 755 google-java-format-1.17.0-all-deps.jar
fi
cd ..

#changed_java_files=$(git diff --cached --name-only --diff-filter=ACMR | grep ".*java$" )
#echo $changed_java_files
#java -jar .cache/google-java-format-1.17.0-all-deps.jar --replace $changed_java_files

## escludo le classi con il String text block perchè non ancora supportate da google java format
java -jar .cache/google-java-format-1.17.0-all-deps.jar --replace $(find . -type f -name "*.java" ! -name "App.java" ! -name "PspResourceTest.java" ! -name "OrganizationResourceTest.java")

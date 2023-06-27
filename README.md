# mtls-tool

## Install Dependencies

Install dependency into local system-wide Maven repository
```
mvn install:install-file \
  -DgroupId=com.schunker.java \
  -DartifactId=JavaConsoleKit \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=jar \
  -DgeneratePom=true \
  -Dfile=./local-maven-repo/JavaConsoleKit-1.0-SNAPSHOT.jar
```

## Deploy Dependencies

Deploy dependency into local project-specific Maven repository
```
mvn deploy:deploy-file \
  -DgroupId=com.schunker.java \
  -DartifactId=JavaConsoleKit \
  -Dversion=1.0-SNAPSHOT \
  -Durl=file:./local-maven-repo/ \
  -DrepositoryId=local-maven-repo \
  -DupdateReleaseInfo=true \
  -Dfile=./local-maven-repo/JavaConsoleKit-1.0-SNAPSHOT.jar
```

View Manifest File of Dependency
```
unzip -p local-maven-repo/JavaConsoleKit-1.0-SNAPSHOT.jar META-INF/MANIFEST.MF
```

## Download Dependencies
```
mvn dependency:resolve
```

## Verify Dependencies
```
mvn -X dependency:tree
mvn dependency:tree | grep <dependency-name>
```

## Test
```
mvn test
```

## Execute
```
java -cp "C:\\dev\\philipp-schunker\\mtls-tool\\target\\classes;C:\\dev\\philipp-schunker\\JavaConsoleKit\\target\\classes" com.schunker.mtls.App
java -jar target/mtls-tool-1.0-SNAPSHOT-shaded.jar
```

## Package
```
mvn [--settings settings.xml] clean package [-X]
```

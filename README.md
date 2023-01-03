# mtls-tool

## Install Dependencies
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

## Package
```
mvn [--settings settings.xml] clean package
```

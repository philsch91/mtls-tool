# mtls-tool

## Install Dependencies

[Install JavaConsoleKit into local system-wide Maven repository](https://github.com/philsch91/JavaConsoleKit?tab=readme-ov-file#install)
```
mvn install:install-file \
  -Dfile=./local-maven-repo/JavaConsoleKit-1.0-SNAPSHOT.jar \
  -DgroupId=com.schunker.java \
  -DartifactId=JavaConsoleKit \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=jar \
  -DgeneratePom=true
```

## Deploy Dependencies

Deploy dependency into local project-specific Maven repository
```
mvn deploy:deploy-file \
  -Dfile=./local-maven-repo/JavaConsoleKit-1.0-SNAPSHOT.jar \
  -DgroupId=com.schunker.java \
  -DartifactId=JavaConsoleKit \
  -Dversion=1.0-SNAPSHOT \
  -Durl=file:./local-maven-repo/ \
  -DrepositoryId=local-maven-repo \
  -DupdateReleaseInfo=true
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
mvn dependency:tree [-Ddetail=true] | grep <dependency-name>
```

## Test
```
mvn test
```

## Execute
```
# Windows
java -cp "C:\\dev\\philipp-schunker\\mtls-tool\\target\\classes;C:\\dev\\philipp-schunker\\JavaConsoleKit\\target\\classes" com.schunker.mtls.App
# Linux
java -cp "/mnt/c/dev/philipp-schunker/mtls-tool/target/classes:/mnt/c/dev/philipp-schunker/JavaConsoleKit/target/classes" com.schunker.mtls.App
java [-Djava.security.keystore.type=<pkcs12/jks> -Djavax.net.ssl.keyStoreType=<pkcs12/jks> -Djavax.net.ssl.trustStoreType=<pkcs12/jks> -Dhttps.proxyHost=proxy.hostname.com -Dhttps.proxyPort=8080 -Dhttp.nonProxyHosts="*.subd.tld.com|*.tld.com" -Djava.util.logging.config.file=src/main/resources/logging.properties -Djavax.net.debug=all -Dssl.SocketFactory.provider=WireLogSSLSocketFactory] -jar target/mtls-tool-1.0-SNAPSHOT-shaded.jar
```

## Package
```
mvn [--settings settings.xml] clean package [-X]
```

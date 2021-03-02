# Blog Demo


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

Or:

```shell script
./gradlew quarkusDev
```


## Generating the Slide Show

Generate the slide show by calling the `asciidoctor:process-asciidoc`
Maven goal:

```shell script
./mvnw asciidoctor:process-asciidoc
```

You can find the HTML file at `target/generated-docs/SLIDES.html`.


## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

Or (Gradle):

```shell script
./gradlew build
```


## Creating a native executable

You can create a native executable using: 

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Or (Gradle):

```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

FROM adoptopenjdk/openjdk11:jre
VOLUME /tmp
COPY target/widget*.jar widget.jar
CMD [ "java", "-jar", "widget.jar" ]
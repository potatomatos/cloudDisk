FROM arm32v7/adoptopenjdk:11
MAINTAINER mengjinyuan 782399171@qq.com
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone
WORKDIR /ROOT
COPY target/app.jar /ROOT
COPY target/libs /ROOT
COPY target/resources /ROOT
EXPOSE 9203
ENTRYPOINT ["java","-jar", "/ROOT/app.jar"]
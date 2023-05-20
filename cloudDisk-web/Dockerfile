FROM arm32v7/adoptopenjdk:11
MAINTAINER mengjinyuan 782399171@qq.com
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone
ADD target/app.jar app.jar
EXPOSE 9203
ENTRYPOINT ["java","-jar", "/app.jar"]
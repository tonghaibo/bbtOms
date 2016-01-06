name := """bbtOms"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
        "org.apache.httpcomponents" % "httpclient" % "4.3.6",
        "org.apache.httpcomponents" % "httpmime" % "4.3.6",
  		"org.sedis" %% "sedis" % "1.2.2",
  		"com.belerweb" % "pinyin4j" % "2.5.0",
  		"mysql" % "mysql-connector-java" % "5.1.18",
  		"org.apache.poi" % "poi" % "3.10-FINAL",
    	"org.apache.poi" % "poi-ooxml" % "3.10-FINAL",
		"org.apache.httpcomponents" % "httpclient" % "4.3.6",
		"commons-collections" % "commons-collections" % "3.2.1",
		"org.apache.commons" % "commons-lang3" % "3.3.1",
		"org.apache.httpcomponents" % "httpclient" % "4.3.6",
		"org.apache.httpcomponents" % "httpmime" % "4.3.6",
		"commons-httpclient" % "commons-httpclient" % "3.1",
		"net.logstash.logback" % "logstash-logback-encoder" % "3.6",
		"ch.qos.logback" % "logback-core" % "1.1.2",
		"ch.qos.logback" % "logback-classic" % "1.1.2",
		"log4j" % "log4j" % "1.2.17",
		"org.json" % "json" % "20080701",
		"dom4j" % "dom4j" % "1.6",
		"com.alibaba" % "druid" % "1.0.12",
		"com.aliyun.oss" % "aliyun-sdk-oss" % "2.0.1",
		 "com.google.zxing" % "core" % "3.1.0",
		 "com.google.code.gson" % "gson" % "2.2.4",
		 cache
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

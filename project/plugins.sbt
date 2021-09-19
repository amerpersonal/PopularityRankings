import sbt.Resolver

logLevel := Level.Warn

//resolvers += "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"
//resolvers += Resolver.jcenterRepo

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6" exclude("org.apache.maven", "maven-plugin-api"))


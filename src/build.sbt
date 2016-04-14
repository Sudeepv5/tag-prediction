lazy val root = (project in file(".")).
  settings(
    name := "Cooccurrence",
    version := "1.0",
    mainClass in Compile := Some("extract.Cooccurrence")
  )

libraryDependencies += "org.apache.opennlp" % "opennlp-tools" % "1.6.0"

libraryDependencies += "org.apache.opennlp" % "opennlp-uima" % "1.6.0"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.6.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce" % "2.6.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-common" % "2.6.0"




jar:
	(cd src && sbt package)
	cp src/target/scala-*/cooccurrence_*.jar Cooccurrence.jar

run:
	rm -rf data/cooccurrence
	sbt run

hadoop:
	rm -rf data/cooccurrence
	hadoop jar Cooccurrence.jar

clean:
	rm -rf src/project src/target *.jar data/cooccurrence



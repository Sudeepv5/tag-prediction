
jar:
	gradle build

run:
	rm -rf data/cooccurrence
	sbt run



copy:
	hadoop fs -put data/Questions-Dev data/

hadoop:
	#rm -rf data/cooccurrence
	hadoop fs -rm -r data/cooccurrence
	hadoop jar build/libs/Force.jar extract.Cooccurrence

clean:
	zip -d build/libs/Force.jar META-INF/LICENSE



# tag-prediction

A text model to predict tags for stackover flow questions - Supervised Learning

  preprocess.Parser.java
	->Extracts the required fields from Posts.xml and writes to Questions-Dev, Test
	
	extract.Cooccurrence.java
	->Map Reduce program to tokenize and stem all the words from the Ques-* and write a Coooccurrence matrix

	extract.WordTagIndexer.java
	->Indexes the cooccurrence matrix for faster access

	model.CooccurrenceModel.java
	->Builds a model to get top 20 tags for a given question

	model.KNNModel.java [NOt used in the final cut]
	->Builds a model to predict tags in better order, measuring the co-sine similarity

	model.BM25.java
	->Builds a model to predict tags in better order. takes the input from CooccurrenceModel.java

	evaluate.Evaluation.java
	->Soft evaluation for 20 tags
	->Hard evaluatipon for 3 tags
	->Single tag evaluation

	analyze.TagAnalysis.java
	->Used in plotting the graphs for tag distribution

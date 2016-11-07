import java.io.File
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation.MulticlassMetrics


// import org.apache.spark.mllib.util.MLUtil.
val path = "D:\\ScalaSpace\\data\\financialCardMatch\\matchData"
val trainFile = path + "\\train1.csv"
val trianRate = Array(0.8,0.2)
val rst = new Array[(String,Double)](4)
rst(0) = "LRacc : " -> 0d
rst(1) = "DTacc : " -> 0d
rst(2) = "RFacc : " -> 0d
rst(3) = "GBDTacc : " -> 0d

def isNum( _c : Char ) : Boolean = _c >= '0' && _c <= '9'


  def csvFile(_path : String, _rateZero : Int = 1) : RDD[LabeledPoint] = {
    val rawData = scala.io.Source.fromFile(trainFile)("utf-8").getLines.drop(1).toArray
    val data = sc.parallelize(rawData).flatMap{
      line =>
        val splits = line.split(',')
        val label = splits.last.toDouble.toInt
        val features = new DenseVector(splits.init.map(_.toDouble))
        label match {
          case 0 => Array.fill[LabeledPoint](_rateZero)(new LabeledPoint(label,features))
          case 1 => Array.fill[LabeledPoint](1)(new LabeledPoint(label,features))
          case _ => Array.fill(0)(new LabeledPoint(label,features))
        }
    }  
    data
  }


val data = csvFile(trainFile)


import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation.MulticlassMetrics

// Split data into training (60%) and test (40%).
val splits = data.randomSplit(trianRate, seed = 11L)
val training = splits(0).cache()
val test = splits(1)

// Run training algorithm to build the model
val model = new LogisticRegressionWithLBFGS().setNumClasses(10).run(training)

// Compute raw scores on the test set.
val predictionAndLabels = test.map { case LabeledPoint(label, features) =>
  val prediction = model.predict(features)
  (prediction, label)
}

val metrics = new MulticlassMetrics(predictionAndLabels)
rst(0) = "LRacc : " -> metrics.accuracy


import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel

// Split the data into training and test sets (30% held out for testing)
val splits = data.randomSplit(trianRate)
val (trainingData, testData) = (splits(0), splits(1))

// Train a DecisionTree model.
//  Empty categoricalFeaturesInfo indicates all features are continuous.
val numClasses = 2
val categoricalFeaturesInfo = Map[Int, Int]()
val impurity = "gini"
val maxDepth = 5
val maxBins = 32

val model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
  impurity, maxDepth, maxBins)

// Evaluate model on test instances and compute test error
val labelAndPreds = testData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}
rst(1) = "DTacc : " -> labelAndPreds.filter(r => r._1 == r._2).count().toDouble / testData.count()

import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel

val splits = data.randomSplit(trianRate)
val (trainingData, testData) = (splits(0), splits(1))

// Train a RandomForest model.
// Empty categoricalFeaturesInfo indicates all features are continuous.
val numClasses = 2
val categoricalFeaturesInfo = Map[Int, Int]()
val numTrees = 3 // Use more in practice.
val featureSubsetStrategy = "auto" // Let the algorithm choose.
val impurity = "gini"
val maxDepth = 4
val maxBins = 32

val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
  numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

// Evaluate model on test instances and compute test error
val labelAndPreds = testData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}
rst(2) = "RFacc : " -> labelAndPreds.filter(r => r._1 == r._2).count.toDouble / testData.count()

import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.configuration.BoostingStrategy
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel

val splits = data.randomSplit(trianRate)
val (trainingData, testData) = (splits(0), splits(1))

// Train a GradientBoostedTrees model.
// The defaultParams for Classification use LogLoss by default.
val boostingStrategy = BoostingStrategy.defaultParams("Classification")
boostingStrategy.numIterations = 3 // Note: Use more iterations in practice.
boostingStrategy.treeStrategy.numClasses = 2
boostingStrategy.treeStrategy.maxDepth = 5
// Empty categoricalFeaturesInfo indicates all features are continuous.
boostingStrategy.treeStrategy.categoricalFeaturesInfo = Map[Int, Int]()

val model = GradientBoostedTrees.train(trainingData, boostingStrategy)

// Evaluate model on test instances and compute test error
val labelAndPreds = testData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}
rst(3) = "GBDTacc" -> labelAndPreds.filter(r => r._1 == r._2).count.toDouble / testData.count()



rst.mkString("\n")


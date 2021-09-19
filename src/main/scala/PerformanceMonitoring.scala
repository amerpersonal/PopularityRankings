import java.io.IOException

import rankings.{CollectionChainingRanker, FoldRanker, RecursionRanker}
import utils.Files
import utils.Files._
import spray.json._
import serializers.FormattedStatisticsSerializer._

import scala.io.Source

object PerformanceMonitoring {
  def main(args: Array[String]): Unit = {

    for {
      filename <- args.headOption.orElse(throw new IOException("You must specify a file"))
      _ <- if (filename.split("\\.").last == "csv") Some(Unit) else throw new IOException("You must specify a valid CSV file")
      f <- Files.createFile("rankings.csv")

      in1 <- f.createInputStream()
      in2 <- f.createInputStream()
      in3 <- f.createInputStream()

      source1 = Source.fromInputStream(in1).getLines()
      source2 = Source.fromInputStream(in2).getLines()
      source3 = Source.fromInputStream(in3).getLines()

      chainingTimeAndOutput = CollectionChainingRanker.calculateAndMesaure(source1)
      recursionTimeAndOutput = RecursionRanker.calculateAndMesaure(source2)
      foldTimeAndOutput = FoldRanker.calculateAndMesaure(source3)

      _ = in1.close()
      _ = in2.close()
      _ = in3.close()

      _ = println(s"Statistics calculated using collection chaining in ${chainingTimeAndOutput._1} ms: ${chainingTimeAndOutput._2.toJson}")
      _ = println(s"Statistics calculated recursion in ${recursionTimeAndOutput._1} ms: ${recursionTimeAndOutput._2.toJson}")
      _ = println(s"Statistics calculated fold in ${foldTimeAndOutput._1} ms: ${foldTimeAndOutput._2.toJson}")

    } yield ()

  }
}

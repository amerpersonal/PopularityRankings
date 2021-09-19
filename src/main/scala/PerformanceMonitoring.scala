import rankings.{CollectionChainingRanker, FoldRanker, RecursionRanker}
import utils.Files
import utils.Files._
import spray.json._

object PerformanceMonitoring {
  def main(args: Array[String]): Unit = {
    args.headOption match {
      case Some(filename: String) if filename.split("\\.").last == "csv" => {
        for {
          f <- Files.createFile(filename)
          in <- f.createInputStream()

          chainingTimeAndOutput = CollectionChainingRanker.calculateAndMesaure(in)
          recursionTimeAndOutput = RecursionRanker.calculateAndMesaure(in)
          foldTimeAndOutput = FoldRanker.calculateAndMesaure(in)

          _ = in.close()

          _ = println(s"Statistics calculated using collection chaining in ${chainingTimeAndOutput._1} ms: ${chainingTimeAndOutput._2.toJson}")
          _ = println(s"Statistics calculated recursion in ${recursionTimeAndOutput._1} ms: ${recursionTimeAndOutput._2.toJson}")
          _ = println(s"Statistics calculated fold in ${foldTimeAndOutput._1} ms: ${foldTimeAndOutput._2.toJson}")

        } yield ()
      }
      case _ => println("You have to pass a valid csv file. Please try again...")
    }
  }
}

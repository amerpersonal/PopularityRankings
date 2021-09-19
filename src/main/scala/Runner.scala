import java.io._

import rankings._
import utils.Files
import Files._

object Runner {
  def main(args: Array[String]): Unit = {
    for {
      filename <- args.headOption.orElse(throw new IOException("You must specify a file"))
      _ <- if (filename.split("\\.").last == "csv") Some(Unit) else throw new IOException("You must specify a valid CSV file")
      f <- Files.createFile(filename)
      in <- f.createInputStream()

      output = RecursionRanker.calculate(in)

      _ = in.close()
    } yield println(output)
  }
}

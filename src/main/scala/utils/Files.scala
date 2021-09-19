package utils

import java.io.{File, FileInputStream, FileWriter}

import scala.util.Try

object Files {
  def createFileWithLines(filename: String, lines: Seq[String]): Option[File] = {
    for {
      f <- createFile(filename)
      fw <- f.createWriter()
      _ <- fw.writeLines(lines)
      _ <- fw.closeWriter()
    } yield f
  }

  def removeFile(f: File): Option[File] = f.remove

  def createFile(filename: String) =  Try(new File(filename)).toOption

  implicit class FileOps(f: File) {
    def check() = if (f.exists()) Some(f) else None

    def remove = if (f.delete()) Some(f) else None

    def createWriter() = Try(new FileWriter(f)).toOption

    def createInputStream() = Try(new FileInputStream(f)).toOption

  }

  implicit class FileWriterOps(fw: FileWriter) {
    def writeLines(lines: Seq[String]) = Try(fw.write(lines.mkString("\n"))).toOption

    def closeWriter() = Try(fw.close()).toOption
  }
}

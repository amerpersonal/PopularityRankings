package models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsArray, JsNull, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import scala.collection.immutable.HashMap

case class Statistics(
                       validLines: Int,
                       invalidLines: Int,
                       ratings: HashMap[String, List[Int]],
                       averages: HashMap[String, Double],
                       numberOfRanks: HashMap[String, Int]) {

  def averageRatings(): Vector[(String, Double)] = {
    averages.toVector.sortBy(_._2)
  }

  def productsByNumberOfRatings(): Vector[(String, Int)] = {
    numberOfRanks.toVector.sortBy(_._2)
  }
}

object Statistics extends SprayJsonSupport with DefaultJsonProtocol {
  def empty(): Statistics = Statistics(0, 0, HashMap.empty[String, List[Int]], HashMap.empty[String, Double], HashMap.empty[String, Int])
//
//  implicit object StatisticsSerializer extends RootJsonFormat[Statistics] {
//    def read(value: JsValue) = ???
//
//
//    def write(s: Statistics): JsObject = {
//      val averageRatings = s.averageRatings()
//      val productsByNumberOfRankings = s.productsByNumberOfRatings()
//
//      JsObject(
//        "validLines" -> JsNumber(s.validLines),
//        "invalidLines" -> JsNumber(s.invalidLines),
//        "bestRatedProducts" -> JsArray(averageRatings.takeRight(3).map(r => JsString(r._1))),
//        "worstRatedProducts" -> JsArray(averageRatings.take(3).map(r => JsString(r._1))),
//        "mostRatedProduct" -> productsByNumberOfRankings.lastOption.map(r => JsString(r._1)).getOrElse(JsNull),
//        "lessRatedProduct" -> productsByNumberOfRankings.headOption.map(r => JsString(r._1)).getOrElse(JsNull)
//      )
//    }
//  }
}

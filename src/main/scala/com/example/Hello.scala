// ～簡単！炎上のさせ方講座～
// https://twitter.com/jagarikin/status/894452278463053824

package com.example

import scala.util.Random
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.canvas.Canvas
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.util.Duration
import javafx.event.{ActionEvent, EventHandler}

import com.example.Hello.maxY

object Hello extends JFXApp {
  // 1ブロックのサイズ
  val bsize = 8
  val canvas = new Canvas(512, 512)
  val maxX = (canvas.width.get / bsize).toInt
  val maxY = (canvas.height.get / bsize).toInt
  val rand = new Random

  val rootPane = new Group
  rootPane.children = List(canvas)

  stage = new PrimaryStage {
    title = "Fire!!"
    scene = new Scene(512, 512) {
      root = rootPane
    }
  }

  // 炎上データ
  val data = initialize()


  // 黒で塗り潰す
  val gc = canvas.graphicsContext2D
  reset(Color.Black)

  val timeline = new Timeline(
    new KeyFrame(Duration.millis(100),
    new EventHandler[ActionEvent] {
      override def handle(ae: ActionEvent) = update(data)
    }))
  timeline.setCycleCount(Animation.INDEFINITE)
  timeline.playFromStart()

  // 初期データ作成
  private def initialize(): Array[Array[Int]] = {
    val data = Array.ofDim[Int](maxX, maxY)
    for (x <- 0 until maxX) {
      data(x)(maxY - 1) = rand.nextInt(64)
    }
    calcData(data)
    data
  }

  private def calc(x: Int, y:Int, data: Array[Array[Int]]): Int = {
    var n = 0
    var c = 0
    // 左下
    if (x > 0) {
      n += data(x - 1)(y + 1)
    } else {
      n += data(maxX - 1)(y + 1)
    }
    c += 1
    // 真下
    n += data(x)(y + 1)
    c += 1
    // 右下
    n += data((x + 1) % maxX)(y + 1)
    c += 1
    // 二つ下
    if (y < maxY - 2) {
      n += data(x)(y + 2)
      c += 1
    }
    n / c
  }

  // 平均値を計算
  private def calcData(data: Array[Array[Int]]): Unit = {
    for (y <- (maxY - 2) to 0 by -1) {
      for (x <- 0 until maxX) {
        data(x)(y) = calc(x, y, data)
      }
    }
  }

  private def randomize(n: Int): Int = {
    // ノイズ
    if (rand.nextInt(100) > 50)
      n + 10
    else
      n - 1
  }
  // データ変化
  private def updateData(data: Array[Array[Int]]): Unit = {
    val y = maxY - 1
    for (x <- 0 until maxX) {
      var n = data(x)(y)
      // 増減
      n += 1
      // ノイズ
      n = randomize(n)
      // 最大最小
      if (n > 63) {
        n = 0
      } else if (n < 0) {
        n = 63
      }
      data(x)(y) = n
    }
    calcData(data)
  }

  private def calcColor(n: Int): Color = {
    // 0〜63 -> RGB
    val r = (n.toDouble / 63 * 255).toInt
    val g = (n.toDouble / 127 * 255).toInt
    val b = 0
    Color.rgb(r, g, b)
  }

  private def update(data: Array[Array[Int]]): Unit = {
    updateData(data)
    for (y <- 0 until maxY) {
      for (x <- 0 until maxX) {
        // 書き続ける
        gc.fill = calcColor(data(x)(y))
        gc.fillRect(x * bsize, y * bsize, bsize, bsize)
      }
    }
  }

  private def reset(color: Color): Unit = {
    gc.fill = color
    gc.fillRect(0, 0, canvas.width.get, canvas.height.get)
  }
}

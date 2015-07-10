//package net.dinkla.raytracer.colors
//
//import java.lang.Math._
//
//import net.dinkla.raytracer.hits.Shade
//
///**
// * Created by Dinkla on 09.07.2015.
// */
//
//import RGBColorD.F
//
//class RGBColorD(val red: F, val green: F, val blue: F) extends Color {
//
//  override def getColor(sr: Shade): RGBColorD = ???
//
//  override def plus(v: Color): RGBColorD = ???
//
//  override def clampToColor(): RGBColorD = {
//    if (red > 1 || green > 1 || blue > 1) {
//      return RGBColorD.CLAMP_COLOR
//    }
//    else {
//      return this
//    }
//  }
//
//  override def maxToOne(): RGBColorD = {
//    val maxValue: Double = max(red, max(green, blue))
//    if (maxValue > 1) {
//      return this.mult(1 / maxValue)
//    }
//    else {
//      return this
//    }
//  }
//
//  override def pow(s: F): RGBColorD = ???
//
//  override def mult(v: Color): RGBColorD = ???
//
//  override def mult(s: F): RGBColorD = new RGBColorD(red * s, green * s, blue * s)
//
//  override def asBytes(): RGBBytes = {
//    val r: Byte = (red * 255).toByte
//    val g: Byte = (green * 255).toByte
//    val b: Byte = (blue * 255).toByte
//    return new RGBBytes(r, g, b)
//  }
//
//  override def asInt(): Int = {
//    val r: Int = (red * 255).toInt
//    val g: Int = (green * 255).toInt
//    val b: Int = (blue * 255).toInt
//    return r << 16 | g << 8 | b
//  }
//
//  override def createFromInt(rgb: Int): RGBColorD = {
//    val rx: Int = (rgb & 0x00ff0000)
//    val gx: Int = (rgb & 0x0000ff00)
//    val bx: Int = (rgb & 0x000000ff)
//    val r: F = ((rgb & 0x00ff0000) >> 16) / 255.0
//    val g: F = ((rgb & 0x0000ff00) >> 8) / 255.0
//    val b: F = (rgb & 0x000000ff) / 255.0
//    return new RGBColorD(r, g, b)
//  }
//
//}
//
//object RGBColorD {
//
//  type F = Double
//
//  def RGBColorD[F](red: F, green: F, blue: F) = new RGBColorD(red, green, blue)
//
//  val BLACK = RGBColorD[Double](0, 0, 0)
//  val WHITE = RGBColorD[Double](1, 1, 1)
//  val RED   = RGBColorD[Double](1, 0, 0)
//  val GREEN = RGBColorD[Double](0, 1, 0)
//  val BLUE  = RGBColorD[Double](0, 0, 1)
//  val CLAMP_COLOR = RGBColorD[Double](1, 0, 0)
//
//}
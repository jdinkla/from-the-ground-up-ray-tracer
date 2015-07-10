/*
package net.dinkla.raytracer.colors

import net.dinkla.raytracer.hits.Shade

/**
 * Created by Dinkla on 09.07.2015.
 */
trait Color {

  type F

  def getColor(sr: Shade): Color

  def plus(v: Color): Color

  def mult(v: Color): Color

  def mult(s: F): Color

  def pow(s: F): Color

  def asInt: Int

  def asBytes: RGBBytes

  def clampToColor: Color

  def maxToOne: Color

  def getBlack: Color = Color.black

  def getWhite: Color = Color.white

  def getErrorColor: Color = Color.error

  def createFromInt(rgb: Int): Color

}

object Color {

  var black: Color = null
  var white: Color = null
  var error: Color = null

}*/

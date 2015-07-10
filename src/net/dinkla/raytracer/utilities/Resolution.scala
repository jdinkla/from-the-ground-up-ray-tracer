package net.dinkla.raytracer.utilities

/**
 * Created by Dinkla on 09.07.2015.
 */
class Resolution(val hres: Int, val vres: Int) {

  def this(vres: Int) = this((vres / 9.0 * 16).toInt, vres)

  override def toString: String = s"($hres,$vres)"

}

object Resolution {

  val RESOLUTION_32: Resolution = new Resolution(32)
  val RESOLUTION_320: Resolution = new Resolution(320)
  val RESOLUTION_480: Resolution = new Resolution(480)
  val RESOLUTION_720: Resolution = new Resolution(720)
  val RESOLUTION_1080: Resolution = new Resolution(1080)
  val RESOLUTION_1440: Resolution = new Resolution(1440)
  val RESOLUTION_2160: Resolution = new Resolution(2160)

}


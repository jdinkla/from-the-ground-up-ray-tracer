package net.dinkla.raytracer.ui.swing

import net.dinkla.raytracer.utilities.AppProperties

internal val appWidth = AppProperties.getAsInteger("display.width")
internal val appHeight = AppProperties.getAsInteger("display.height")

internal val width = AppProperties.getAsInteger("render.resolution.width")
internal val height = AppProperties.getAsInteger("render.resolution.height")

internal val examplesDirectory = AppProperties["examples.directory"] as String

internal val appTitle = AppProperties["app.title"] as String

internal val informationTitle = AppProperties["information.title"] as String
internal val informationHeader = AppProperties["information.headerText"] as String
internal val informationContext = AppProperties["information.contentText"] as String

internal val confirmationTitle = AppProperties["confirmation.title"] as String
internal val confirmationHeader = AppProperties["confirmation.headerText"] as String

internal val pngTitle = AppProperties["png.title"] as String
internal val pngMessage = AppProperties["png.message"] as String

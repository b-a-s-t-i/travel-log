package at.droelf.backend.service

import java.io.File
import at.droelf.backend.storage.{DBStorageService, FileStorageService}
import at.droelf.gui.entities.{GuiImageLocation, GuiImage}
import models.Image
import org.joda.time.{DateTimeZone, LocalDate, DateTime}
import play.api.{Logger, Play}
import java.nio.file.{Path, Files, Paths}
import java.util.UUID

class ImageService(fileStorageService: FileStorageService, dbStorage: DBStorageService, timeToLocationService: TimeToLocationService) {


  def saveImage(image: File, date: DateTime, name: String){
      val path = fileStorageService.saveImageToDisk(image, date.toString, name)

    val localdatetime = date.toLocalDateTime
    val timezone = date.getChronology.getZone

    dbStorage.insertImage(Image(name,localdatetime, timezone,path.getFileName.toString))
  }

  def getImagesForDate(date: LocalDate): Seq[GuiImage] = {
    dbStorage.getImagesForLocalDate(date).map(img => GuiImage(img,timeToLocationService.getLocationForDateTime(img.dateTime.toDateTime(img.dateTimeZone))))
  }

  def getImageFile(name: String): File = {
    fileStorageService.getImageFromDisk(name) match{
      case Some(x) => x
      case None => new File("public/images/favicon.png")
    }
  }

}
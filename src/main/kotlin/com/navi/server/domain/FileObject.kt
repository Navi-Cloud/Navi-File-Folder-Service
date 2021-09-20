package com.navi.server.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

class FileObject (
    val userEmail: String = "",
    val category: String = Category.Doc.toString(),
    val fileName: String = "", // Full file path of this file/folder
    val currFolderName: String = "", // Full file path of current folder (this file/folder belongs to)
    val lastModifiedTime: String = Date().toString(),

    @JsonProperty(value="isFile")
    val isFile: Boolean = true, // File or Folder

    @JsonProperty(value="isFavorites")
    val isFavorites: Boolean = false,

    @JsonProperty(value="isTrash")
    val isTrash: Boolean = false
)

enum class Category {
    Image, Video, Audio, Doc
}
package com.navi.server.domain

import java.util.Date

class FileObject (
    val userEmail: String = "",
    val category: Category = Category.Etc,
    val fileName: String = "", // Full file path of this file/folder
    val currFolderName: String = "", // Full file path of current folder (this file/folder belongs to)
    val lastModifiedTime: Date = Date(),
    val isFile: Boolean = true, // File or Folder
    val isFavorites: Boolean = false,
    val isTrash: Boolean = false
)

enum class Category {
    Image, Video, Audio, Doc, Etc
}
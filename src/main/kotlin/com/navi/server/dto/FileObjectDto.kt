package com.navi.server.dto

import com.navi.server.domain.Category
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class FileObjectDto (
    val userEmail: String = "",
    val category: String = Category.Doc.toString(),
    val fileName: String = "", // Full file path of this file/folder
    val currFolderName: String = "", // Full file path of current folder (this file/folder belongs to)
    val lastModifiedTime: String = Date().toString(),
    val isFile: Boolean = true, // File or Folder
    val isFavorites: Boolean = false,
    val isTrash: Boolean = false
)
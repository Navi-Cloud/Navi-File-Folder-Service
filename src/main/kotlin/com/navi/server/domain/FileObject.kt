package com.navi.server.domain

import com.navi.server.dto.FileObjectDto
import java.util.Date

class FileObject (
    val userEmail: String = "",
    val category: String = Category.Doc.toString(),
    val fileName: String = "", // Full file path of this file/folder
    val currFolderName: String = "", // Full file path of current folder (this file/folder belongs to)
    val lastModifiedTime: String = Date().toString(),
    val isFile: Boolean = true, // File or Folder
    val isFavorites: Boolean = false,
    val isTrash: Boolean = false
) {
    fun toFileObjectDTO(): FileObjectDto {
        return FileObjectDto(
            userEmail = userEmail,
            category = category,
            fileName = fileName,
            currFolderName = currFolderName,
            lastModifiedTime = lastModifiedTime,
            isFile = isFile,
            isFavorites = isFavorites,
            isTrash = isTrash
        )
    }
}

enum class Category {
    Image, Video, Audio, Doc
}
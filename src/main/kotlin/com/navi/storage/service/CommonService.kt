package com.navi.storage.service

import com.navi.storage.domain.GridFSRepository
import org.springframework.stereotype.Service

@Service
class CommonService (
    private val gridFSRepository: GridFSRepository
) {
    fun getPullPath(prevFolderName: String, targetName: String): String {
        // Handle Root or else - case
        return if (prevFolderName == "/") {
            "/${targetName}"
        } else {
            "$prevFolderName/$targetName"
        }
    }

    fun isExisting(userEmail: String, targetPath: String, isFile: Boolean): Boolean {
        runCatching {
            gridFSRepository.getMetadataSpecific(userEmail, targetPath, isFile)
        }.getOrElse {
            return false
        }
        return true
    }
}
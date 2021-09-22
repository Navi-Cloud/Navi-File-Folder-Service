package com.navi.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.navi.server.domain.GridFSRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class FolderDeletionService(
    private val gridFSRepository: GridFSRepository
) {

    private val objectMapper: ObjectMapper = ObjectMapper()

    /**
     * Message should contains db-exists user email address.
     */
    @KafkaListener(topics = ["naviDeletionRequest"], groupId = "KTFDS")
    fun handleNaviDeletionRequest(message: String) {
        gridFSRepository.removeAllStorageByUserEmail(message)
    }
}
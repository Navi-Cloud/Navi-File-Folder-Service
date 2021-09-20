package com.navi.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.navi.server.domain.FileObject
import com.navi.server.domain.GridFSRepository
import io.github.navi_cloud.shared.CommonCommunication
import io.github.navi_cloud.shared.storage.FolderGrpc
import io.github.navi_cloud.shared.storage.StorageMessage
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.ByteArrayInputStream

@RunWith(SpringRunner::class)
@SpringBootTest
class FolderServiceTest {

    @Autowired
    private lateinit var gridFSRepository: GridFSRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    lateinit var folderBlockingStub: FolderGrpc.FolderBlockingStub

    val testUserEmail: String = "je@navi.com"

    @Before
    fun buildUp(){
        val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
            .usePlaintext()
            .build()
        folderBlockingStub = FolderGrpc.newBlockingStub(channel)
    }

    @Test
    fun is_createRootFolder_works_well(){
        // Perform
        val request: StorageMessage.CreateRootFolderRequest = StorageMessage.CreateRootFolderRequest.newBuilder()
            .setUserEmail(testUserEmail)
            .build()
        val response: CommonCommunication.Result = folderBlockingStub.createRootFolder(request)

        // Assert
        assertThat(response.resultType).isEqualTo(CommonCommunication.ResultType.SUCCESS)
    }

    @Test
    fun is_findInsideFiles_works_well(){
        val targetFolder: String = "/"
        val insideFile: FileObject = FileObject(
            userEmail = testUserEmail, fileName = "testing", currFolderName = targetFolder
        )
        gridFSRepository.saveToGridFS(fileObject = insideFile, ByteArrayInputStream("".toByteArray()))

        // Perform
        val request: StorageMessage.FindInsideFilesRequest = StorageMessage.FindInsideFilesRequest.newBuilder()
            .setUserEmail(testUserEmail)
            .setTargetFolder(targetFolder)
            .build()
        val response: CommonCommunication.Result = folderBlockingStub.findInsideFiles(request)

        // Assert
        assertThat(response.resultType).isEqualTo(CommonCommunication.ResultType.SUCCESS)
        val fileList: List<FileObject> = objectMapper.readValue(response.`object`)
        assertThat(fileList.size).isEqualTo(1)
        assertThat(fileList[0].userEmail).isEqualTo(testUserEmail)
        assertThat(fileList[0].currFolderName).isEqualTo(targetFolder)
    }
}
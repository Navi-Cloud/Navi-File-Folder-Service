package com.navi.server.service

import com.navi.server.domain.FileObject
import com.navi.server.domain.GridFSRepository
import com.navi.server.dto.FileObjectDto
import io.github.navi_cloud.shared.CommonCommunication
import io.github.navi_cloud.shared.storage.FolderGrpc
import io.github.navi_cloud.shared.storage.StorageMessage
import io.grpc.stub.StreamObserver
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import java.io.ByteArrayInputStream
import java.util.*

@GrpcService
class FolderService: FolderGrpc.FolderImplBase() {

    @Autowired
    private lateinit var gridFSRepository: GridFSRepository

    override fun createRootFolder(createRootFolderRequest: StorageMessage.CreateRootFolderRequest,
                         responseObserver: StreamObserver<CommonCommunication.Result>)
    {
        gridFSRepository.saveToGridFS(
            fileObject = FileObject(
                userEmail = createRootFolderRequest.userEmail,
                category = "",
                fileName = "/",
                currFolderName = "",
                lastModifiedTime = Date().toString(),
                isFile = false,
                isFavorites = false,
                isTrash = false
            ),
            inputStream = ByteArrayInputStream("".toByteArray())
        )

        val reply: CommonCommunication.Result = CommonCommunication.Result.newBuilder()
            .setResultType(CommonCommunication.ResultType.SUCCESS)
            .build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }

    override fun findInsideFiles(findInsideFilesRequest: StorageMessage.FindInsideFilesRequest,
                        responseObserver: StreamObserver<CommonCommunication.Result>)
    {
        val filesList: List<FileObject> = gridFSRepository.getMetadataInsideFolder(
            userEmail = findInsideFilesRequest.userEmail,
            targetFolderName = findInsideFilesRequest.targetFolder
        )

        val responseFileList: List<FileObjectDto> = convertToFileObjectDTO(filesList)
        val reply: CommonCommunication.Result = CommonCommunication.Result.newBuilder()
            .setResultType(CommonCommunication.ResultType.SUCCESS)
            .setObject(Json.encodeToString(responseFileList))
            .build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }

    private fun convertToFileObjectDTO(filesList: List<FileObject>): List<FileObjectDto> {
        var responseList: MutableList<FileObjectDto> = mutableListOf()
        filesList.forEach {
            responseList.add(it.toFileObjectDTO())
        }
        return responseList
    }
}
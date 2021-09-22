package com.navi.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.navi.server.domain.Category
import com.navi.server.domain.FileObject
import com.navi.server.domain.GridFSRepository
import io.github.navi_cloud.shared.CommonCommunication
import io.github.navi_cloud.shared.storage.FolderGrpc
import io.github.navi_cloud.shared.storage.StorageMessage
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import java.io.ByteArrayInputStream
import java.util.*

@GrpcService
class FolderService(
    private val gridFSRepository: GridFSRepository
): FolderGrpc.FolderImplBase() {
                
    @Autowired
    private lateinit var objectMapper: ObjectMapper // jacksonObjectMapper

    override fun createRootFolder(
        request: StorageMessage.CreateRootFolderRequest,
        responseObserver: StreamObserver<CommonCommunication.Result>
    ) {
        // Save to GridFS Repository
        gridFSRepository.saveToGridFS(
            fileObject = FileObject(
                userEmail = request.userEmail,
                category = Category.Etc,
                fileName = "/",
                currFolderName = "",
                lastModifiedTime = Date(),
                isFile = false,
                isFavorites = false,
                isTrash = false
            ),
            inputStream = ByteArrayInputStream("".toByteArray())
        )

        // Setup Reply
        val reply: CommonCommunication.Result = CommonCommunication.Result.newBuilder()
            .setResultType(CommonCommunication.ResultType.SUCCESS)
            .build()

        // Continue to serve.
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }

    override fun findInsideFiles(
        request: StorageMessage.FindInsideFilesRequest,
        responseObserver: StreamObserver<CommonCommunication.Result>
    ) {
        // Get File List
        val filesList: List<FileObject> = gridFSRepository.getMetadataInsideFolder(
            userEmail = request.userEmail,
            targetFolderName = request.targetFolder
        )

        // Create reply
        val reply: CommonCommunication.Result = CommonCommunication.Result.newBuilder()
            .setResultType(CommonCommunication.ResultType.SUCCESS)
            .setObject(objectMapper.writeValueAsString(filesList))
            .build()

        // Continue communication
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}
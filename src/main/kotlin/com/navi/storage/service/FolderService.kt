package com.navi.storage.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.navi.storage.domain.Category
import com.navi.storage.domain.FileObject
import com.navi.storage.domain.GridFSRepository
import io.github.navi_cloud.shared.CommonCommunication
import io.github.navi_cloud.shared.storage.FolderGrpc
import io.github.navi_cloud.shared.storage.StorageMessage
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import java.io.ByteArrayInputStream
import java.util.*

@GrpcService
class FolderService(
    private val gridFSRepository: GridFSRepository
): FolderGrpc.FolderImplBase() {
                
    @Autowired
    private lateinit var objectMapper: ObjectMapper // jacksonObjectMapper

    @Autowired
    private lateinit var commonService: CommonService

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

    override fun createNewFolder(
        request: StorageMessage.CreateNewFolderRequest?,
        responseObserver: StreamObserver<CommonCommunication.Result>
    ) {
        var reply: CommonCommunication.Result? = null

        request?.apply {
            val pullPathOfNewFolder: String = commonService.getPullPath(parentFolderName, newFolderName)

            // Check whether folder exists on DB
            val isExisting = commonService.isExisting(userEmail, pullPathOfNewFolder, false)

            if(!isExisting) {
                // Create new folder (upload to DB)
                val fileObject: FileObject = FileObject(
                    userEmail = userEmail,
                    category = Category.Etc,
                    fileName = pullPathOfNewFolder,
                    currFolderName = parentFolderName,
                    lastModifiedTime = Date(),
                    isFile = false,
                    isFavorites = false,
                    isTrash = false
                )
                gridFSRepository.saveToGridFS(fileObject, ByteArrayInputStream("".toByteArray()))

                // [Success] Create reply
                reply = CommonCommunication.Result.newBuilder()
                    .setResultType(CommonCommunication.ResultType.SUCCESS)
                    .build()
            } else {
                // [Duplicate Error] Create reply
                reply = CommonCommunication.Result.newBuilder()
                    .setResultType(CommonCommunication.ResultType.DUPLICATE)
                    .setMessage("Folder name $newFolderName already exists!")
                    .build()
                return@apply
            }
        }

        // Continue communication
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}
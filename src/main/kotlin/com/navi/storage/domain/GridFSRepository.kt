package com.navi.storage.domain

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.client.gridfs.model.GridFSFile
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Repository
import java.io.InputStream

@Repository
class GridFSRepository(
    private val gridFsTemplate: GridFsTemplate,
    private val gridFsOperations: GridFsOperations
) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun saveToGridFS(fileObject: FileObject, inputStream: InputStream) {
        val dbMetaData: DBObject = convertFileObjectToMetaData(fileObject)

        val id: ObjectId = gridFsTemplate.store(
            inputStream, fileObject.fileName, dbMetaData
        )
    }

    // For querying specific file using full file path
    fun getMetadataSpecific(userEmail: String, targetFileName: String, isFile: Boolean): FileObject? {
        val query: Query  = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.${FileObject::userEmail.name}").`is`(userEmail),
                    Criteria.where("metadata.${FileObject::fileName.name}").`is`(targetFileName),
                    Criteria.where("metadata.${FileObject::isFile.name}").`is`(isFile)
                )
            )
        }
        val gridFSFile: GridFSFile = gridFsTemplate.findOne(query)
        return convertMetaDataToFileObject(gridFSFile.metadata ?: return null)
    }
    
    fun removeAllStorageByUserEmail(userEmail: String) {
        val removeQuery = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.userEmail").`is`(userEmail)
                )
            )
        }
        gridFsTemplate.delete(removeQuery)
    }

    fun removeFilesInsideFolder(userEmail: String, targetFolderName: String): List<FileObject> {
        val query = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.${FileObject::userEmail.name}").`is`(userEmail),
                    Criteria.where("metadata.${FileObject::currFolderName.name}").`is`(targetFolderName),
                )
            )
        }
        val fileList: List<FileObject> = gridFsTemplate.find(query).map {
            convertMetaDataToFileObject(it.metadata)
        }.toList()

        gridFsTemplate.delete(query)

        // return removed FileObject list
        return fileList
    }

    fun removeOne(userEmail: String, targetFName: String, isFile: Boolean) {
        val removeOneQuery = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.${FileObject::userEmail.name}").`is`(userEmail),
                    Criteria.where("metadata.${FileObject::fileName.name}").`is`(targetFName),
                    Criteria.where("metadata.${FileObject::isFile.name}").`is`(isFile)
                )
            )
        }
        gridFsTemplate.delete(removeOneQuery)
    }

//    fun getRootFolder(userEmail: String): FileObject {
//        val query: Query = Query().apply {
//            addCriteria(
//                Criteria().andOperator(
//                    Criteria.where("metadata.userEmail").`is`(userEmail),
//                    Criteria.where("metadata.fileName").`is`("/"),
//                    Criteria.where("metadata.isFile").`is`(false)
//                )
//            )
//        }
//
//        val gridFSFile: GridFSFile = gridFsTemplate.findOne(query)
//
//        return convertMetaDataToFileObject(gridFSFile.metadata)
//    }

    // For querying inside-folder file
    fun getMetadataInsideFolder(userEmail: String, targetFolderName: String): List<FileObject> {
        val query: Query = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.userEmail").`is`(userEmail),
                    Criteria.where("metadata.currFolderName").`is`(targetFolderName)
                )
            )
        }

        return gridFsTemplate.find(query).map {
            convertMetaDataToFileObject(it.metadata)
        }.toList()
    }

//    fun getFullTargetStream(userEmail: String, fileObject: FileObject): InputStream {
//        val query: Query = Query().apply {
//            addCriteria(
//                Criteria().andOperator(
//                    Criteria.where("metadata.userEmail").`is`(userEmail),
//                    Criteria.where("metadata.fileName").`is`(fileObject.fileName)
//                )
//            )
//        }
//
//        val file: GridFSFile = gridFsTemplate.findOne(query)
//        return gridFsOperations.getResource(file).inputStream
//    }

    // Reflection Helper
    private fun convertFileObjectToMetaData(fileObject: FileObject): BasicDBObject {
        val jsonString = objectMapper.writeValueAsString(fileObject)

        return BasicDBObject.parse(jsonString)
    }

    private fun convertMetaDataToFileObject(metadata: Document): FileObject {
        return objectMapper.readValue(metadata.toJson(
            JsonWriterSettings
            .builder()
            .outputMode(JsonMode.RELAXED)
            .build()))
    }
}
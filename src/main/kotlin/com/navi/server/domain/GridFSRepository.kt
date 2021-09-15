package com.navi.server.domain

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.client.gridfs.model.GridFSFile
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Repository
import java.io.InputStream
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberProperties

@Repository
class GridFSRepository(
    private val gridFsTemplate: GridFsTemplate,
    private val gridFsOperations: GridFsOperations
) {
    fun saveToGridFS(fileObject: FileObject, inputStream: InputStream) {
        val dbMetaData: DBObject = convertFileObjectToMetaData(fileObject)

        val id: ObjectId = gridFsTemplate.store(
            inputStream, fileObject.fileName, dbMetaData
        )
    }

    // For querying specific file using full file path
    fun getMetadataSpecific(userEmail: String, targetFileName: String): FileObject {
        val query: Query  = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.userEmail").`is`(userEmail),
                    Criteria.where("metadata.fileName").`is`(targetFileName)
                )
            )
        }
        val gridFSFile: GridFSFile = gridFsTemplate.findOne(query)
        return convertMetaDataToFileObject(gridFSFile.metadata)
    }

    fun getRootToken(userEmail: String): FileObject {
        val query: Query = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.userEmail").`is`(userEmail),
                    Criteria.where("metadata.fileName").`is`("/"),
                    Criteria.where("metadata.isFile").`is`(false)
                )
            )
        }

        val gridFSFile: GridFSFile = gridFsTemplate.findOne(query)

        return convertMetaDataToFileObject(gridFSFile.metadata)
    }

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

    fun getFullTargetStream(userEmail: String, fileObject: FileObject): InputStream {
        val query: Query = Query().apply {
            addCriteria(
                Criteria().andOperator(
                    Criteria.where("metadata.userEmail").`is`(userEmail),
                    Criteria.where("metadata.fileName").`is`(fileObject.fileName)
                )
            )
        }

        val file: GridFSFile = gridFsTemplate.findOne(query)
        return gridFsOperations.getResource(file).inputStream
    }

    // Reflection Helper
    private fun convertFileObjectToMetaData(fileObject: FileObject): DBObject {
        val dbObject: DBObject = BasicDBObject()
        FileObject::class.memberProperties.forEach {
            dbObject.put(it.name, it.get(fileObject))
        }

        return dbObject
    }

    private fun convertMetaDataToFileObject(metadata: Document?): FileObject {
        val defaultConstructor: KFunction<FileObject> = FileObject::class.constructors.first()
        val argument = defaultConstructor
            .parameters
            .map {
                it to (metadata?.get(it.name) ?: "")
            }
            .toMap()

        return defaultConstructor.callBy(argument)
    }
}
package com.navi.storage.domain

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.io.ByteArrayInputStream
import org.assertj.core.api.Assertions.assertThat
import org.springframework.data.mongodb.core.query.Criteria
import kotlin.test.assertNotNull

@RunWith(SpringRunner::class)
@SpringBootTest
class GridFSRepositoryTest {

    @Autowired
    private lateinit var gridFSRepository: GridFSRepository

    @Autowired
    private lateinit var gridFsTemplate: GridFsTemplate

    @After
    @Before
    fun clearAll() {
        gridFsTemplate.delete(Query())
    }

    @Test
    fun is_storeFile_and_getMetadataSpecific_works_well() {
        val testUserEmail: String = "je@navi.com"
        val testFileName: String = "testFile"
        val fileObject: FileObject = FileObject(
            userEmail = testUserEmail,
            fileName = testFileName,
            isFile = true
        )

        // Perform
        gridFSRepository.saveToGridFS(
            fileObject = fileObject,
            inputStream = ByteArrayInputStream("".toByteArray())
        )
        val resultFileObject: FileObject? = gridFSRepository.getMetadataSpecific(testUserEmail, testFileName, true)

        // Assert
        assertNotNull(resultFileObject)
        assertThat(resultFileObject.userEmail).isEqualTo(testUserEmail)
        assertThat(resultFileObject.fileName).isEqualTo(testFileName)
    }

    @Test
    fun is_removeAllStorageByUserEmail_works_well() {
        val fileObject = FileObject(
            userEmail = "kangdroid@navi.com",
            fileName = "testFileName"
        )
        gridFSRepository.saveToGridFS(fileObject, ByteArrayInputStream("".toByteArray()))
        val countBefore = gridFsTemplate.find(Query.query(Criteria.where("metadata.userEmail").`is`(fileObject.userEmail))).count()

        // Do
        gridFSRepository.removeAllStorageByUserEmail(fileObject.userEmail)

        // Check
        gridFsTemplate.find(Query.query(Criteria.where("metadata.userEmail").`is`(fileObject.userEmail))).also {
            assertThat(it.count()).isEqualTo(countBefore-1)
        }
    }

}
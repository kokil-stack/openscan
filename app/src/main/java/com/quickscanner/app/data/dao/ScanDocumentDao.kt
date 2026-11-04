package com.quickscanner.app.data.dao

import androidx.room.*
import com.quickscanner.app.data.model.ScanDocument
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDocumentDao {
    @Query("SELECT * FROM scanned_documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<ScanDocument>>

    @Query("SELECT * FROM scanned_documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): ScanDocument?

    @Query("SELECT * FROM scanned_documents WHERE title LIKE '%' || :query || '%' OR ocrText LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchDocuments(query: String): Flow<List<ScanDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: ScanDocument): Long

    @Update
    suspend fun updateDocument(doc: ScanDocument)

    @Delete
    suspend fun deleteDocument(doc: ScanDocument)

    @Query("DELETE FROM scanned_documents WHERE id = :id")
    suspend fun deleteById(id: Long)
}


package com.manoj.base.presentation.service.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MqMessageDao {

    @get:Query("SELECT * FROM MQMessageEntity")
    val all: List<MqMessageEntity>

    @Query("SELECT * FROM MQMessageEntity WHERE clientHandle = :clientHandle ORDER BY timestamp ASC")
    fun allArrived(clientHandle: String): List<MqMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mqMessageEntity: MqMessageEntity): Long

    @Update
    fun updateAll(vararg mqMessageEntity: MqMessageEntity)

    @Delete
    fun delete(mqMessageEntity: MqMessageEntity)

    @Query("DELETE FROM MqMessageEntity WHERE clientHandle = :clientHandle AND messageId = :id")
    fun deleteId(clientHandle: String, id: String): Int

    @Query("DELETE FROM MqMessageEntity WHERE clientHandle = :clientHandle")
    fun deleteClientHandle(clientHandle: String): Int

}
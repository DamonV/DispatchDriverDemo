
package com.driver.reliantdispatch.data

import androidx.paging.DataSource
import androidx.room.*
import com.driver.reliantdispatch.data.secondary.DBConverters
import com.driver.reliantdispatch.domain.entities.*
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined

@Database(entities = [Ebol::class, Company::class, Vehicle::class, Inspection::class,
    InspectImage::class, DamageMark::class, Attachment::class], version = 1, exportSchema = false)
@TypeConverters(DBConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ebolsDao(): EbolsDao
    abstract fun vehiclesDao(): VehiclesDao
    abstract fun companiesDao(): CompaniesDao
    abstract fun inspectionsDao(): InspectionsDao
    abstract fun inspectImagesDao(): InspectImagesDao
    abstract fun attachmentsDao(): AttachmentsDao
    abstract fun damageMarksDao(): DamageMarksDao


    @Dao
    abstract class EbolsDao {
        @Transaction
        @Query("SELECT * FROM ebols WHERE user=:user")
        abstract suspend fun getAll(user: String): MutableList<EbolJoined>

        @Transaction
        @Query("SELECT * FROM ebols WHERE user=:user")
        abstract fun getAllPaged(user: String): DataSource.Factory<Int, EbolJoined>

        @Transaction
        @Query("SELECT COUNT(*) FROM ebols WHERE user=:user")
        abstract suspend fun getCount(user: String): Int

        @Transaction
        @Query("SELECT * FROM ebols WHERE id=:id AND user=:user")
        abstract suspend fun get(id: Int, user: String): EbolJoined

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract suspend fun insert(item: Ebol): Long

        @Update
        abstract suspend fun update(item: Ebol)

        suspend fun save(item: Ebol): Long =
            if (item.id <= 0) insert(item)
            else {
                update(item)
                item.id
            }

        @Delete
        abstract suspend fun delete(item: Ebol): Int
    }

    @Dao
    interface VehiclesDao {
        /*@Query("SELECT * FROM vehicles")
        suspend fun getAll(): MutableList<Vehicle>

        @Query("SELECT * FROM vehicles WHERE id=:id")
        suspend fun get(id: Int): Vehicle*/

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertList(list: List<Vehicle>): List<Long>

        /*@Delete
        suspend fun deleteList(list: List<Vehicle>)*/

        @Query("DELETE FROM vehicles WHERE ebolId = :ebolId")
        suspend fun deleteByEbolId(ebolId: Long)

        /*@Delete
        suspend fun delete(item: Ebol)*/
    }

    @Dao
    abstract class CompaniesDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract suspend fun insert(item: Company): Long

        @Update
        abstract suspend fun update(item: Company)

        suspend fun save(item: Company): Long =
            if (item.id <= 0) insert(item)
            else {
                update(item)
                item.id
            }

        @Delete
        abstract suspend fun delete(item: Company)
    }

    @Dao
    interface InspectionsDao {
        @Query("SELECT * FROM inspections " +
                "INNER JOIN vehicles " +
                "ON (inspections.id = vehicles.pickupInspectionId " +
                "OR inspections.id = vehicles.deliveryInspectionId) " +
                "AND vehicles.ebolId = :ebolId")
        suspend fun getByEbolId(ebolId: Long): List<Inspection>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(item: Inspection): Long

        @Update
        suspend fun update(item: Inspection)

        /*suspend fun save(item: Inspection): Long =
            if (item.id <= 0) insert(item)
            else {
                update(item)
                item.id
            }*/

        @Delete
        suspend fun delete(item: Inspection)

        @Delete
        suspend fun deleteList(list: List<Inspection>)
    }

    @Dao
    interface InspectImagesDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertList(list: List<InspectImage>): List<Long>

        @Delete
        suspend fun deleteList(list: List<InspectImage>)

        @Query("DELETE FROM inspect_images WHERE inspectionId = :inspectionId")
        suspend fun deleteByInspectionId(inspectionId: Long)
    }

    @Dao
    interface AttachmentsDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertList(list: List<Attachment>): List<Long>

        @Delete
        suspend fun deleteList(list: List<Attachment>)

        @Query("DELETE FROM attachments WHERE inspectionId = :inspectionId")
        suspend fun deleteByInspectionId(inspectionId: Long)
    }

    @Dao
    interface DamageMarksDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertList(list: List<DamageMark>): List<Long>

        @Delete
        suspend fun deleteList(list: List<DamageMark>)

        @Query("DELETE FROM damage_marks WHERE inspectImageId = :inspectImageId")
        suspend fun deleteByInspectImageId(inspectImageId: Long)
    }

}
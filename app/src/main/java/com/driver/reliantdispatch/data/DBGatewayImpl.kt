package com.driver.reliantdispatch.data

import androidx.paging.DataSource
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.DbGateway
import com.driver.reliantdispatch.domain.entities.InspectImage
import com.driver.reliantdispatch.domain.entities.Vehicle
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.domain.entities.joined.InspectionJoined
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DBGatewayImpl: DbGateway{
    private val appDB: AppDatabase = App.component.getAppDatabase()
    private val global = App.component.getGlobal()

    override suspend fun getEbols(): MutableList<EbolJoined> = global.mUser?.let{ appDB.ebolsDao().getAll(it) } ?: mutableListOf()

    override fun getEbolsPaged(): DataSource.Factory<Int, EbolJoined>? = global.mUser?.let{ appDB.ebolsDao().getAllPaged(it) }


    override suspend fun getCount(): Int? = global.mUser?.let{ appDB.ebolsDao().getCount(it) }

    override suspend fun saveEbol(ebolJoined: EbolJoined): Long = withContext(Dispatchers.IO) {
        ebolJoined.ebol?.let { ebol ->
            if (ebol.id > 0) with (appDB.inspectionsDao()) {    //if save has already created, delete vehicles with inspections
                deleteList(getByEbolId(ebol.id))
                appDB.vehiclesDao().deleteByEbolId(ebol.id)
            }

            ebol.shipperCompanyId = ebolJoined.shipperComp?.let{ appDB.companiesDao().save(it)}
            ebol.pickUpCompanyId = ebolJoined.pickUpComp?.let{ appDB.companiesDao().save(it)}
            ebol.deliveryCompanyId = ebolJoined.deliveryComp?.let{ appDB.companiesDao().save(it)}
            val ebolId = appDB.ebolsDao().save(ebol)

            val vehiclesList = mutableListOf<Vehicle>()
            for (vehicleJoined in ebolJoined.vehiclesList) vehicleJoined.vehicle?.let{ vehicle ->

                vehicle.pickupInspectionId = vehicleJoined.pickupInspect?.inspection?.let{ appDB.inspectionsDao().insert(it)}
                vehicle.deliveryInspectionId = vehicleJoined.deliveryInspect?.inspection?.let{ appDB.inspectionsDao().insert(it)}

                saveInspectionRelated(vehicle.pickupInspectionId, vehicleJoined.pickupInspect)
                saveInspectionRelated(vehicle.deliveryInspectionId, vehicleJoined.deliveryInspect)

                vehicle.ebolId = ebolId
                vehiclesList.add(vehicle)
            }
            appDB.vehiclesDao().insertList(vehiclesList)
            return@withContext ebolId
        }
        return@withContext -1L
    }

    private suspend fun saveInspectionRelated(pickupInspectionId: Long?, inspection: InspectionJoined?){
        pickupInspectionId?.let{ pickupInspectionId ->
            inspection?.attachmentList?.let { list ->
                val idsList = appDB.attachmentsDao().insertList(list.map {      //insert list of attachments
                    it.inspectionId = pickupInspectionId
                    it
                })
                for ((i,attachment) in list.withIndex()) attachment.id = idsList[i]         //refresh ids
            }
            inspection?.imagesList?.let { list ->
                val inspectImagesList = mutableListOf<InspectImage>()
                val inspectImageJoinedList = mutableListOf<InspectImageJoined>()    //list with non-null item
                for(item in list) item.inspectImage?.let{
                    it.inspectionId = pickupInspectionId                        //fill id of the inspection
                    inspectImagesList.add(it)
                    inspectImageJoinedList.add(item)
                }
                val idsList = appDB.inspectImagesDao().insertList(inspectImagesList)       //insert list of inspect images
                for ((i,inspectImageJoined) in inspectImageJoinedList.withIndex()){
                    inspectImageJoined.inspectImage?.id = idsList[i]                      //fill id for inserted inspect image
                    val idsDamageList = appDB.damageMarksDao().insertList(inspectImageJoined.damagesList.map {
                        //save list of damage with new inspect image id
                        it.inspectImageId = idsList[i]
                        it
                    })
                    for ((j,damageMark) in inspectImageJoined.damagesList.withIndex()) damageMark.id = idsDamageList[j]     //refresh ids
                }
            }
        }
    }

    override suspend fun deleteEbol(ebolJoined: EbolJoined): Boolean = withContext(Dispatchers.IO) {
        ebolJoined.ebol?.let { ebol ->

            //val vehiclesList = mutableListOf<Vehicle>()
            for (vehicleJoined in ebolJoined.vehiclesList) {
                vehicleJoined.pickupInspect?.inspection?.let{ appDB.inspectionsDao().delete(it)}        //cascade deleting inspection and all child tables
                vehicleJoined.deliveryInspect?.inspection?.let{ appDB.inspectionsDao().delete(it)}

                //vehicleJoined.vehicle?.let{ vehiclesList.add(it)}
            }
            //appDB.vehiclesDao().deleteList(vehiclesList)

            //ebol.customerSignatureFile?.let { App.component.getFilesGateway().deleteSignature(it)}

            ebolJoined.shipperComp?.let{ appDB.companiesDao().delete(it)}
            ebolJoined.pickUpComp?.let{ appDB.companiesDao().delete(it)}
            ebolJoined.deliveryComp?.let{ appDB.companiesDao().delete(it)}
            val res = appDB.ebolsDao().delete(ebol)
            return@withContext res > 0         //cascade delete with vehicles
        }
        return@withContext false
    }

}

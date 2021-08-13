package com.fithealthteam.fithealth.huawei.CloudDB;

import android.content.Context;
import android.util.Log;

import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.ListenerHandler;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class CloudDBZoneWrapper {
    private static final String TAG = "CloudDBZoneWrapper";

    private AGConnectCloudDB mCloudDB;

    private CloudDBZone mCloudDBZone;

    private ListenerHandler mRegister;

    private CloudDBZoneConfig mConfig;

    //initialize Cloud DB in Application
    public static void initAGConnectCloudDB(Context context) {
        AGConnectCloudDB.initialize(context);
    }

    //get AGConnectCloudDB instance
    public CloudDBZoneWrapper() {
        mCloudDB = AGConnectCloudDB.getInstance();
    }
    //create an object type
    public void createObjectType() {
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "createObjectType: " + e.getMessage());
        }
    }

    //configure CloudDBZone configuration object and open CloudDBZone
    /**
     * Call AGConnectCloudDB.openCloudDBZone to open a cloudDBZone.
     * We set it with cloud cache mode, and data can be store in local storage
     */
    public void openCloudDBZone(String zone) {
        mConfig = new CloudDBZoneConfig(zone,
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);
        try {
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig, true);
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "openCloudDBZone: " + e.getMessage());
        }
    }

    //close the cloudDB zone
    public void closeCloudDBZone() {
        try {
            mRegister.remove();
            mCloudDB.closeCloudDBZone(mCloudDBZone);
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "closeCloudDBZone: " + e.getMessage());
        }
    }

    //delete cloudDB Zone
    public void deleteCloudDBZone() {
        try {
            mCloudDB.deleteCloudDBZone(mConfig.getCloudDBZoneName());
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "deleteCloudDBZone: " + e.getMessage());
        }
    }

    /*
    * CloudDB CRUD Function Here
    * */

    //query all exercise cloudDB zone data while data type is exercise
    public void queryAllExercise(){
        if(mCloudDBZone == null){
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        Task<CloudDBZoneSnapshot<Event>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(Event.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Event>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Event> exerciseCloudDBZoneSnapshot) {
                extractExerciseResult(exerciseCloudDBZoneSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //show failure message
            }
        });
    }

    //extract it from cloudDB list object into normal List<Exercise> Object
    public List<Event> extractExerciseResult(CloudDBZoneSnapshot<Event> snapshot){
        CloudDBZoneObjectList<Event> cursor = snapshot.getSnapshotObjects();
        List<Event> list = new ArrayList<>();

        try{
            while (cursor.hasNext()){
                Event item = cursor.next();
                list.add(item);
            }
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }finally {
            snapshot.release();
        }
        return list;
    }

}

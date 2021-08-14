package com.fithealthteam.fithealth.huawei.CloudDB;

import android.content.Context;
import android.util.Log;

import com.fithealthteam.fithealth.huawei.myplan.MyPlanActivity;
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

    private exerciseUICallBack exerciseCallback = exerciseUICallBack.DEFAULT;

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
    //our zone always is fithealth
    public void openCloudDBZone() {
        mConfig = new CloudDBZoneConfig("fithealth",
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

    //add callback for update UI
    public void addExerciseCallBack(exerciseUICallBack UICallBack){
        exerciseCallback = UICallBack;
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

        Task<CloudDBZoneSnapshot<exercise>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(exercise.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<exercise>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<exercise> exerciseCloudDBZoneSnapshot) {
                List<exercise> tempResult = extractExerciseResult(exerciseCloudDBZoneSnapshot);
                exerciseCallback.onAddorQuery(tempResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //show failure message
                exerciseCallback.showError("Query Failed");
            }
        });
    }

    //extract it from cloudDB list object into normal List<Exercise> Object
    public List<exercise> extractExerciseResult(CloudDBZoneSnapshot<exercise> snapshot){
        CloudDBZoneObjectList<exercise> cursor = snapshot.getSnapshotObjects();
        List<exercise> list = new ArrayList<>();

        try{
            while (cursor.hasNext()){
                exercise item = cursor.next();
                list.add(item);
            }
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }finally {
            snapshot.release();
        }
        return list;
    }

    //add call back method
    public void addCallBack(exerciseUICallBack InputUICallBack){
        exerciseCallback = InputUICallBack;
    }

    //call back method for exercise object type
    public interface exerciseUICallBack {
        exerciseUICallBack DEFAULT = new exerciseUICallBack() {
            @Override
            public void onAddorQuery(List<exercise> exerciseList) {

            }

            @Override
            public void onSubscribe(List<exercise> exerciseList) {

            }

            @Override
            public void onDelete(List<exercise> exerciseList) {

            }

            @Override
            public void showError(String error) {

            }
        };
        void onAddorQuery(List<exercise> exerciseList);
        void onSubscribe(List<exercise> exerciseList);
        void onDelete(List<exercise> exerciseList);
        void showError(String error);
    }

}

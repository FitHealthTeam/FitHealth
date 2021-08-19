package com.fithealthteam.fithealth.huawei.CloudDB;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
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

    private userUICallBack userCallback = userUICallBack.DEFAULT;

    //get AGConnectCloudDB instance
    public CloudDBZoneWrapper() {
        mCloudDB = AGConnectCloudDB.getInstance();
    }


    //initialize Cloud DB in Application
    public static void initAGConnectCloudDB(Context context) {
        AGConnectCloudDB.initialize(context);
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
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig,true);
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "openCloudDBZone: " + e.getMessage());
        }catch (Exception e){
            Log.w("CloudDB", e.getMessage());
        }
    }

    //close the cloudDB zone
    public void closeCloudDBZone() {
        try {
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

    public void queryExercise(CloudDBZoneQuery<exercise> query){
        if(mCloudDBZone == null){
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        Task<CloudDBZoneSnapshot<exercise>> queryTask = mCloudDBZone.executeQuery(
                query,
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

    //generate id and execute upsertExercise method
    public void insertNewExercise(exercise item) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }
        Handler handler = new Handler();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

        CloudDBZoneQuery<exercise> query = CloudDBZoneQuery.where(exercise.class).equalTo("uid", user.getUid());
        CloudDBZoneQuery<exercise> query2 = CloudDBZoneQuery.where(exercise.class).equalTo("uid", user.getUid()).equalTo("deleteStatus",false);

        Task<Long> countQueryTask = mCloudDBZone.executeCountQuery(query, "uid",
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        countQueryTask.addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                //set the fetch id count and pass to upsert
                item.setId(Integer.valueOf(aLong.toString())+1);
                upsertExercise(item);

                //delay a bit to query and update UI to wait for insert action is complete
                handler.postDelayed(()->{
                        queryExercise(query2);
                },500);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, "Count query is failed: " + Log.getStackTraceString(e));
            }
        });
    }

    //generate id and execute upsertExercise method from motion track
    public void insertNewExerciseFromMotionTrack(exercise item) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }
        Handler handler = new Handler();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

        CloudDBZoneQuery<exercise> query = CloudDBZoneQuery.where(exercise.class).equalTo("uid", user.getUid());

        Task<Long> countQueryTask = mCloudDBZone.executeCountQuery(query, "uid",
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        countQueryTask.addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                //set the fetch id count and pass to upsert
                item.setId(Integer.valueOf(aLong.toString())+1);

                item.setUid(user.getUid());
                upsertExercise(item);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, "Count query is failed: " + Log.getStackTraceString(e));
            }
        });
    }

    //upsert function - update or add into the cloudDB
    public void upsertExercise(exercise exerciseItem){
        if (mCloudDBZone == null){
            Log.w(TAG,"CloudDB Zone is null !");
            return;
        }

        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(exerciseItem);
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                Log.w(TAG, " upsert " + integer + " record");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                exerciseCallback.showError("Insert or update was failed !");
            }
        });
    }

    //remove the exercise item from CloudDB
    public void deleteExercise(exercise exerciseList){
        if(mCloudDBZone == null){
            Log.w(TAG, "CloudDB Zone is null.");
            return;
        }
        Task<Integer> deleteTask = mCloudDBZone.executeDelete(exerciseList);
        if(deleteTask.getException() != null){
            exerciseCallback.showError("Delete Exercise Failed !");
            return;
        }
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


    //CRUD for user info
    //query all user data from cloud
    public void queryAllUser() {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }
        Task<CloudDBZoneSnapshot<user>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(user.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<user>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<user> usersnapshot) {
                List<user> tempResult = extractuserResult(usersnapshot);
                userCallback.userOnAddorQuery(tempResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                userCallback.userShowError("Query user list from cloud failed");
            }
        });
    }

    //query particular user data
    public void queryUser(CloudDBZoneQuery<user> userquery){
        if(mCloudDBZone == null){
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }

        Task<CloudDBZoneSnapshot<user>> queryTask = mCloudDBZone.executeQuery(
                userquery,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<user>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<user> userCloudDBZoneSnapshot) {
                List<user> tempResult = extractuserResult(userCloudDBZoneSnapshot);
                userCallback.userOnAddorQuery(tempResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //show failure message
                userCallback.userShowError("Query user list from cloud failed");
            }
        });
    }

    //method help to extract the user data into array and send back to query user data above
    public List<user> extractuserResult(CloudDBZoneSnapshot<user> usersnapshot){
        CloudDBZoneObjectList<user> cursor = usersnapshot.getSnapshotObjects();
        List<user> list = new ArrayList<>();

        try{
            while (cursor.hasNext()){
                user userItem = cursor.next();
                list.add(userItem);
            }
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }finally {
            usersnapshot.release();
        }
        return list;
    }

    //upsert function - update or add user data into the cloudDB
    public void upsertUser(user user){
        if (mCloudDBZone == null){
            Log.w(TAG,"CloudDB Zone is null ! from user upsert");
            return;
        }

        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(user);
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                Log.w(TAG, " upsert " + integer + " record");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                exerciseCallback.showError("Upsert or Insert to cloud failed !");
            }
        });
    }


    //update Weight
    public void updateWeight(String uid, double mWeight,double mHeight){
        if (mCloudDBZone == null){
            Log.w(TAG,"CloudDB Zone is null !");
            return;
        }

        CloudDBZoneQuery<user> query1 = CloudDBZoneQuery.where(user.class).
                equalTo("id", uid);

        Task<CloudDBZoneSnapshot<user>> queryTask = mCloudDBZone.executeQuery(
                query1,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<user>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<user> userCloudDBZoneSnapshot) {
                List<user> tempResult = extractuserResult(userCloudDBZoneSnapshot);
                user tempUser = tempResult.get(0);

                tempUser.setWeight(mWeight);
                tempUser.setHeight(mHeight);

                upsertUser(tempUser);

                userCallback.userOnAddorQuery(tempResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //show failure message
                userCallback.userShowError("Query user list from cloud failed");
            }
        });
    }


    //update drink Water
    public void updateDrinkWater(String uid, boolean drinkWater){
        if (mCloudDBZone == null){
            Log.w(TAG,"CloudDB Zone is null !");
            return;
        }

        CloudDBZoneQuery<user> query1 = CloudDBZoneQuery.where(user.class).
                equalTo("id", uid);

        Task<CloudDBZoneSnapshot<user>> queryTask = mCloudDBZone.executeQuery(
                query1,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<user>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<user> userCloudDBZoneSnapshot) {
                List<user> tempResult = extractuserResult(userCloudDBZoneSnapshot);
                user tempUser = tempResult.get(0);

                tempUser.setDrinkWater(drinkWater);

                upsertUser(tempUser);

                userCallback.userOnAddorQuery(tempResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //show failure message
                userCallback.userShowError("Query user list from cloud failed");
            }
        });
    }

    //update drink Water
    public void updatecaloriesWarning(String uid, boolean caloriesWarning){
        if (mCloudDBZone == null){
            Log.w(TAG,"CloudDB Zone is null !");
            return;
        }

        CloudDBZoneQuery<user> query1 = CloudDBZoneQuery.where(user.class).
                equalTo("id", uid);

        Task<CloudDBZoneSnapshot<user>> queryTask = mCloudDBZone.executeQuery(
                query1,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<user>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<user> userCloudDBZoneSnapshot) {
                List<user> tempResult = extractuserResult(userCloudDBZoneSnapshot);
                user tempUser = tempResult.get(0);

                tempUser.setExcessiveCalories(caloriesWarning);

                upsertUser(tempUser);

                userCallback.userOnAddorQuery(tempResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //show failure message
                userCallback.userShowError("Query user list from cloud failed");
            }
        });
    }

    //update drink Water
    public void updateSubscribedTips(String uid, boolean subscribeTips){
        if (mCloudDBZone == null){
            Log.w(TAG,"CloudDB Zone is null !");
            return;
        }

        CloudDBZoneQuery<user> query1 = CloudDBZoneQuery.where(user.class).
                equalTo("id", uid);

        Task<CloudDBZoneSnapshot<user>> queryTask = mCloudDBZone.executeQuery(
                query1,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<user>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<user> userCloudDBZoneSnapshot) {
                List<user> tempResult = extractuserResult(userCloudDBZoneSnapshot);
                user tempUser = tempResult.get(0);

                tempUser.setSubscribeTips(subscribeTips);

                upsertUser(tempUser);

                userCallback.userOnAddorQuery(tempResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //show failure message
                userCallback.userShowError("Query user list from cloud failed");
            }
        });
    }

    //remove the user data item from CloudDB
    public void deleteUser(user user){
        if(mCloudDBZone == null){
            Log.w(TAG, "CloudDB Zone is null.");
            return;
        }
        Task<Integer> deleteTask = mCloudDBZone.executeDelete(user);
        if(deleteTask.getException() != null){
            exerciseCallback.showError("Delete Exercise in cloud Failed !");
            return;
        }
    }

    //add call back method - to call
    public void addUserCallBack(userUICallBack InputUICallBack){
        userCallback = InputUICallBack;
    }

    //call back method for user object type
    public interface userUICallBack {
        userUICallBack DEFAULT = new userUICallBack() {
            @Override
            public void userOnAddorQuery(List<user> userList) {

            }

            @Override
            public void userOnSubscribe(List<user> userList) {

            }

            @Override
            public void userOnDelete(List<user> userList) {

            }

            @Override
            public void userShowError(String error) {

            }
        };
        void userOnAddorQuery(List<user> userList);
        void userOnSubscribe(List<user> userList);
        void userOnDelete(List<user> userList);
        void userShowError(String error);
    }

}

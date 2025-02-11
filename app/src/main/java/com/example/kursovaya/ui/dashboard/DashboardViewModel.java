package com.example.kursovaya.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private final MutableLiveData<Integer> sharedData = new MutableLiveData<Integer>(0);

    public void setSharedData(Integer data) {
        this.sharedData.postValue(data);
    }

    public LiveData<Integer> getSharedData() {
        return sharedData;
    }

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
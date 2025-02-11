package com.example.kursovaya.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;


    private final MutableLiveData<Integer> sharedData = new MutableLiveData<Integer>(0);

    public void setSharedData(Integer data) {
        this.sharedData.postValue(data);
    }

    public LiveData<Integer> getSharedData() {
        return sharedData;
    }

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
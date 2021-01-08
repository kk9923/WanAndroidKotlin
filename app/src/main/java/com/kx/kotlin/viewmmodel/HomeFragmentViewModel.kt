package com.kx.kotlin.viewmmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kx.kotlin.bean.Banner
import com.kx.kotlin.bean.HttpResult
import com.kx.kotlin.http.RequestViewModel
import com.kx.kotlin.http.RetrofitHelper
import com.kx.kotlin.http.requestLiveData
import kotlinx.coroutines.launch

class HomeFragmentViewModel : RequestViewModel() {

    private val service = RetrofitHelper.service

    val liveData = MutableLiveData<List<Banner>>()

    fun getBanners() {
        request<List<Banner>> {
            onRequest {
                println(" Thread-->onRequest  ${Thread.currentThread().name} ")
                service.getBannersSuspend()
            }
            onResponse {
                println(" Thread-->onResponse  ${Thread.currentThread().name} ")
                println(" Thread-->onResponse  ${Gson().toJson(it)} ")
                liveData.value = it
            }
            onStart {
                println(" Thread-->onStart  ${Thread.currentThread().name} ")
                false
            }
            onError { errorMsg, httpCode ->
                println(" Thread-->onError   httpCode  $httpCode   errorMsg  $errorMsg    ${Thread.currentThread().name} ")
                false
            }
        }
    }
}
package com.example.front.viewmodels.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.front.repository.Repository

//ViewmModelProvider.Factory
class MainViewModelFacotry(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }

        throw IllegalArgumentException("Nepodržan tip ViewModel-a: ${modelClass.name}")
    }
}
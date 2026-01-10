package com.example.aistatementparser

import StatementViewModel
import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider

class StatementViewModelFactory (
    private val repository: StatementRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatementViewModel::class.java)) {
            return StatementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
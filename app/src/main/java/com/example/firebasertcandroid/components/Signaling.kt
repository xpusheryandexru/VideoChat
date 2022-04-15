package com.example.firebasertcandroid.components

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@InstallIn(SingletonComponent::class)
@Module
class Signaling @Inject constructor() {

    var db = FirebaseFirestore.getInstance()

}
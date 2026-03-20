package com.example.zoom.presentation.profile

import com.example.zoom.model.User

interface ProfileContract {
    interface View {
        fun showUser(user: User)
    }
    interface Presenter {
        fun loadData()
    }
}

package com.example.activityworld.login.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.activityworld.login.datasource.LoginRepository
import com.example.activityworld.R
import com.example.activityworld.login.ui.LoggedInUserView
import com.example.activityworld.login.ui.LoginFormState
import com.example.activityworld.login.ui.LoginResult
import com.example.activityworld.network.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _loadingStatus = MutableLiveData<ApiStatus>()
    val loadingStatus: LiveData<ApiStatus> = _loadingStatus

    fun login(username: String, password: String) {

        _loadingStatus.value = ApiStatus.LOADING

        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository.login(username, password)

            if (result is Result.Success) {
                _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = result.data.displayName)))
                _loadingStatus.postValue(ApiStatus.DONE)
            } else {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
                _loadingStatus.postValue(ApiStatus.ERROR)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
       /*
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
        */
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
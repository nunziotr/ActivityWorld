package com.example.activityworld.login.datasource

import com.example.activityworld.login.model.LoggedInUser
import com.example.activityworld.login.model.Result
import com.example.activityworld.login.network.LoginProxy
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val loginProxy = LoginProxy.instance

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        var result: Result<LoggedInUser>

        result = Result.Error(IOException("Error logging in"))

        loginProxy?.let {
            it.loginUser(username, password)?.let { loggedInUser ->
                result = Result.Success(loggedInUser)
            }
        }
        return result
    }


    fun loginFake(username: String, password: String): Result<LoggedInUser> {
        try {
            val fakeUser = LoggedInUser(0L, "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
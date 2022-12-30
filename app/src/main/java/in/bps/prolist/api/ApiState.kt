package `in`.bps.prolist.api

import `in`.bps.prolist.models.ApiResponse

sealed class ApiState {
    object Loading : ApiState()
    class Failure(val msg : Throwable) : ApiState()
    class Success(val data: ApiResponse) : ApiState()
    object Empty : ApiState()
}
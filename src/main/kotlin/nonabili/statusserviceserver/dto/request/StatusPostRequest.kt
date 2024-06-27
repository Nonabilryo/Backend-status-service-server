package nonabili.statusserviceserver.dto.request

data class StatusPostRequest(
    val articleIdx: String,
    val title: String,
    val description: String
)

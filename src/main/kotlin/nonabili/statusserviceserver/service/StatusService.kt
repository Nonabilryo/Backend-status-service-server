package nonabili.statusserviceserver.service

import nonabili.statusserviceserver.client.ArticleClient
import nonabili.statusserviceserver.client.UserClient
import nonabili.statusserviceserver.dto.request.StatusPostRequest
import nonabili.statusserviceserver.entity.Status
import nonabili.statusserviceserver.repository.StatusRepository
import nonabili.statusserviceserver.util.error.CustomError
import nonabili.statusserviceserver.util.error.ErrorState
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class StatusService(val statusRepository: StatusRepository, val s3UploadService: S3UploadService, val articleClient: ArticleClient, val userClient: UserClient) {
    fun postStatus(images: List<MultipartFile>, video: MultipartFile?, request: StatusPostRequest, userIdx: String) {
        if (images.isEmpty() && video == null) throw CustomError(ErrorState.EMPTY_REQUEST)
//        val userIdx = userClient.getUserIdxById(userId).idx ?: throw CustomError(ErrorState.SERVER_UNAVAILABLE)
        val writerIdx = articleClient.getWriterIdxByArticleIdx(request.articleIdx).idx ?: throw CustomError(ErrorState.SERVER_UNAVAILABLE)
        if (userIdx != writerIdx) throw CustomError(ErrorState.DIFFERENT_USER)
        try {
            statusRepository.save(
                Status(
                    imageUrls = images.map { s3UploadService.saveFileAndGetUrl(it, "status_images") },
                    videoUrl = video?.let { s3UploadService.saveFileAndGetUrl(video, "status_video")} ?: "",
                    article = UUID.fromString(request.articleIdx),
                    title = request.title,
                    description = request.description
                )
            )
        } catch (e: Exception) {
            throw CustomError(ErrorState.CANT_SAVE)
        }
    }
    fun getStatus(articleIdx: String, page: Int): Page<Status> {
        return statusRepository.findStatusesByArticle(UUID.fromString(articleIdx), PageRequest.of(page, 10))
    }
    fun deleteStatus(statusIdx: String, userIdx: String) {
        val status = statusRepository.findStatusByIdx(UUID.fromString(statusIdx)) ?: throw CustomError(ErrorState.NOT_FOUND_STATUS)
//        val userIdx = userClient.getUserIdxById(userId).idx ?: throw CustomError(ErrorState.SERVER_UNAVAILABLE)
        val writerIdx = articleClient.getWriterIdxByArticleIdx(status.article.toString()).idx ?: throw CustomError(ErrorState.SERVER_UNAVAILABLE)
        if (userIdx != writerIdx) throw CustomError(ErrorState.DIFFERENT_USER)
        statusRepository.delete(status)
    }
}
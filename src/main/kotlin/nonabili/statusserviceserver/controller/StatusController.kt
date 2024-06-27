package nonabili.statusserviceserver.controller

import jakarta.validation.Valid
import nonabili.statusserviceserver.dto.request.StatusPostRequest
import nonabili.statusserviceserver.service.StatusService
import nonabili.statusserviceserver.util.ResponseFormat
import nonabili.statusserviceserver.util.ResponseFormatBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/status")
class StatusController(val statusService: StatusService) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun postStatus(
        @RequestParam(required = false) images: List<MultipartFile>,
        @RequestParam(required = false) video: MultipartFile?,
        @RequestHeader requestHeaders: HttpHeaders,
        @Valid request: StatusPostRequest
    ): ResponseEntity<ResponseFormat<Any>> {
        val userIdx = requestHeaders.get("userIdx")!![0]
        statusService.postStatus(images, video, request, userIdx)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.noData())
    }
    @GetMapping()
    fun getStatus(
        @RequestHeader requestHeaders: HttpHeaders,
        @RequestParam(required = false, defaultValue = "0", value = "page",) page: Int,
        @RequestParam(required = true, value = "articleIdx") articleIdx: String
        ): ResponseEntity<ResponseFormat<Any>> {
        val result = statusService.getStatus(articleIdx, page)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.build(result))
    }
    @DeleteMapping("/{statusIdx}")
    fun deleteStatus(@RequestHeader requestHeaders: HttpHeaders, @PathVariable statusIdx: String): ResponseEntity<ResponseFormat<Any>> {
        val userIdx = requestHeaders.get("userIdx")!![0]
        statusService.deleteStatus(statusIdx, userIdx)
        return ResponseEntity.ok(ResponseFormatBuilder { message = "success" }.noData())
    }
}
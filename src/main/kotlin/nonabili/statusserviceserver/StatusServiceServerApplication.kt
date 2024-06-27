package nonabili.statusserviceserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["nonabili.statusserviceserver.client"])
class StatusServiceServerApplication

fun main(args: Array<String>) {
    runApplication<StatusServiceServerApplication>(*args)
}

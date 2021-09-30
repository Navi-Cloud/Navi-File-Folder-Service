package com.navi.storage

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class NaviStorageServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(NaviStorageServiceApplication::class.java, *args)
}
package com.example.tics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TicsApplication

fun main(args: Array<String>) {
	runApplication<TicsApplication>(*args)
}

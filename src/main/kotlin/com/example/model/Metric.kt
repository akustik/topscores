package com.example.model

class Metric() {
    lateinit var name: String
    lateinit var value: String
    lateinit var type: String
    
    constructor(
            name: String,
            value: String,
            type: String): this() {
        this.name = name
        this.value = value
        this.type = type
    }
}
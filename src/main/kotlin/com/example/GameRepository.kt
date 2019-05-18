package com.example

import java.sql.Timestamp

interface GameRepository {

    fun listTicks(): List<Timestamp>
}
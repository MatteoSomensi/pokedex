package com.example.pokedex.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * This interface is responsible for DataRepository logic.
 * Part of the Clean Architecture structure.
 */
interface DataRepository {
  val data: Flow<List<String>>
}

class DefaultDataRepository : DataRepository {
  override val data: Flow<List<String>> = flow { emit(listOf("Android")) }
}

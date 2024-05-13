package com.example.animalobserving.data.species

data class Specie(private val specieId: Int, private val specieName: String) {
    fun getID(): Int {
        return specieId
    }

    fun getSpecieName(): String {
        return specieName
    }

    override fun toString(): String {
        return "$specieId;$specieName"
    }
}
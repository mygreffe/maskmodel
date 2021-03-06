package fr.convergence.proddoc.model.lib.obj

import fr.convergence.proddoc.model.lib.serdes.LocalDateTimeSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import java.time.LocalDateTime
import java.util.*


@Serializable
class MaskEntete(
    @Required
    val idUnique: String,
    val idLot: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateHeureDemande: LocalDateTime,
    val idEmetteur: String,
    val idReference: String,
    @Required
    val idGreffe: String,
    val typeDemande: String
) {
    override fun toString(): String {
        return "MaskEntete(idUnique='$idUnique', idLot=$idLot, dateHeureDemande=$dateHeureDemande, idEmetteur='$idEmetteur', idGreffe='$idGreffe', typeDemande='$typeDemande')"
    }
}

@Serializable
class MaskReponse(
    val estReponseOk: Boolean,
    val messageErreur: String? = null,
    val stackTrace: String? = null
) {
    override fun toString(): String {
        return "MaskReponse(estReponseOk=$estReponseOk, messageErreur=$messageErreur, stackTrace=$stackTrace)"
    }
}

@Serializable
class MaskMessage(
    @Required
    val entete: MaskEntete,
    val objetMetier: JsonElement?,
    val reponse: MaskReponse?
) {
    inline fun <reified T> recupererObjetMetier(): T {
        return Json { ignoreUnknownKeys = true } .decodeFromJsonElement(objetMetier!!)
    }

    fun isQuestion() = reponse == null
    fun isReponse() = !isQuestion()


    companion object MaskMessageBuilder {


        fun deserialisationKo(ex: Exception): MaskMessage {

            val maskEntete = MaskEntete(
                UUID.randomUUID().toString(),
                "idLot indécodable",
                LocalDateTime.now(),
                "idEmetteur indécodable",
                "idReference indécodable",
                "idGreffe indécodable",
                "typeDemande indécodable"
            )

            return MaskMessage(maskEntete, Json.parseToJsonElement(Json.encodeToString(ex.message)), null)
        }

        inline fun <reified T> question(
            payload: T,
            idLot: String,
            idEmetteur: String,
            idReference: String,
            idGreffe: String,
            typeDemande: String
        ): MaskMessage {

            val maskEntete = MaskEntete(
                UUID.randomUUID().toString(),
                idLot,
                LocalDateTime.now(),
                idEmetteur,
                idReference,
                idGreffe,
                typeDemande
            )

            return MaskMessage(maskEntete, Json.parseToJsonElement(Json.encodeToString(payload)), null)
        }

        inline fun <reified T> question(
            payload: T,
            maskMessageQuestion: MaskMessage,
            idReference: String
        ): MaskMessage {

            val maskEntete = MaskEntete(
                UUID.randomUUID().toString(),
                maskMessageQuestion.entete.idLot,
                LocalDateTime.now(),
                maskMessageQuestion.entete.idEmetteur,
                idReference,
                maskMessageQuestion.entete.idGreffe,
                maskMessageQuestion.entete.typeDemande
            )

            return MaskMessage(maskEntete, Json.parseToJsonElement(Json.encodeToString(payload)), null)
        }


        inline fun <reified T> reponseOk(
            payload: T,
            maskMessageQuestion: MaskMessage,
            idReference: String
        ): MaskMessage {
            val maskEntete = MaskEntete(
                UUID.randomUUID().toString(),
                maskMessageQuestion.entete.idLot,
                LocalDateTime.now(),
                maskMessageQuestion.entete.idEmetteur,
                idReference,
                maskMessageQuestion.entete.idGreffe,
                maskMessageQuestion.entete.typeDemande
            )

            return MaskMessage(
                maskEntete,
                Json.parseToJsonElement(Json.encodeToString(payload)),
                MaskReponse(true)
            )
        }

        inline fun <reified T> reponseKo(
            ex: Exception,
            maskMessageQuestion: MaskMessage,
            idReference: String
        ): MaskMessage {
            val maskEntete = MaskEntete(
                UUID.randomUUID().toString(),
                maskMessageQuestion.entete.idLot,
                LocalDateTime.now(),
                maskMessageQuestion.entete.idEmetteur,
                idReference,
                maskMessageQuestion.entete.idGreffe,
                maskMessageQuestion.entete.typeDemande
            )

            return MaskMessage(
                maskEntete,
                null, MaskReponse(false, ex.message, ex.message)
            )
        }
    }

    override fun toString(): String {
        return "MaskMessage(entete=$entete, objetMetier=$objetMetier, reponse=$reponse)"
    }
}
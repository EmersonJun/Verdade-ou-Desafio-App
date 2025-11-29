package com.cafeteria.verdadeoudesafio.models

enum class GameState {
    MAIN_MENU, OPTIONS, SETUP, SPINNING, PLAYERS_REVEAL, CHOOSE, QUESTION_TYPE, RESULT, SCOREBOARD, PHOTO_CAPTURE
}

data class PlayerScore(
    val name: String,
    var points: Int = 0,
    var challengesCompleted: Int = 0,
    var truthsCompleted: Int = 0,
    var refusals: Int = 0
)

data class PhotoRecord(
    val id: String,
    val players: List<String>,
    val timestamp: Long,
    val challengeType: String,
    val photoUri: String,
    val thumbnailUri: String? = null,
    val description: String = "",
    val consentGiven: Boolean = true
)

data class GameSettings(
    var soundEnabled: Boolean = true,
    var musicEnabled: Boolean = true,
    var soundVolume: Float = 0.7f,
    var musicVolume: Float = 0.5f,
    var allowSavePhotos: Boolean = true,
    var hapticEnabled: Boolean = true
)

// Pontuação sugerida
object ScoreRules {
    const val COMPLETE_DARE = 5
    const val COMPLETE_TRUTH = 3
    const val REFUSE_CHALLENGE = -3
    const val WIN_ROUND = 4
}

val truthQuestions = listOf(
    "Qual foi a maior vergonha que você já passou em público?",
    "Qual foi a coisa mais idiota que você já fez por amor?",
    "Se pudesse trocar de vida com alguém por um dia, quem seria?",
    "Qual o seu maior crush de todos os tempos (famoso ou não)?",
    "Já mentiu para sair de um encontro? O que disse?",
    "Qual o apelido mais estranho que já te deram?",
    "Se tivesse que comer apenas uma coisa pro resto da vida, o que seria?",
    "Qual foi a última mensagem que te deixou com vergonha?",
    "Se pudesse apagar um momento da sua vida, qual seria?",
    "Qual foi o maior mico que você já pagou na escola ou trabalho?"
)

val dareQuestions = listOf(
    "Imite alguém famoso por 10 segundos.",
    "Cante o refrão da primeira música que vier na sua cabeça.",
    "Dance sem música por 20 segundos.",
    "Fale com voz de bebê até sua próxima vez.",
    "Conte uma piada ruim como se fosse a mais engraçada do mundo.",
    "Envie um emoji aleatório para a última pessoa do seu WhatsApp.",
    "Fale um trava-língua sem errar — se errar, faz uma careta!",
    "Finja que está dando uma entrevista para um programa de TV.",
    "Diga o nome de três frutas o mais rápido que conseguir (sem repetir).",
    "Faça uma pose de super-herói e mantenha por 10 segundos."
)
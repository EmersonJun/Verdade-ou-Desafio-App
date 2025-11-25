package com.cafeteria.verdadeoudesafio.models

enum class GameState {
    MAIN_MENU, OPTIONS, SETUP, SPINNING, PLAYERS_REVEAL, CHOOSE, QUESTION_TYPE, RESULT
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
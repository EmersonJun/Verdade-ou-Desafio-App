package com.cafeteria.verdadeoudesafio.models

import androidx.compose.ui.graphics.Color
import com.cafeteria.verdadeoudesafio.ui.theme.NeonBlue
import com.cafeteria.verdadeoudesafio.ui.theme.NeonRed

enum class CardRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

enum class CardType {
    TRUTH,
    DARE,
    UNIVERSAL
}

enum class PowerCardEffect {
    SKIP,
    DOUBLE_POINTS,
    STEAL_POINTS,
    SHIELD,
    CHOOSE_PLAYER,
    REVERSE,
    EXTRA_TURN,
    TRIPLE_POINTS
}

data class PowerCard(
    val id: String,
    val name: String,
    val description: String,
    val effect: PowerCardEffect,
    val type: CardType,
    val rarity: CardRarity,
    val pointsModifier: Float = 1f
) {
    fun getColor(): Color = when (type) {
        CardType.TRUTH -> NeonBlue
        CardType.DARE -> NeonRed
        CardType.UNIVERSAL -> Color(0xFFAA00FF)
    }

    fun getRarityColor(): Color = when (rarity) {
        CardRarity.COMMON -> Color.Gray
        CardRarity.RARE -> Color(0xFF4169E1)
        CardRarity.EPIC -> Color(0xFF9400D3)
        CardRarity.LEGENDARY -> Color(0xFFFFD700)
    }
}

object PowerCards {
    val allCards = listOf(
        PowerCard(
            id = "truth_skip_common",
            name = "PULO SEGURO",
            description = "Pule uma verdade sem perder pontos",
            effect = PowerCardEffect.SKIP,
            type = CardType.TRUTH,
            rarity = CardRarity.COMMON
        ),
        PowerCard(
            id = "truth_double_common",
            name = "VERDADE EM DOBRO",
            description = "Ganhe o dobro de pontos nesta verdade",
            effect = PowerCardEffect.DOUBLE_POINTS,
            type = CardType.TRUTH,
            rarity = CardRarity.COMMON,
            pointsModifier = 2f
        ),
        PowerCard(
            id = "dare_skip_common",
            name = "FUGA RÁPIDA",
            description = "Pule um desafio sem perder pontos",
            effect = PowerCardEffect.SKIP,
            type = CardType.DARE,
            rarity = CardRarity.COMMON
        ),
        PowerCard(
            id = "dare_double_common",
            name = "DESAFIO EM DOBRO",
            description = "Ganhe o dobro de pontos neste desafio",
            effect = PowerCardEffect.DOUBLE_POINTS,
            type = CardType.DARE,
            rarity = CardRarity.COMMON,
            pointsModifier = 2f
        ),
        PowerCard(
            id = "truth_shield_rare",
            name = "ESCUDO DA VERDADE",
            description = "Proteção contra perda de pontos na próxima verdade",
            effect = PowerCardEffect.SHIELD,
            type = CardType.TRUTH,
            rarity = CardRarity.RARE
        ),
        PowerCard(
            id = "truth_steal_rare",
            name = "ROUBO DE HONESTIDADE",
            description = "Roube 3 pontos de outro jogador",
            effect = PowerCardEffect.STEAL_POINTS,
            type = CardType.TRUTH,
            rarity = CardRarity.RARE
        ),
        PowerCard(
            id = "dare_shield_rare",
            name = "ESCUDO DO DESAFIO",
            description = "Proteção contra perda de pontos no próximo desafio",
            effect = PowerCardEffect.SHIELD,
            type = CardType.DARE,
            rarity = CardRarity.RARE
        ),
        PowerCard(
            id = "dare_steal_rare",
            name = "ROUBO DE CORAGEM",
            description = "Roube 5 pontos de outro jogador",
            effect = PowerCardEffect.STEAL_POINTS,
            type = CardType.DARE,
            rarity = CardRarity.RARE
        ),
        PowerCard(
            id = "truth_choose_epic",
            name = "ESCOLHA DO SÁBIO",
            description = "Escolha quem será desafiado na próxima rodada",
            effect = PowerCardEffect.CHOOSE_PLAYER,
            type = CardType.TRUTH,
            rarity = CardRarity.EPIC
        ),
        PowerCard(
            id = "dare_reverse_epic",
            name = "REVERSÃO ÉPICA",
            description = "Inverta quem desafia quem na próxima rodada",
            effect = PowerCardEffect.REVERSE,
            type = CardType.DARE,
            rarity = CardRarity.EPIC
        ),
        PowerCard(
            id = "dare_extra_epic",
            name = "TURNO EXTRA",
            description = "Jogue novamente após completar este desafio",
            effect = PowerCardEffect.EXTRA_TURN,
            type = CardType.DARE,
            rarity = CardRarity.EPIC
        ),
        PowerCard(
            id = "universal_triple_legendary",
            name = "PODER SUPREMO",
            description = "Ganhe TRIPLO de pontos nesta rodada!",
            effect = PowerCardEffect.TRIPLE_POINTS,
            type = CardType.UNIVERSAL,
            rarity = CardRarity.LEGENDARY,
            pointsModifier = 3f
        ),
        PowerCard(
            id = "universal_skip_legendary",
            name = "PASSE LIVRE",
            description = "Pule qualquer desafio sem perder pontos",
            effect = PowerCardEffect.SKIP,
            type = CardType.UNIVERSAL,
            rarity = CardRarity.LEGENDARY
        )
    )

    fun getRandomCard(challengesCompleted: Int): PowerCard? {
        val rarityRoll = (0..100).random()
        val rarity = when {
            rarityRoll <= 60 -> CardRarity.COMMON
            rarityRoll <= 90 -> CardRarity.RARE
            rarityRoll <= 98 -> CardRarity.EPIC
            else -> CardRarity.LEGENDARY
        }

        val availableCards = allCards.filter { it.rarity == rarity }
        return availableCards.randomOrNull()
    }
}
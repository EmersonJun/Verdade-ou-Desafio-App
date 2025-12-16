// ============================================
// 1. PowerCardManager.kt - NOVO ARQUIVO
// ============================================
package com.cafeteria.verdadeoudesafio.managers

import com.cafeteria.verdadeoudesafio.models.*

object PowerCardManager {

    data class CardEffect(
        val skipChallenge: Boolean = false,
        val pointsMultiplier: Float = 1f,
        val stealPoints: Int = 0,
        val stealFromChallenger: Boolean = false,
        val hasShield: Boolean = false,
        val reverseRoles: Boolean = false,
        val choosePlayer: Boolean = false,
        val extraTurn: Boolean = false,
        val skipNextChallenge: Boolean = false
    )

    fun applyCardEffect(
        card: PowerCard,
        challenged: String,
        challenger: String,
        players: List<String>,
        currentType: String
    ): CardEffect {
        return when (card.effect) {
            PowerCardEffect.SKIP -> CardEffect(
                skipChallenge = canSkipWithCard(card, currentType)
            )

            PowerCardEffect.DOUBLE_POINTS -> CardEffect(
                pointsMultiplier = 2f
            )

            PowerCardEffect.TRIPLE_POINTS -> CardEffect(
                pointsMultiplier = 3f
            )

            PowerCardEffect.STEAL_POINTS -> CardEffect(
                stealPoints = if (card.type == CardType.TRUTH) 3 else 5,
                stealFromChallenger = true
            )

            PowerCardEffect.SHIELD -> CardEffect(
                hasShield = true
            )

            PowerCardEffect.REVERSE -> CardEffect(
                reverseRoles = true
            )

            PowerCardEffect.CHOOSE_PLAYER -> CardEffect(
                choosePlayer = true
            )

            PowerCardEffect.EXTRA_TURN -> CardEffect(
                extraTurn = true
            )
        }
    }

    private fun canSkipWithCard(card: PowerCard, currentType: String): Boolean {
        return when (card.type) {
            CardType.TRUTH -> currentType == "Verdade"
            CardType.DARE -> currentType == "Desafio"
            CardType.UNIVERSAL -> true
        }
    }

    fun calculatePoints(basePoints: Int, multiplier: Float, hasShield: Boolean, refused: Boolean): Int {
        if (refused) {
            return if (hasShield) 0 else ScoreRules.REFUSE_CHALLENGE
        }
        return (basePoints * multiplier).toInt()
    }
}


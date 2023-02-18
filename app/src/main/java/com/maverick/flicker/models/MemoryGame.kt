package com.maverick.flicker.models

import com.google.android.material.snackbar.Snackbar
import com.maverick.flicker.utils.DEFAULT_ICONS

class MemoryGame(
    private val boardSize: BoardSize,
    private val customImages: List<String>?
) {
    val cards: List<MemoryCard>
    var numPairsFound = 0
    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null

    init {
        if (customImages == null) {
            val chosenImages: List<Int> = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
            val randomizedImages: List<Int> = (chosenImages + chosenImages).shuffled()
            cards = randomizedImages.map { MemoryCard(it) }
        } else {
            val randomizedImages: List<String> = (customImages + customImages).shuffled()
            cards = randomizedImages.map { MemoryCard(it.hashCode(), it) } //hashnode provide unique number for unique image string. i.e for easy its 4
        }
    }

    fun flipCard(position: Int): Boolean {
        numCardFlips++
        val card: MemoryCard = cards[position]
        //0 card is flipped = flip over the card + restore state
        //1 card is flipped = flip over the card and check if 0 and 1 is same
        //2 card is flipped = no 3 cards can be flipped over. return to the previous state
        var foundMatch = false
        if (indexOfSingleSelectedCard == null) {
            //0 and 2 cards condition
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            //1 card condition
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(indexOfSingleSelectedCard: Int, position: Int): Boolean {
        if (cards[indexOfSingleSelectedCard].identifier != cards[position].identifier) return false
        cards[indexOfSingleSelectedCard].isMatched = true
        cards[position].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for (card: MemoryCard in cards) {
            if (!card.isMatched) card.isFaceUp = false
        }
    }

    fun hasWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips / 2
    }
}
package org.hildan.minecraft.mining.optimizer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.hildan.minecraft.mining.optimizer.blocks.*
import org.hildan.minecraft.mining.optimizer.geometry.*
import org.hildan.minecraft.mining.optimizer.ore.*
import org.hildan.minecraft.mining.optimizer.patterns.*
import org.hildan.minecraft.mining.optimizer.patterns.generated.*
import org.hildan.minecraft.mining.optimizer.statistics.*

private const val NUM_EVAL_SAMPLES = 50

private const val SAMPLE_WIDTH = 16
private const val SAMPLE_HEIGHT = 5
private const val SAMPLE_LENGTH = 16

private const val SAMPLE_LOW_Y_POSITION = 5

private const val MAX_DUG_BLOCKS = 20

suspend fun main() = withContext(Dispatchers.Default) {
    val sampleDimensions = Dimensions(SAMPLE_WIDTH, SAMPLE_HEIGHT, SAMPLE_LENGTH)
    val constraints = GenerationConstraints(sampleDimensions, MAX_DUG_BLOCKS)

    println("Generating $NUM_EVAL_SAMPLES reference samples with lowest Y=$SAMPLE_LOW_Y_POSITION...")
    val referenceSamples = generateSamples(NUM_EVAL_SAMPLES, sampleDimensions, SAMPLE_LOW_Y_POSITION)

    println("Starting pattern generation with constraints: $constraints")
    val generatedPatterns = generatePatternsAsync(constraints)

    println("Starting pattern evaluation on reference samples...")
    val evaluatedPatterns = evaluateAsync(referenceSamples, generatedPatterns)

    val store = storePatternsAndPrintProgress(evaluatedPatterns)

    printBestPatterns(sampleDimensions, store)
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun CoroutineScope.generatePatternsAsync(constraints: GenerationConstraints): ReceiveChannel<DiggingPattern> =
    produce(Dispatchers.Default, capacity = 200) {
        PatternGenerator(constraints).forEach { send(it) }
        close()
    }

@OptIn(ExperimentalCoroutinesApi::class)
private fun CoroutineScope.evaluateAsync(
    referenceSamples: List<Sample>,
    generatedPatterns: ReceiveChannel<DiggingPattern>
) = produce(Dispatchers.Default, capacity = 200) {
    repeat(Runtime.getRuntime().availableProcessors() - 1) {
        launch {
            val patternEvaluator = PatternEvaluator(referenceSamples)
            for (p in generatedPatterns) {
                send(patternEvaluator.evaluate(p))
            }
        }
    }
}

private suspend fun storePatternsAndPrintProgress(evaluatedPatterns: ReceiveChannel<EvaluatedPattern>): PatternStore {
    val store = PatternStore()
    var count = 0
    for (p in evaluatedPatterns) {
        count++
        if (count % 10000 == 0) {
            println("$count evaluated patterns so far")
        }
        if (store.add(p)) {
            println(store)
        }
    }
    println("Finished with $count total evaluated patterns")
    return store
}

fun printBestPatterns(sampleDimensions: Dimensions, store: PatternStore) {
    val sample = Sample(sampleDimensions, BlockType.STONE)
    for (pattern in store) {
        sample.fill(BlockType.STONE)
        pattern.pattern.digInto(sample)
        println(sample)
        println(pattern.statistics.toFullString(NUM_EVAL_SAMPLES))
    }
}

package org.hildan.minecraft.mining.optimizer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hildan.minecraft.mining.optimizer.blocks.BlockType
import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.generated.GenerationConstraints
import org.hildan.minecraft.mining.optimizer.patterns.generated.PatternGenerator
import org.hildan.minecraft.mining.optimizer.statistics.EvaluatedPattern
import org.hildan.minecraft.mining.optimizer.statistics.PatternEvaluator
import org.hildan.minecraft.mining.optimizer.statistics.PatternStore

private const val NUM_EVAL_SAMPLES = 50

private const val SAMPLE_WIDTH = 16
private const val SAMPLE_HEIGHT = 5
private const val SAMPLE_LENGTH = 16

private const val SAMPLE_LOW_Y_POSITION = 5

private const val MAX_DUG_BLOCKS = 20

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val sampleDimensions = Dimensions(SAMPLE_WIDTH, SAMPLE_HEIGHT, SAMPLE_LENGTH)
    val constraints = GenerationConstraints(sampleDimensions, MAX_DUG_BLOCKS)

    println("Starting pattern generation with constraints: $constraints")
    val generator = PatternGenerator(constraints)
    val patterns = generateAsync(generator)

    println("Initializing evaluator with $NUM_EVAL_SAMPLES evaluation samples...")
    val evaluator = PatternEvaluator(NUM_EVAL_SAMPLES, sampleDimensions, SAMPLE_LOW_Y_POSITION)

    println("Starting pattern evaluation...")
    val evaluatedPatterns = evaluateAsync(evaluator, patterns)

    val store = PatternStore()
    var count = 0
    evaluatedPatterns.consumeEach {
        count++
        if (count % 10000 == 0) {
            println("$count total evaluated patterns")
        }
        if (store.add(it)) {
            println(store)
        }
    }
    println("Finished!")

    printBestPatterns(sampleDimensions, store)
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.generateAsync(patternGenerator: PatternGenerator) = produce(capacity = 200) {
    patternGenerator.iterator().forEach { send(it) }
    close()
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.evaluateAsync(patternEvaluator: PatternEvaluator, patterns: ReceiveChannel<DiggingPattern>) =
    produce(capacity = 200) {
        repeat(8) {
            launch {
                for (p in patterns) {
                    val stats = patternEvaluator.evaluate(p)
                    send(EvaluatedPattern(p, stats))
                }
            }
        }
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

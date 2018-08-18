package org.hildan.minecraft.mining.optimizer

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
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

private const val MAX_ACTIONS = 30
private const val MAX_DUG_BLOCKS = 20

fun main(vararg args: String) = runBlocking {
    val sampleDimensions = Dimensions(SAMPLE_WIDTH, SAMPLE_HEIGHT, SAMPLE_LENGTH)
    val constraints = GenerationConstraints(sampleDimensions, MAX_ACTIONS, MAX_DUG_BLOCKS)

    println("Starting pattern generation with constraints: $constraints")
    val generator = PatternGenerator(constraints)
    val patterns = generator.generateAsync()

    println("Initializing evaluator with $NUM_EVAL_SAMPLES evaluation samples...")
    val evaluator = PatternEvaluator(NUM_EVAL_SAMPLES, sampleDimensions, SAMPLE_LOW_Y_POSITION)

    println("Starting pattern evaluation...")
    val evaluatedPatterns = evaluator.evaluateAsync(patterns)

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

fun PatternGenerator.generateAsync() = produce(capacity = 200) {
    iterator().forEach { send(it) }
    close()
}

fun PatternEvaluator.evaluateAsync(patterns: ReceiveChannel<DiggingPattern>) = produce(capacity = 200) {
    repeat(8) {
        launch(coroutineContext) {
            for (p in patterns) {
                val stats = evaluate(p)
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
        println(pattern.statistics.toFullString())
    }
}

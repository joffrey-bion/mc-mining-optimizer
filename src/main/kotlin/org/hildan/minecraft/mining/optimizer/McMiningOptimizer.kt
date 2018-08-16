package org.hildan.minecraft.mining.optimizer

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.hildan.minecraft.mining.optimizer.blocks.BlockType
import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.generated.GenerationConstraints
import org.hildan.minecraft.mining.optimizer.patterns.generated.PatternGenerator
import org.hildan.minecraft.mining.optimizer.statistics.EvaluatedPattern
import org.hildan.minecraft.mining.optimizer.statistics.PatternEvaluator
import org.hildan.minecraft.mining.optimizer.statistics.PatternStore

private const val NUM_ITERATIONS = 50

private const val SAMPLE_WIDTH = 16
private const val SAMPLE_HEIGHT = 5
private const val SAMPLE_LENGTH = 16

private const val MAX_ACTIONS = 30
private const val MAX_DUG_BLOCKS = 20

fun main(vararg args: String) = runBlocking {
    val reference = Sample(SAMPLE_WIDTH, SAMPLE_HEIGHT, SAMPLE_LENGTH, BlockType.STONE)

    val constraints = GenerationConstraints(MAX_ACTIONS, MAX_DUG_BLOCKS)
    println("Starting pattern generation with constraints: $constraints")
    val generator = PatternGenerator(Sample(reference), constraints)
    val patterns = generator.generateAsync()

    println("Initializing evaluator for $NUM_ITERATIONS iterations...")
    val evaluator = PatternEvaluator(OreGenerator(), NUM_ITERATIONS, reference, 5)

    println("Starting pattern evaluation...")
    val evaluatedPatterns = evaluator.evaluateAsync(patterns)

    val store = PatternStore()
    var count = 0
    evaluatedPatterns.consumeEach {
        if (store.add(it)) {
            println(store)
        }
        println("${count++} evaluated patterns stored")
    }
    println("Finished!")

    for (pattern in store) {
        System.out.println(pattern.pattern)
        System.out.println(pattern.statistics.toFullString())
    }
}

fun PatternGenerator.generateAsync() = produce(capacity = 200) {
    iterator().forEach { send(it) }
    close()
}

fun PatternEvaluator.evaluateAsync(patterns: ReceiveChannel<DiggingPattern>) = produce(capacity = 200) {
    var count = 0
    repeat(8) {
        launch(coroutineContext) {
            for (p in patterns) {
                val stats = evaluate(p)
                println("Evaluator $it: evaluated pattern #${count++}")
                send(EvaluatedPattern(p, stats))
            }
        }
    }
}


package ispark.comet

import net.liftweb._
import http._
import actor._

import ispark.exec.SparkEvaluator

/**
 * Holds all state for one session of an open notebook.
 */
object NotebookSession extends LiftActor with ListenerManager {
  /** The object that evaluates scala/spark code */
  private val evaluator: SparkEvaluator = new SparkEvaluator

  /** Each codeChunk represents one input code chunk. */
  private var codeChunks: Vector[String] = Vector("val x=4\nval y=5")
  /** Each responseChunk represents one stdout chunk associated with a codeChunk. */
  private var responseChunks: Vector[String] = Vector("")

  /** An "update" to send to listeners (NotebookLines instances) are the chunks of code. */
  def createUpdate = (codeChunks, responseChunks)

  /** New code chunks are appended to the list. */
  override def lowPriority = {
    case codeChunk: String => {
      codeChunks :+= codeChunk
      responseChunks :+= evaluator.evaluate(codeChunk) /* Evaluate the new code. */
      updateListeners()
    }
  }
}

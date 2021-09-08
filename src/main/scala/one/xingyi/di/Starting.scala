package one.xingyi.di
import java.util.concurrent.atomic.AtomicInteger

object NonFunctional {

  type NonFunctional[From, To] = (From => To) => (From => To)

  def compose[From, To](nonFunctionalRequirements: NonFunctional[From, To]*): NonFunctional[From, To] =
    bizLogic => nonFunctionalRequirements.foldLeft(bizLogic)((acc, v) => v(acc))

  def defaultErrorStrategy[From, To]: (Exception, From) => To = (e, from) => throw e

  def nonFunctionalsClassical[From, To](fn: From => To, counter: AtomicInteger, errorStrategy: (Exception, From) => To, msgFn: (From, To) => String): NonFunctional[From, To] =
    bizLogic => addLogging(addErrorHandling(addMetrics(bizLogic, counter), errorStrategy), msgFn)

  def nonFunctionalsUsingCompose[From, To](fn: From => To, counter: AtomicInteger, errorStrategy: (Exception, From) => To, msgFn: (From, To) => String): NonFunctional[From, To] =
    compose(
      addLogging(_, msgFn),
      addErrorHandling(_,errorStrategy),
      addMetrics(_,counter))


  def addLogging[From, To](fn: From => To, msgFn: (From, To) => String): From => To =
    from => {
      val result = fn(from)
      println(msgFn(from, result))
      result
    }

  def addMetrics[From, To](fn: From => To, counter: AtomicInteger): From => To = {
    from =>
      counter.incrementAndGet()
      fn(from)
  }

  def addErrorHandling[From, To](fn: From => To, errorStrategy: (Exception, From) => To): From => To =
    from => try {fn(from) } catch {case e: Exception => errorStrategy(e, from)}

}
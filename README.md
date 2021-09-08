We have here a set of higher order functions that lift some 'business logic'.

All of them turn a function `From => To` into a new function `From => To`. The
new functions do the same as the old, and 'something else'. The something else
is the 'non business logic' item (such as logging)

There are two different styles of usage shown `nonFunctionalsClassical` and `nonFunctionalsUsingCompose`. 

At the moment all of the functions dependency inject the things they need
using method injection.

Rewrite these to use currying

So for example:

```scala
  def addLogging[From, To](fn: From => To, msgFn: (From, To) => String): From => To =
    from => {
      val result = fn(from)
      println(msgFn(from, result))
      result
    }

```
Needs to be changed so that it has a signature like this
```scala
  def addLogging[From, To]( msgFn: (From, To) => String)(fn: From => To): From => To 
```

Ask yourself why this is 'not as good' from the perspective of dependency injection 
```scala
  def addLogging[From, To](fn: From => To)( msgFn: (From, To) => String): From => To 
```

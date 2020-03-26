package de.flapdoodle.tab.fx.events

import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node

//fun <T: Event> Node.onEvent(eventType: EventType<T>) {
//  this.addEventHandler(eventType) {
//
//  }
//}

fun <T : Event> Node.handleEvent(eventType: EventType<T>, handlerBuilder: EventHandlers.Builder<T>.() -> Unit) {
  val builder = EventHandlers.Builder<T>()
  handlerBuilder(builder)
  addEventHandler(eventType, builder.build())
}

fun <T : Event> Node.filterEvent(eventType: EventType<T>, handlerBuilder: EventHandlers.Builder<T>.() -> Unit) {
  val builder = EventHandlers.Builder<T>()
  handlerBuilder(builder)
  addEventFilter(eventType, builder.build())
}

object EventHandlers {

  class RuleBuilder<T : Event>(
      private val parent: Builder<T>,
      private val preAction: (T) -> Unit,
      private val predicate: (T) -> Boolean
  ) {
    infix fun by(action: (T) -> Unit) {
      parent.add(predicate, preAction, action)
    }
  }

  class GroupBuilder<T: Event>(
      private val parent: Builder<T>,
      private val otherPredicate: (T) -> Boolean
  ) {
    infix fun then(block: GroupBuilder<T>.() -> Unit) {
      block(this)
    }

    fun consume(predicate: (T) -> Boolean): RuleBuilder<T> {
      return RuleBuilder(parent, Event::consume) { otherPredicate(it) && predicate(it) }
    }
  }

  class Builder<T : Event> {
    private var rootHandler: ChainingEventHandler<T>? = null

    fun consume(predicate: (T) -> Boolean): RuleBuilder<T> {
      return RuleBuilder(this, Event::consume, predicate)
    }

    fun matching(predicate: (T) -> Boolean): GroupBuilder<T> {
      return GroupBuilder(this,predicate)
    }

    internal fun add(
        predicate: (T) -> Boolean,
        preAction: (T) -> Unit,
        action: (T) -> Unit
    ): Builder<T> {
      val add = ChainingEventHandler(
          predicate = predicate,
          preAction = preAction,
          action = action
      )
      rootHandler = rootHandler?.append(add) ?: add
      return this
    }

    fun build(): EventHandler<in T> {
      println("-----------------------------------")
      println("---> $rootHandler")
      println("-----------------------------------")
      return rootHandler ?: throw IllegalArgumentException("no rule added")
    }
  }

  data class ChainingEventHandler<T : Event>(
      private val predicate: (T) -> Boolean,
      private val preAction: (T) -> Unit,
      private val action: (T) -> Unit,
      private val fallback: ChainingEventHandler<T>? = null
  ) : EventHandler<T> {
    override fun handle(event: T) {
      if (predicate(event)) {
        preAction(event)
        action(event)
      } else
        fallback?.handle(event)
    }

    internal fun append(last: ChainingEventHandler<T>): ChainingEventHandler<T> {
      return if (fallback ==null)
        copy(fallback = last)
      else
        copy(fallback = fallback.append(last))
    }
  }
}
package de.flapdoodle.tab.test

import de.flapdoodle.tab.fx.events.fireEventToChildren
import javafx.event.Event
import javafx.event.EventType
import javafx.scene.Node
import tornadofx.*
import java.util.UUID

class EventPropagation : View("event prop") {
  private val knownTarget = button("known") {
    logEvents()
  }

  override val root = group {
    logEvents()

    vbox {
      logEvents()

      button("close") {
        logEvents()

        setOnAction {
          close()
        }
      }
      hbox {
        logEvents()

        button("just fire") {
          logEvents()

          setOnAction {
            this@group.fireEvent(DummyEvent.Down)
          }
        }
        button("fireToKnown") {
          logEvents()

          setOnAction {
            Event.fireEvent(knownTarget, DummyEvent.Down)
          }
        }
        button("fireToChildren") {
          logEvents()

          setOnAction {
            this@group.fireEventToChildren(DummyEvent.Down)
          }
        }
        vbox {
          logEvents()

          group {
            logEvents()

            label("label") {
              logEvents()

            }
            button("child") {
              logEvents()

              setOnAction {
                fireEvent(DummyEvent.Up)
              }
            }

            this += knownTarget
          }
        }
      }
    }
  }

  private fun Node.logEvents() {
    addEventFilter(DummyEvent.ALL) {
      println("filter: $this got event $it")
    }
    addEventHandler(DummyEvent.ALL) {
      println("handler: $this got event $it")
    }
  }

  companion object {
    fun open() {
      val view = find(EventPropagation::class)
      view.openModal(stageStyle = javafx.stage.StageStyle.DECORATED)
    }
  }

  sealed class DummyEvent(eventType: EventType<DummyEvent>) : Event(eventType) {
    companion object {
      val ALL = EventType<DummyEvent>(Event.ANY, "DUMMY:"+UUID.randomUUID().toString())
    }

    object Up : DummyEvent(ALL)
    object Down : DummyEvent(ALL)
  }

}
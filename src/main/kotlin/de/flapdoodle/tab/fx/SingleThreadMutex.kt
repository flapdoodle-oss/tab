package de.flapdoodle.tab.fx

class SingleThreadMutex {
  private var switch = false

  fun tryExecute(action: () -> Unit) {
    if (!switch) {
      switch = true
      try {
        action()
      } finally {
        switch = false
      }
    }
  }

  fun execute(action: () -> Unit) {
    require(switch == false) { "mutex is locked" }
    tryExecute(action)
  }
}
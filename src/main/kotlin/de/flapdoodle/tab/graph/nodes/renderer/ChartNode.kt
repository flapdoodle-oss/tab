package de.flapdoodle.tab.graph.nodes.renderer

import javafx.scene.Parent
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*

class ChartNode() : Fragment() {

  override val root = accordion {
    this.panes += titledpane("Chart") {
      isExpanded = false
      isAnimated = false
      linechart("Unit Sales Q2 2016", CategoryAxis(), NumberAxis()) {
        minWidth = 50.0
        minHeight = 20.0
        maxHeight = 150.0

        multiseries("Product X", "Product Y") {
          data("MAR", 10245, 28443)
          data("APR", 23963, 22845)
          data("MAY", 15038, 19045)
        }

      }
    }
  }
}
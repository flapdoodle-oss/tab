package de.flapdoodle.fx.clone

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle

object BoxFactory {
    fun sampleProperties(): GraphEditorProperties {
        val graphEditorProperties = GraphEditorProperties()
        graphEditorProperties.snapToGridProperty().set(false)
        graphEditorProperties.westBoundValue = -200.0
        graphEditorProperties.eastBoundValue = 200.0
        graphEditorProperties.northBoundValue= 0.0
        graphEditorProperties.southBoundValue= 100.0
        return graphEditorProperties
    }
    
    fun sampleBox(graphEditorProperties: GraphEditorProperties): ResizableBox {
        return ResizableBox(EditorElement.NODE).apply {
//            this.content = Rectangle(200.0, 200.0, Color.RED)
            val border = Rectangle()
            val background = Rectangle()

            background.widthProperty().bind(border.widthProperty().subtract(border.strokeWidthProperty().multiply(2)))
            background.heightProperty().bind(border.heightProperty().subtract(border.strokeWidthProperty().multiply(2)))

            border.widthProperty().bind(this.widthProperty())
            border.heightProperty().bind(this.heightProperty())

            border.getStyleClass()
                .setAll("default-node-border")
            background.getStyleClass()
                .setAll("default-node-background")

            this.getChildren().addAll(border, background)
            this.setMinSize(
                30.0,
                30.0
            )
            this.setPrefSize(40.0, 40.0)
            this.resize(60.0, 60.0)
//            this.layoutX = 20.0
//            this.layoutY = 30.0

            background.addEventFilter<MouseEvent>(
                MouseEvent.MOUSE_DRAGGED,
                EventHandler { event: MouseEvent? ->
                    if (event!!.isPrimaryButtonDown && /*!isSelected()*/false) {
                        event.consume()
                    }
                })


            this.setEditorProperties(graphEditorProperties)
        }
    }
}
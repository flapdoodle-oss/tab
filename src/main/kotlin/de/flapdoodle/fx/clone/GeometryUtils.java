/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package de.flapdoodle.fx.clone;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class containing helper methods relating to geometry, positions, etc.
 */
public class GeometryUtils {

    private static final double HALF_A_PIXEL = 0.5;

    /**
     * Gets the position of the cursor relative to some node.
     *
     * @param event a {@link MouseEvent} storing the cursor position
     * @param node some {@link Node}
     *
     * @return the position of the cursor relative to the node origin
     */
    public static Point2D getCursorPosition(final MouseEvent event, final Node node)
    {
        final double sceneX = event.getSceneX();
        final double sceneY = event.getSceneY();

        final Point2D containerScene = node.localToScene(0, 0);
        return new Point2D(sceneX - containerScene.getX(), sceneY - containerScene.getY());
    }

    /**
     * Moves an x or y position value on-pixel.
     *
     * <p>
     * Lines drawn off-pixel look blurry. They should therefore have integer x and y values.
     * </p>
     *
     * @param position the position to move on-pixel
     *
     * @return the position rounded to the nearest integer
     */
    public static double moveOnPixel(final double position) {
        return Math.ceil(position);
    }

    /**
     * Moves an x or y position value off-pixel.
     *
     * <p>
     * This is for example useful for a 1-pixel-wide stroke with a stroke-type of centered. The x and y positions need
     * to be off-pixel so that the stroke is on-pixel.
     * </p>
     *
     * @param position the position to move off-pixel
     *
     * @return the position moved to the nearest value halfway between two integers
     */
    public static double moveOffPixel(final double position) {
        return Math.ceil(position) - HALF_A_PIXEL;
    }

    /**
     * Checks if the given position is between two values.
     *
     * <p>
     * Also returns true if the given position is equal to either of the values.
     * </p>
     *
     * @param firstValue an x or y position value
     * @param secondValue another x or y position value
     * @param position the cursor's position value
     *
     * @return {@code true} if the cursor position is between the two points
     */
    public static boolean checkInRange(final double firstValue, final double secondValue, final double position) {

        if (secondValue >= firstValue) {
            return firstValue <= position && position <= secondValue;
        }
        // ELSE:
        return secondValue <= position && position <= firstValue;
    }

    /**
     * Checks if a horizontal line segment AB intersects with a vertical line segment CD.
     *
     * @param a start of line segment AB
     * @param b end of line segment AB
     * @param c start of line segment CD
     * @param d end of line segment CD
     * @return {@code true} if AB and CD intersect, {@code false} otherwise
     */
    public static boolean checkIntersection(final Point2D a, final Point2D b, final Point2D c, final Point2D d) {

        if (!(c.getX() > a.getX() && c.getX() < b.getX()) && !(c.getX() > b.getX() && c.getX() < a.getX())) {
            return false;
        }

        if (!(a.getY() > c.getY() && a.getY() < d.getY()) && !(a.getY() > d.getY() && a.getY() < c.getY())) {
            return false;
        }

        return true;
    }

    /**
     * Rounds some value to the nearest multiple of the grid spacing.
     *
     * @param pProperties
     *            {@link GraphEditorProperties} or {@code null}
     * @param pValue
     *            a double value
     * @return the input value rounded to the nearest multiple of the grid
     *         spacing
     */
    public static double roundToGridSpacing(final GraphEditorProperties pProperties, final double pValue)
    {
        if (pProperties == null || !pProperties.isSnapToGridOn())
        {
            return pValue;
        }
        final double spacing = pProperties.getGridSpacing();
        return spacing * Math.round(pValue / spacing);
    }
}

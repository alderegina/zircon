package org.hexworks.zircon.api.behavior

import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.graphics.TileGraphics

/**
 * Represents an object which can contain multiple [Layer]s which are specialized
 * [TileGraphics] objects which can be displayed above each other within the [Layerable] object.
 * Indexing is done from bottom to top, eg: calling [addLayer] with a [Layerable] which
 * has only one [Layer] (at index `0`) will add the new [Layer] at index `1`.
 * [Layerable] also implements [Clearable]. In this context [clear] can be used to remove
 * all layers.
 */
interface Layerable : Clearable {

    val layers: Iterable<Layer>

    /**
     * The area this [Layerable] covers. This means that the maximum size for a [Layer] this
     * [Layerable] accepts is [Layerable.size] - [Layer.position] + [Layer.size]
     */
    val size: Size

    /**
     * Returns the [Layer] at the given [level] (if present).
     */
    fun getLayerAt(level: Int): Maybe<Layer>

    /**
     * Adds a layer on top of the currently present layers.
     */
    fun addLayer(layer: Layer)

    /**
     * Inserts the given [Layer] into this [Layerable] at the given [level].
     */
    fun insertLayerAt(level: Int, layer: Layer)

    /**
     * Sets the given [Layer] in this [Layerable] at the given [level].
     */
    fun setLayerAt(level: Int, layer: Layer)

    /**
     * Removes the given [layer] from the current layers. This method has no effect
     * if this [Layerable] doesn't contain the given [Layer].
     */
    fun removeLayer(layer: Layer)

    /**
     * Removes the [Layer] at the given [index]. This method has no effect if this [Layerable]
     * doesn't contain a [Layer] at the given [index].
     */
    fun removeLayerAt(index: Int)

    /**
     * Removes the given [layers] from this [Layerable].
     */
    fun removeLayers(layers: Collection<Layer>)

    /**
     * Removes all layers from this [Layerable].
     */
    fun removeAllLayers()
}

package org.codetome.zircon.internal.font.impl

import org.codetome.zircon.api.TextCharacter
import org.codetome.zircon.api.font.CharacterMetadata
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.font.FontTextureRegion
import org.codetome.zircon.internal.BuiltInModifiers.*
import org.codetome.zircon.internal.extensions.isNotPresent
import org.codetome.zircon.internal.font.FontRegionTransformer
import org.codetome.zircon.internal.font.MetadataPickingStrategy
import org.codetome.zircon.internal.font.cache.DefaultFontRegionCache
import org.codetome.zircon.internal.font.transformer.*
import java.awt.image.BufferedImage
import java.util.*

/**
 * Represents a font which is backed by a sprite sheet.
 */
class Java2DFont(private val source: BufferedImage,
                 private val metadata: Map<Char, List<CharacterMetadata>>,
                 private val width: Int,
                 private val height: Int,
                 private val regionTransformers: List<FontRegionTransformer>,
                 private val cache: DefaultFontRegionCache<FontTextureRegion>,
                 private val metadataPickingStrategy: MetadataPickingStrategy = PickFirstMetaStrategy())
    : Font {

    private val id = UUID.randomUUID()

    override fun getId(): UUID = id

    override fun getWidth() = width

    override fun getHeight() = height

    override fun hasDataForChar(char: Char) = metadata.containsKey(char)

    override fun fetchMetadataForChar(char: Char): List<CharacterMetadata> {
        return metadata[char] ?: listOf()
    }

    override fun fetchRegionForChar(textCharacter: TextCharacter): FontTextureRegion {
        val meta = fetchMetaFor(textCharacter)
        val maybeRegion = cache.retrieveIfPresent(textCharacter)

        var region = if (maybeRegion.isNotPresent()) {
            var image: FontTextureRegion = Java2DFontTextureRegion(source.getSubimage(meta.x * width, meta.y * height, width, height))
            regionTransformers.forEach {
                image = it.transform(image, textCharacter)
            }
            cache.store(textCharacter, image)
            image
        } else {
            maybeRegion.get()
        }
        textCharacter.getModifiers().forEach {
            region = MODIFIER_TRANSFORMER_LOOKUP[it::class]?.transform(region, textCharacter) ?: region
        }
        return region
    }

    private fun fetchMetaFor(textCharacter: TextCharacter): CharacterMetadata {
        val tags = textCharacter.getTags()
        require(hasDataForChar(textCharacter.getCharacter())) {
            "No metadata exists for character: '${textCharacter.getCharacter().toInt()}'!"
        }
        val filtered = metadata[textCharacter.getCharacter()]!!.filter { it.tags.containsAll(tags.toList()) }
        require(filtered.isNotEmpty()) {
            "No metadata found for tag(s): ${tags.joinToString()}"
        }
        return metadataPickingStrategy.pickMetadata(filtered)
    }

    companion object {
        val MODIFIER_TRANSFORMER_LOOKUP = mapOf(
                Pair(Underline::class, Java2DUnderlineTransformer()),
                Pair(VerticalFlip::class, Java2DVerticalFlipper()),
                Pair(HorizontalFlip::class, Java2DHorizontalFlipper()),
                Pair(CrossedOut::class, Java2DCrossedOutTransformer()),
                Pair(Border::class, Java2DBorderTransformer()),
                Pair(Blink::class, NoOpTransformer()),
                Pair(Hidden::class, Java2DHiddenTransformer())

        ).toMap()
    }
}
package icbm.classic.api.actions.data;

import icbm.classic.api.data.meta.MetaTag;
import lombok.Data;
import net.minecraft.util.ResourceLocation;

@Data
public final class ActionTypes {
    /** Root for all actions */
    public static final MetaTag ROOT = MetaTag.create(new ResourceLocation("icbmclassic","action"));
    
    /** Action type involving an {@link net.minecraft.entity.Entity} */
    public static final MetaTag ENTITY = MetaTag.create(ROOT, "entity");

    /** Action type involving an {@link net.minecraft.entity.Entity} */
    public static final MetaTag ENTITY_AREA = MetaTag.create(ENTITY, "area");

    /** Action type involving an {@link net.minecraft.entity.Entity} being modified/mutated. This includes healing, doing damage, changing properties, and replacement. Anything that would change entities stored in world/chunk */
    public static final MetaTag ENTITY_EDIT = MetaTag.create(ENTITY, "edit");

    /** Action type that are considered destructive, most often combined with {@link #BLAST} and {@link #PROJECTILE} to note they caused destructive/harmful side effects */
    public static final MetaTag DESTRUCTIVE = MetaTag.create(ROOT, "destructive");

    /** Action type that are considered constructive, adding to the world in a way that is considered positive... such as regrowing plants or restoring pre-battle block state */
    public static final MetaTag CONSTRUCTIVE = MetaTag.create(ROOT, "constructive");

    /** Action type involving one or more entities being spawned into the world */
    public static final MetaTag ENTITY_CREATION = MetaTag.create(ENTITY, "creation");

    /** Entity spawn action that creates projectile entities from {@link icbm.classic.api.missiles.projectile.IProjectileData} */
    public static final MetaTag PROJECTILE = MetaTag.create(ENTITY_CREATION, "projectile");

    /** Action type involving a {@link net.minecraft.world.World}. Specific to blocks/tiles with {@link net.minecraft.entity.Entity} being covered by {@link #ENTITY} */
    public static final MetaTag WORLD = MetaTag.create(ROOT, "world");

    /** Action type covering an area of effect */
    public static final MetaTag WORLD_AREA = MetaTag.create(WORLD, "area");

    /** Action type involving a {@link net.minecraft.world.World} being modified/mutated. This can be placing blocks, removing blocks, changing biomes, properties on blocks, etc. Anything that updated what is stored in a world/chunk, excluding entities. */
    public static final MetaTag WORLD_EDIT = MetaTag.create(WORLD, "edit");

    /** Action type involving a {@link net.minecraft.world.World} being modified/mutated. This can be placing blocks, removing blocks, changing biomes, properties on blocks, etc. Anything that updated what is stored in a world/chunk, excluding entities. */
    public static final MetaTag BLOCK_EDIT = MetaTag.create(WORLD_EDIT, "block");

    /** Block being added to the world */
    public static final MetaTag BLOCK_ADD = MetaTag.create(BLOCK_EDIT, "add");

    /** Block being removed from the world */
    public static final MetaTag BLOCK_REMOVE = MetaTag.create(BLOCK_EDIT, "remove");

    /** Block being modified but staying consistent as the same {@link net.minecraft.block.Block} instance but could be different {@link net.minecraft.block.state.IBlockState} or data on the {@link net.minecraft.tileentity.TileEntity} */
    public static final MetaTag BLOCK_CHANGE = MetaTag.create(BLOCK_EDIT, "change");

    /** Block being mutated into another block, this is sorta a remove and add but perceived logically as block transmuted or replaced. Think of water to stream (constructive), or machine into scrap metal (destructive) */
    public static final MetaTag BLOCK_REPLACE = MetaTag.create(BLOCK_EDIT, "replace");

    /** Actions flavored to with effects to look like explosive blasts. */
    public static final MetaTag BLAST = MetaTag.create(ROOT, "blast");
}

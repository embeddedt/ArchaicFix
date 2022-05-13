package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@Mixin(MapGenStructure.class)
public abstract class MixinMapGenStructure {
    private static boolean generatingStructures = false;
    @Shadow protected abstract void func_143027_a(World p_143027_1_);

    @Shadow protected Map structureMap;

    @Shadow protected abstract void func_143026_a(int p_143026_1_, int p_143026_2_, StructureStart p_143026_3_);

    /**
     * @author embeddedt
     * @reason Prevent CME when generating structures.
     */
    @Overwrite
    public synchronized boolean generateStructuresInChunk(World worldIn, Random rand, int chunkX, int chunkZ)
    {
        if(generatingStructures)
            return false;
        this.func_143027_a(worldIn);
        int k = (chunkX << 4) + 8;
        int l = (chunkZ << 4) + 8;
        boolean flag = false;

        generatingStructures = true;

        for (Object o : this.structureMap.values()) {
            StructureStart structurestart = (StructureStart) o;

            if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().intersectsWith(k, l, k + 15, l + 15)) {
                structurestart.generateStructure(worldIn, rand, new StructureBoundingBox(k, l, k + 15, l + 15));
                flag = true;
                this.func_143026_a(structurestart.func_143019_e(), structurestart.func_143018_f(), structurestart);
            }
        }
        generatingStructures = false;

        return flag;
    }
}

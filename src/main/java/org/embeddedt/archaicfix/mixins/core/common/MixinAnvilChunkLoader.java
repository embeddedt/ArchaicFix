package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.embeddedt.archaicfix.ArchaicFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {
    @Inject(method = "loadChunk__Async", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/RegionFileCache;getChunkInputStream(Ljava/io/File;II)Ljava/io/DataInputStream;"), remap = false)
    private void lockBeforeRead(World p_75815_1_, int p_75815_2_, int p_75815_3_, CallbackInfoReturnable<Object[]> cir) {
        ArchaicFix.REGION_FILE_LOCK.lock();
    }
    @Inject(method = "loadChunk__Async", at = @At(value = "RETURN", ordinal = 0), remap = false)
    private void unlockAfterReadNull(World p_75815_1_, int p_75815_2_, int p_75815_3_, CallbackInfoReturnable<Object[]> cir) {
        ArchaicFix.REGION_FILE_LOCK.unlock();
    }
    @Inject(method = "loadChunk__Async", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompressedStreamTools;read(Ljava/io/DataInputStream;)Lnet/minecraft/nbt/NBTTagCompound;", shift = At.Shift.AFTER), remap = false)
    private void unlockAfterRead(World p_75815_1_, int p_75815_2_, int p_75815_3_, CallbackInfoReturnable<Object[]> cir) {
        ArchaicFix.REGION_FILE_LOCK.unlock();
    }
    @Inject(method = "writeNextIO", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/AnvilChunkLoader;writeChunkNBTTags(Lnet/minecraft/world/chunk/storage/AnvilChunkLoader$PendingChunk;)V"))
    private void lockBeforeWrite(CallbackInfoReturnable<Boolean> cir) {
        ArchaicFix.REGION_FILE_LOCK.lock();
    }
    @Inject(method = "writeNextIO", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/AnvilChunkLoader;writeChunkNBTTags(Lnet/minecraft/world/chunk/storage/AnvilChunkLoader$PendingChunk;)V", shift = At.Shift.AFTER))
    private void unlockAfterWrite(CallbackInfoReturnable<Boolean> cir) {
        ArchaicFix.REGION_FILE_LOCK.unlock();
    }
    @Inject(method = "writeNextIO", at = @At(value = "INVOKE", target = "Ljava/lang/Exception;printStackTrace()V", shift = At.Shift.AFTER))
    private void unlockAfterException(CallbackInfoReturnable<Boolean> cir) {
        ArchaicFix.REGION_FILE_LOCK.unlock();
    }
}

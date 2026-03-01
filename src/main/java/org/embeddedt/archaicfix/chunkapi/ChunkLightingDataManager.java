package org.embeddedt.archaicfix.chunkapi;

import com.falsepattern.chunk.api.DataManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import org.embeddedt.archaicfix.lighting.api.IChunkLightingData;
import org.embeddedt.archaicfix.lighting.world.lighting.LightingHooks;

public class ChunkLightingDataManager implements DataManager.ChunkDataManager
{
    @Override
    public String domain() {
        return "archaicfix";
    }

    @Override
    public String id() {
        return "lighting";
    }

    @Override
    public void writeChunkToNBT(Chunk chunk, NBTTagCompound nbt) 
    {
        LightingHooks.writeNeighborLightChecksToNBT(chunk, nbt);
        nbt.setBoolean("LightPopulated", ((IChunkLightingData) chunk).isLightInitialized());
    }

    @Override
    public void readChunkFromNBT(Chunk chunk, NBTTagCompound nbt) 
    {
        LightingHooks.readNeighborLightChecksFromNBT(chunk, nbt);
        ((IChunkLightingData) chunk).setLightInitialized(nbt.getBoolean("LightPopulated"));
    }

    @Override
    public void cloneChunk(Chunk from, Chunk to) {
        ((IChunkLightingData) to).setNeighborLightChecks(((IChunkLightingData) from).getNeighborLightChecks());
        ((IChunkLightingData) to).setLightInitialized(((IChunkLightingData) from).isLightInitialized());
    }

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public String newInstallDescription() {
        return "Migrating the Light Initialization checks to a DataManager when ChunkAPI is installed.";
    }

    @Override
    public String uninstallMessage() {
        return "Removing Light Inititialization checks from a Datamanager. Shouldn't break anything.";
    }

    @Override
    public String versionChangeMessage(String priorVersion) 
    {
        return null;
    }
    
    @Override
    public boolean chunkPrivilegedAccess() {
        return true;
    }
}

package org.embeddedt.archaicfix.threadedupdates;

import org.embeddedt.archaicfix.helpers.ThreadedChunkUpdateHelper;

public interface IRendererUpdateResultHolder {

    ThreadedChunkUpdateHelper.UpdateTask.Result arch$getRendererUpdateResult();
    void arch$setRendererUpdateResult(ThreadedChunkUpdateHelper.UpdateTask.Result result);

}

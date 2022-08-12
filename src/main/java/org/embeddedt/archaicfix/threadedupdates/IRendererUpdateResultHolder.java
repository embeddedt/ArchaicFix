package org.embeddedt.archaicfix.threadedupdates;

import org.embeddedt.archaicfix.helpers.ThreadedChunkUpdateHelper;

public interface IRendererUpdateResultHolder {

    ThreadedChunkUpdateHelper.UpdateTask arch$getRendererUpdateTask();

}

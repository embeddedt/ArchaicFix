package org.embeddedt.archaicfix.helpers;

import net.minecraft.world.NextTickListEntry;

import java.util.HashSet;
import java.util.Set;

public class WorldServerHelper {
    public static final class NextTickListEntryWrapper {
        Object entry;

        public NextTickListEntryWrapper() {}

        public NextTickListEntryWrapper(Object entry) {
            this.entry = entry;
        }

        @Override
        public int hashCode() {
            return entry.hashCode();
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object entry) {
            return this.entry.equals(((NextTickListEntryWrapper) entry).entry);
        }
    }

    @SuppressWarnings("serial")
    public static final class NextTickListEntryHashSet extends HashSet<NextTickListEntry> {
        private final transient Set<NextTickListEntryWrapper> backingSet = new HashSet<>();
        private final transient NextTickListEntryWrapper wrapper = new NextTickListEntryWrapper();

        @Override
        public int size() {
            return backingSet.size();
        }

        @Override
        public boolean contains(Object entry) {
            wrapper.entry = entry;
            return backingSet.contains(wrapper);
        }

        @Override
        public boolean add(NextTickListEntry entry) {
            return backingSet.add(new NextTickListEntryWrapper(entry));
        }

        @Override
        public boolean remove(Object entry) {
            wrapper.entry = entry;
            return backingSet.remove(wrapper);
        }

        public static HashSet newHashSet() {
            return new NextTickListEntryHashSet();
        }
    }
}

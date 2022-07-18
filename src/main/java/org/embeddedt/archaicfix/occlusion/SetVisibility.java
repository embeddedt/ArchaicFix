package org.embeddedt.archaicfix.occlusion;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.util.EnumFacing;

public class SetVisibility {

	private static final int COUNT_FACES = EnumFacing.values().length;
	private final BitSet bitSet;
	private boolean isAllVisibleTrue;
	private boolean isAllVisibleFalse;

	public SetVisibility() {

		this.bitSet = new BitSet(COUNT_FACES * COUNT_FACES);
	}

	public void setManyVisible(Set<EnumFacing> faces) {

		Iterator<EnumFacing> iterator = faces.iterator();

		while (iterator.hasNext()) {
			EnumFacing enumfacing = iterator.next();
			Iterator<EnumFacing> iterator1 = faces.iterator();

			while (iterator1.hasNext()) {
				EnumFacing enumfacing1 = iterator1.next();
				setVisible(enumfacing, enumfacing1, true);
			}
		}
	}

	public void setVisible(EnumFacing from, EnumFacing to, boolean visible) {

		bitSet.set(from.ordinal() + to.ordinal() * COUNT_FACES, visible);
		bitSet.set(to.ordinal() + from.ordinal() * COUNT_FACES, visible);
		updateIsAllVisible();
	}

	public void setAllVisible(boolean visible) {

		bitSet.set(0, bitSet.size(), visible);
		updateIsAllVisible();
	}

	public boolean isAllVisible(boolean visible) {
		return visible ? isAllVisibleTrue : isAllVisibleFalse;
	}

	private void updateIsAllVisible() {
		int iTrue = bitSet.nextClearBit(0);
		isAllVisibleTrue = iTrue < 0 || iTrue >= (COUNT_FACES * COUNT_FACES);

		int iFalse = bitSet.nextSetBit(0);
		isAllVisibleFalse = iFalse < 0 || iFalse >= (COUNT_FACES * COUNT_FACES);
	}

	public boolean isVisible(EnumFacing from, EnumFacing to) {

		return from == null || to == null ? true : bitSet.get(from.ordinal() + to.ordinal() * COUNT_FACES);
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof SetVisibility) {
			return ((SetVisibility) o).bitSet.equals(bitSet);
		}
		return false;
	}

	@Override
	public int hashCode() {

		return bitSet.hashCode();
	}

	@Override
	public SetVisibility clone() {

		SetVisibility r = new SetVisibility();
		r.bitSet.or(bitSet);
		return r;
	}

	@Override
	public String toString() {

		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(' ');
		EnumFacing[] aenumfacing = EnumFacing.values();
		int i = aenumfacing.length;
		int j;
		EnumFacing enumfacing;

		for (j = 0; j < i; ++j) {
			enumfacing = aenumfacing[j];
			stringbuilder.append(' ').append(enumFacingToStringFixed(enumfacing).toUpperCase().charAt(0));
		}

		stringbuilder.append('\n');
		aenumfacing = EnumFacing.values();
		i = aenumfacing.length;

		for (j = 0; j < i; ++j) {
			enumfacing = aenumfacing[j];
			stringbuilder.append(enumFacingToStringFixed(enumfacing).toUpperCase().charAt(0));
			EnumFacing[] aenumfacing1 = EnumFacing.values();
			int k = aenumfacing1.length;

			for (int l = 0; l < k; ++l) {
				EnumFacing enumfacing1 = aenumfacing1[l];

				if (enumfacing == enumfacing1) {
					stringbuilder.append("  ");
				} else {
					boolean flag = this.isVisible(enumfacing, enumfacing1);
					stringbuilder.append(' ').append(flag ? 'Y' : 'n');
				}
			}

			stringbuilder.append('\n');
		}

		return stringbuilder.toString();
	}

	// Do not trust MCP.
	private static String enumFacingToStringFixed(EnumFacing f) {
		return new String[]{"DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST"}[f.ordinal()];
	}

}

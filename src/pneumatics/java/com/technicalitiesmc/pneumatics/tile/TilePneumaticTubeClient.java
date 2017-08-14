package com.technicalitiesmc.pneumatics.tile;

import java.util.EnumSet;
import java.util.Set;

import com.technicalitiesmc.api.pneumatics.EnumTubeDirection;
import com.technicalitiesmc.api.pneumatics.IPneumaticTube;
import com.technicalitiesmc.api.pneumatics.ITubeStack;
import com.technicalitiesmc.api.pneumatics.TubeModule;
import com.technicalitiesmc.lib.client.SpecialRenderer;
import com.technicalitiesmc.pneumatics.client.TESRPneumaticTube;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

@SpecialRenderer(TESRPneumaticTube.class)
public class TilePneumaticTubeClient extends TilePneumaticTubeBase {

    private final Set<EnumFacing> connections = EnumSet.noneOf(EnumFacing.class);

    @Override
    public <T extends TubeModule> boolean setModule(EnumFacing face, TubeModule.Type<T> type) {
        return true;
    }

    @Override
    public IPneumaticTube.INeighbor getNeighbor(EnumFacing side) {
        return null;
    }

    @Override
    public boolean isConnected(EnumFacing side) {
        return connections.contains(side);
    }

    @Override
    public void insertStack(EnumFacing side, float position, EnumTubeDirection direction, ItemStack stack, EnumDyeColor color) {
    }

    @Override
    public void removeStack(ITubeStack stack) {
    }

    @Override
    public void readDescription(NBTTagCompound tag) {
        super.readDescription(tag);

        int connections = tag.getInteger("connections");
        for (EnumFacing f : EnumFacing.VALUES) {
            if ((connections & (1 << f.ordinal())) != 0) {
                this.connections.add(f);
            } else {
                this.connections.remove(f);
            }
        }
    }

    @Override
    public void readUpdateExtra(PacketBuffer buf) {
        super.readUpdateExtra(buf);

        int connections = buf.readInt();
        for (EnumFacing face : EnumFacing.VALUES) {
            if ((connections & (1 << face.ordinal())) != 0) {
                this.connections.add(face);
            } else {
                this.connections.remove(face);
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().grow(0.25);
    }

}
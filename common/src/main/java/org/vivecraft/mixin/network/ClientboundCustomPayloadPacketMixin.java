package org.vivecraft.mixin.network;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.vivecraft.common.network.CommonNetworkHelper;
import org.vivecraft.common.network.packets.VivecraftDataPacket;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = {ClientboundCustomPayloadPacket.class})
public class ClientboundCustomPayloadPacketMixin {

    /**
     * catches the vivecraft client bound packets so that they don't get discarded.
     * Neoforge handles that with the network events in {@link org.vivecraft.neoforge.event.ClientEvents#handleVivePacket}
     */
    /*@Inject(at = @At("HEAD"), method = "readPayload(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;", cancellable = true)
    private static void vivecraft$catchVivecraftPackets(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf, CallbackInfoReturnable<CustomPacketPayload> cir) {
        if (CommonNetworkHelper.CHANNEL.equals(resourceLocation)) {
            cir.setReturnValue(new VivecraftDataPacket(friendlyByteBuf));
        }
    }*/

    @WrapOperation(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;", ordinal = 0), method = "<clinit>")
    private static <E> ArrayList<E> vivecraft$addPacket(E[] elements, Operation<ArrayList<E>> original) {
        return original.call(new Object[]{ArrayUtils.add(elements,
            new CustomPacketPayload.TypeAndCodec<>(VivecraftDataPacket.TYPE, VivecraftDataPacket.STREAM_CODEC))});
    }
}

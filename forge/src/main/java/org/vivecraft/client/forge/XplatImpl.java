package org.vivecraft.client.forge;

import com.mojang.blaze3d.pipeline.RenderTarget;
import io.netty.buffer.Unpooled;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkDirection;
import org.lwjgl.glfw.GLFW;
import org.vivecraft.client.Xplat;
import org.vivecraft.common.network.packet.c2s.VivecraftPayloadC2S;
import org.vivecraft.common.network.packet.s2c.VivecraftPayloadS2C;
import org.vivecraft.forge.Vivecraft;

import java.nio.file.Path;

public class XplatImpl implements Xplat {

    public static void init() {}

    public static boolean isModLoaded(String name) {
        return FMLLoader.getLoadingModList().getModFileById(name) != null;
    }

    public static Path getConfigPath(String fileName) {
        return FMLPaths.CONFIGDIR.get().resolve(fileName);
    }

    public static boolean isDedicatedServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    public static Xplat.ModLoader getModloader() {
        return Xplat.ModLoader.FORGE;
    }

    public static String getModVersion() {
        if (isModLoadedSuccess()) {
            return FMLLoader.getLoadingModList().getModFileById("vivecraft").versionString();
        }
        return "no version";
    }

    public static boolean isModLoadedSuccess() {
        return FMLLoader.getLoadingModList().getModFileById("vivecraft") != null;
    }

    public static boolean enableRenderTargetStencil(RenderTarget renderTarget) {
        renderTarget.enableStencil();
        return true;
    }

    public static Path getJarPath() {
        return FMLLoader.getLoadingModList().getModFileById("vivecraft").getFile().getSecureJar().getPath("/");
    }

    public static String getUseMethodName() {
        return "useWithoutItem";
    }

    public static TextureAtlasSprite[] getFluidTextures(
        BlockAndTintGetter level, BlockPos pos, FluidState fluidStateIn)
    {
        return ForgeHooksClient.getFluidSprites(level, pos, fluidStateIn);
    }

    public static Biome.ClimateSettings getBiomeClimateSettings(Biome biome) {
        return biome.getModifiedClimateSettings();
    }

    public static BiomeSpecialEffects getBiomeEffects(Biome biome) {
        return biome.getModifiedSpecialEffects();
    }

    public static boolean serverAcceptsPacket(ClientPacketListener connection, ResourceLocation id) {
        return true;
    }

    public static Packet<?> getC2SPacket(VivecraftPayloadC2S payload) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        payload.write(buffer);
        return NetworkDirection.PLAY_TO_SERVER.buildPacket(Vivecraft.VIVECRAFT_NETWORK_CHANNEL, buffer);
    }

    public static Packet<?> getS2CPacket(VivecraftPayloadS2C payload) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        payload.write(buffer);
        return NetworkDirection.PLAY_TO_CLIENT.buildPacket(Vivecraft.VIVECRAFT_NETWORK_CHANNEL, buffer);
    }

    public static boolean hasKeyModifier(KeyMapping keyMapping) {
        return keyMapping.getKeyModifier() != KeyModifier.NONE;
    }

    public static int getKeyModifier(KeyMapping keyMapping) {
        return switch (keyMapping.getKeyModifier()) {
            case SHIFT -> GLFW.GLFW_MOD_SHIFT;
            case ALT -> GLFW.GLFW_MOD_ALT;
            case CONTROL -> GLFW.GLFW_MOD_CONTROL;
            default -> 0;
        };
    }

    public static int getKeyModifierKey(KeyMapping keyMapping) {
        return switch (keyMapping.getKeyModifier()) {
            case SHIFT -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case ALT -> GLFW.GLFW_KEY_RIGHT_ALT;
            case CONTROL -> GLFW.GLFW_KEY_LEFT_CONTROL;
            default -> -1;
        };
    }
}

package org.vivecraft.mixin.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client.gui.screens.UpdateScreen;
import org.vivecraft.client.utils.UpdateChecker;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Unique
    private Button vivecraft$vrModeButton;
    @Unique
    private Button vivecraft$updateButton;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", shift = At.Shift.AFTER, ordinal = 1), method = "createNormalMenuOptions")
    public void vivecraft$initFullGame(CallbackInfo ci) {
        vivecraft$addVRModeButton();
    }

    @Inject(at = @At("TAIL"), method = "createDemoMenuOptions")
    public void vivecraft$initDemo(CallbackInfo ci) {
        vivecraft$addVRModeButton();
    }

    @Unique
    private void vivecraft$addVRModeButton() {

        vivecraft$vrModeButton = new Button(
            this.width / 2 + 104, this.height / 4 + 72,
            56, 20,
            Component.translatable("vivecraft.gui.vr", VRState.vrEnabled ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF),
            (button) -> {
                VRState.vrEnabled = !VRState.vrEnabled;
                ClientDataHolderVR.getInstance().vrSettings.vrEnabled = VRState.vrEnabled;
                ClientDataHolderVR.getInstance().vrSettings.saveOptions();
                button.setMessage(Component.translatable("vivecraft.gui.vr", VRState.vrEnabled ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF));
            });

        vivecraft$vrModeButton.visible = ClientDataHolderVR.getInstance().vrSettings.vrToggleButtonEnabled;

        this.addRenderableWidget(vivecraft$vrModeButton);

        vivecraft$updateButton = new Button(
            this.width / 2 + 104, this.height / 4 + 96,
            56, 20,
            new TranslatableComponent("vivecraft.gui.update"),
            (button) -> minecraft.setScreen(new UpdateScreen()));

        vivecraft$updateButton.visible = UpdateChecker.hasUpdate;

        this.addRenderableWidget(vivecraft$updateButton);
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void vivecraft$renderToolTip(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        vivecraft$updateButton.visible = UpdateChecker.hasUpdate;

        if (vivecraft$vrModeButton.visible && vivecraft$vrModeButton.isMouseOver(i, j)) {
            renderTooltip(poseStack, font.split(new TranslatableComponent("vivecraft.options.VR_MODE.tooltip"), Math.max(width / 2 - 43, 170)), i, j);
        }
        if (VRState.vrInitialized && !VRState.vrRunning) {
            Component hotswitchMessage = new TranslatableComponent("vivecraft.messages.vrhotswitchinginfo");
            renderTooltip(poseStack, font.split(hotswitchMessage, 280), width / 2 - 140 - 12, 17);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V"), method = "render")
    public void vivecraft$maybeNoPanorama(PanoramaRenderer instance, float f, float g) {
        if (VRState.vrRunning && ClientDataHolderVR.getInstance().menuWorldRenderer.isReady()) {
            return;
        }
        instance.render(f, g);
    }
}

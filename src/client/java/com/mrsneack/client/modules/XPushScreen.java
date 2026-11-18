package com.mrsneack.client.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import com.mrsneack.client.XpushClient;
import net.minecraft.util.Formatting;

public class XPushScreen extends Screen {

    public CheckboxWidget EnabledCheckbox;
    public DelaySlider slider;
    private Text enabledText;
    public CheckboxWidget TimingJitterCheckbox;

    public XPushLogger LOGGER = new XPushLogger();

    // creating XPush Screen
    public XPushScreen() {
        super(Text.translatable("text.xpush.menu"));
        enabledText = null;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = width / 2;
        int centerY = height / 2;

        // Create Checkbox
        this.EnabledCheckbox = CheckboxWidget.builder(
                Text.translatable("checkbox.xpush.enable"),
                this.textRenderer
        ).pos(centerX - 100, centerY - 55).checked(XpushClient.config.enabled).callback((checkbox, checked) -> {
            XpushClient.config.enabled = checked;
            XpushClient.saveManager.save();
            if (checked) {
                LOGGER.INFO("[XPush] Enabled from GUI");
            } else {
                LOGGER.INFO("[XPush] Disabled from GUI");
            }
        }).build();

        this.addDrawableChild(this.EnabledCheckbox);

        // Create Timing Jitter Checkbox
        this.TimingJitterCheckbox = CheckboxWidget.builder(
                Text.of("Enable Timing Jitter"),
                this.textRenderer
        ).pos(centerX - 100, centerY - 27).checked(XpushClient.config.enabledJitter).callback((checkbox, enabledJitter) -> {
            XpushClient.config.enabledJitter = enabledJitter;
            XpushClient.saveManager.save();
            if (enabledJitter) {
                LOGGER.INFO("[XPush] Enabled Timing Jitter");
            } else {
                LOGGER.INFO("[XPush] Disabled Timing Jitter");
            }
        }).build();

        this.addDrawableChild(this.TimingJitterCheckbox);

        // creating custom slider
        this.slider = new DelaySlider(
                centerX - 100,
                centerY + 1,
                200,
                20,
                Text.translatable("text.xpush.delay"),
                (XpushClient.config.delay - 1) / 19.0,
                value -> XpushClient.config.delay = value
        );

        this.addDrawableChild(this.slider);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.literal("XPush Menu"), this.width / 2, this.height / 2 - 90, 0xFFFFFFFF
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.translatable("text.xpush.delay").append(String.valueOf(XpushClient.config.delay)), this.width / 2, this.height / 2 + 27, 0xFFFFFFFF
        );

        if (XpushClient.config.enabled) {
            enabledText = Text.translatable("text.xpush.enabled").formatted(Formatting.GREEN);
        } else {
            enabledText = Text.translatable("text.xpush.disabled").formatted(Formatting.RED);
        }

        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.translatable("text.xpush.status").append(enabledText), this.width / 2, this.height / 2 + 43, 0xFFFFFFFF
        );
    }

    public static class DelaySlider extends SliderWidget {

        private final java.util.function.IntConsumer onChange;

        public DelaySlider(int x, int y, int width, int height, Text text, double value, java.util.function.IntConsumer onChange) {
            super(x, y, width, height, text, value);
            this.onChange = onChange;
            updateMessage();
        }

        private int getDelay() {
            return 1 + (int) Math.round(this.value * 19.0);
        }

        @Override
        public void updateMessage() {
            this.setMessage(
                    Text.translatable("text.xpush.delay").append(String.valueOf(this.getDelay()))
            );
        }

        @Override
        protected void applyValue() {
            int delay = this.getDelay();

            this.value = (delay - 1) / 19.0;

            this.onChange.accept(delay);
            XpushClient.config.delay = delay;
            XpushClient.saveManager.save();
            updateMessage();
        }
    }
}

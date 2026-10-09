package com.mrsneack.client.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import com.mrsneack.client.XpushClient;
import net.minecraft.util.Formatting;

import java.awt.*;

public class XPushScreen extends Screen {

    public CheckboxWidget checkbox;

    public DelaySlider slider;

    private Text text;
    // creating XPush Screen
    public XPushScreen() {
        super(Text.of("XPush Menu"));
        text = null;
    }

    @Override
    protected void init() {
        // initialize init method
        super.init();

        // save center X and Y to variable
        int centerX = width / 2;
        int centerY = height / 2;

        // Create Checkbox
        this.checkbox = CheckboxWidget.builder(
                Text.translatable("checkbox.xpush.enable"),
                this.textRenderer

        ).pos(centerX - 100, centerY - 50).checked(XpushClient.config.enabled).callback((checkbox, checked) -> {
            XpushClient.config.enabled = checked;
            XpushClient.saveManager.save();
            if (checked) {
                System.out.println("[XPush] Enabled from GUI");
            } else {
                System.out.println("[XPush] Disabled from GUI");
            }
        }).build();

        // adding checkbox to render context
        this.addDrawableChild(this.checkbox);

        // creating custom slider
        this.slider = new DelaySlider(
            centerX - 100,
            centerY - 20,
            200,
            20,
            Text.translatable("text.xpush.delay"),
            (XpushClient.config.delay - 1) / 19.0,
            value -> {
                XpushClient.config.delay = value;
                XpushClient.saveManager.save();
            }
        );
        // add custom slider to render context
        this.addDrawableChild(this.slider);

    }
    @Override
    // render method
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        // initialize render
        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );

        // creating menu title
        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.literal("XPush Menu"), this.width / 2, this.height / 2 - 90, 0xFFFFFFFF
        );
        // creating delay static text
        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.translatable("text.xpush.delay").append(String.valueOf(XpushClient.config.delay)), this.width / 2, this.height / 2 + 25, 0xFFFFFFFF
        );
        if(XpushClient.config.enabled) {
            text = Text.translatable("text.xpush.enabled").formatted(Formatting.GREEN);
        }if(!XpushClient.config.enabled) {
            text = Text.translatable("text.xpush.disabled").formatted(Formatting.RED);
        }
        // creating enabled static text
        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.translatable("text.xpush.status").append(text), this.width / 2, this.height / 2 + 40, 0xFFFFFFFF
        );
    }
    // custom slider method
    public static class DelaySlider extends SliderWidget {

        private final java.util.function.IntConsumer onChange;
        // creating custom slider event
        public DelaySlider(int x, int y, int width, int height, Text text, double value, java.util.function.IntConsumer onChange) {
            super(x ,y , width, height, text, value);
            this.onChange = onChange;
            updateMessage();
        }

        // event which return specified delay
        private int getDelay() {
            return 1 + (int) Math.round(this.value * 19.0);
        }
        @Override
        // event which update in slider text
        public void updateMessage() {
            this.setMessage(
                    Text.translatable("text.xpush.delay").append(String.valueOf(this.getDelay()))
            );
        }

        // event which specified selected delay
        @Override
        protected void applyValue() {
            int delay =  this.getDelay();

            this.value = (delay - 1) / 19.0;

            this.onChange.accept(delay);

            updateMessage();
        }
    }
}

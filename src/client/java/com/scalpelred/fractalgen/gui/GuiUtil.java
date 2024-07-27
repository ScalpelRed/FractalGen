package com.scalpelred.fractalgen.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;

public class GuiUtil {

    public static final int BUTTON_HEIGHT = 20;
    public static final int BUTTON_WIDTH = 200;

    public static boolean canParseDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean canParseInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean verifyDouble(TextFieldWidget text) {
        try {
            double res = Double.parseDouble(text.getText());
            text.setEditableColor(0xFFFFFF);
            return true;
        }
        catch (NumberFormatException e) {
            text.setEditableColor(0xFF0000);
            return false;
        }
    }

}

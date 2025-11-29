package me.filizes.adventuretime.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    public static List<Text> wrapText(MutableText text, int maxLength) {
        List<Text> wrappedLines = new ArrayList<>();
        String fullText = text.getString();
        Style style = text.getStyle();

        if (fullText.isEmpty()) {
            wrappedLines.add(Text.empty().setStyle(style));
            return wrappedLines;
        }

        if (fullText.length() <= maxLength) {
            wrappedLines.add(text);
            return wrappedLines;
        }

        StringBuilder currentLine = new StringBuilder();
        String[] words = fullText.split("\\s+");
        for (String word : words) {
            if (word.isEmpty() && currentLine.isEmpty()) continue;

            int addedLength = currentLine.isEmpty() ? word.length() : currentLine.length() + 1 + word.length();
            if (addedLength > maxLength) {
                if (!currentLine.isEmpty()) {
                    wrappedLines.add(Text.literal(currentLine.toString()).setStyle(style));
                } else {
                    while (word.length() > maxLength) {
                        wrappedLines.add(Text.literal(word.substring(0, maxLength)).setStyle(style));
                        word = word.substring(maxLength);
                    }
                }
                currentLine = new StringBuilder(word);
            } else {
                if (!currentLine.isEmpty()) currentLine.append(" ");
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            wrappedLines.add(Text.literal(currentLine.toString()).setStyle(style));
        }

        return wrappedLines.isEmpty() ? List.of(Text.empty().setStyle(style)) : wrappedLines;
    }
}
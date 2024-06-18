package renektoff.refabricated_necessities.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    private final static int lineCharacterLimit = 40;

    public static List<MutableText> wrapText(String text) {
        var result = new ArrayList<MutableText>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        var words = text.split(" ");

        var currentLine = MutableText.of(TextContent.EMPTY);
        var currentCharacters = 0;

        result.add(currentLine);

        for (var word : words) {
            if (currentCharacters >= lineCharacterLimit) {
                currentLine = MutableText.of(TextContent.EMPTY);
                currentCharacters = 0;

                result.add(currentLine);
            }

            if (currentCharacters != 0) {
                currentLine.append(" ");
            }

            currentLine.append(word);

            currentCharacters += 1 + word.length();
        }

        return result;
    }
}

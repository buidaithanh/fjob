package vn.baymax.fjob.util;

import java.text.Normalizer;

public class TextUtil {

    public static String normalize(String text) {

        if (text == null)
            return "";

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

}
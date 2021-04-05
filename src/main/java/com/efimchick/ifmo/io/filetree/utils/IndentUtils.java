package com.efimchick.ifmo.io.filetree.utils;

import com.efimchick.ifmo.io.filetree.entity.PathWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static com.efimchick.ifmo.io.filetree.constants.FileTreeConstants.INDENT;
import static com.efimchick.ifmo.io.filetree.constants.FileTreeConstants.SPACE_CHAR;
import static com.efimchick.ifmo.io.filetree.constants.GraphicConstants.CROSS;
import static com.efimchick.ifmo.io.filetree.constants.GraphicConstants.LAST;
import static com.efimchick.ifmo.io.filetree.constants.GraphicConstants.LAST_IN_BRANCH;
import static com.efimchick.ifmo.io.filetree.constants.GraphicConstants.LINE;
import static com.efimchick.ifmo.io.filetree.constants.GraphicConstants.OPENING_BRANCH;

public class IndentUtils {
    private static Set<Integer> openedBranches = new HashSet<>();

    public static void addIndent(PathWrapper path, StringBuilder stringBuilder) {
        String indent = makeIndent(path);
        refreshListOfOpenedBranches(indent);
        indent = addOpenedBranches(indent);
        stringBuilder.append(indent);
    }

    private static String makeIndent(PathWrapper pathWrapper) {
        return INDENT.repeat(Math.max(0, pathWrapper.getNestingLevel() - 1)) +
                (pathWrapper.isLast() ? LAST : CROSS);
    }

    private static void refreshListOfOpenedBranches(String indent) {
        char[] chars = indent.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == OPENING_BRANCH) {
                openedBranches.add(i);
            } else if (chars[i] == LAST_IN_BRANCH) {
                openedBranches.remove(i);
            }
        }
    }

    private static String addOpenedBranches(String indent) {
        char[] chars = indent.toCharArray();
        IntStream.range(0, chars.length)
                .filter(i -> openedBranches.contains(i) && chars[i] == SPACE_CHAR)
                .forEach(i -> chars[i] = LINE);

        return String.valueOf(chars);
    }
}
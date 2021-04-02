package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static com.efimchick.ifmo.io.filetree.FileTreeConstants.BYTES;

import static com.efimchick.ifmo.io.filetree.FileTreeConstants.INDENT;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.SPACE;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.SPACE_CHAR;
import static com.efimchick.ifmo.io.filetree.GraphicConstants.CROSS;
import static com.efimchick.ifmo.io.filetree.GraphicConstants.LAST;
import static com.efimchick.ifmo.io.filetree.GraphicConstants.LAST_IN_BRANCH;
import static com.efimchick.ifmo.io.filetree.GraphicConstants.LINE;
import static com.efimchick.ifmo.io.filetree.GraphicConstants.OPENING_BRANCH;

public class FileTreeImpl implements FileTree {
    private static Set<Integer> openedBranches = new HashSet<>();
    private static Location location;

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || Files.notExists(path)) {
            return Optional.empty();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            PathWrapper pathWrapper = new PathWrapper(path, 0);
            makeTree(pathWrapper, stringBuilder);
            return Optional.of(stringBuilder.toString());
        }
    }

    public static void makeTree(PathWrapper pathWrapper, StringBuilder stringBuilder) {
        if (Files.isRegularFile(pathWrapper.path())) {
            location = new File();
        } else if (Files.isDirectory(pathWrapper.path())) {
            location = new Folder();
        }

        location.getTree(pathWrapper, stringBuilder);
    }

    public static void convertFileToString(PathWrapper pathWrapper, StringBuilder stringBuilder) {
        if (pathWrapper.isNested()) {
            addIndent(pathWrapper, stringBuilder);
        }
        long size;
        try {
            size = location.getSize(pathWrapper.path());
        } catch (IOException e) {
            throw new RuntimeException("Something is wrong at getSize");
        }

        stringBuilder.append(pathWrapper.path().getFileName())
                .append(SPACE)
                .append(size)
                .append(SPACE)
                .append(BYTES)
                .append("\n");
    }

    private static void addIndent(PathWrapper path, StringBuilder stringBuilder) {
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
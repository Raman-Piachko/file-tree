package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.efimchick.ifmo.io.filetree.FileTreeConstants.BYTES;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.CROSS;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.INDENT;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.LAST;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.LAST_IN_BRANCH;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.LINE;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.NEW_LINE;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.OPENING_BRANCH;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.SPACE;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.SPACE_CHAR;
import static com.efimchick.ifmo.io.filetree.FileTreeConstants.ZERO;

public class FileTreeImpl implements FileTree {
    private Set<Integer> openedBranches = new HashSet<>();

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || Files.notExists(path)) {
            return Optional.empty();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            PathWrapper pathWrapper = wrapPath(path, ZERO);
            getTree(pathWrapper, stringBuilder);
            return Optional.of(stringBuilder.toString());
        }
    }

    private void getTree(PathWrapper pathWrapper, StringBuilder stringBuilder) {
        if (Files.isRegularFile(pathWrapper.path())) {
            convertFileToString(pathWrapper, stringBuilder);
        } else if (Files.isDirectory(pathWrapper.path())) {
            convertFileToString(pathWrapper, stringBuilder);
            List<Path> paths = getFilesListSorted(pathWrapper.path());
            List<PathWrapper> pathWrappers = wrapList(paths, pathWrapper.getNestingLevel() + 1);
            pathWrappers.forEach(file -> getTree(file, stringBuilder));
            pathWrappers.stream().mapToInt(pathWrapper1 -> pathWrapper.getNestingLevel()).forEach(System.out::println);
        }
    }

    private PathWrapper wrapPath(Path path, int nesting) {
        return new PathWrapper(path, nesting);
    }

    private List<PathWrapper> wrapList(List<Path> paths, int nesting) {
        List<PathWrapper> pathWrappers = paths.stream()
                .map(path -> wrapPath(path, nesting))
                .collect(Collectors.toList());

        markLastElement(pathWrappers);

        return pathWrappers;
    }

    private void markLastElement(List<PathWrapper> pathWrappers) {
        pathWrappers.get(pathWrappers.size() - 1).makeLast();
    }

    private List<Path> getFilesListSorted(Path path) {
        try {
            return Files.list(path)
                    .sorted(new FileNameComparator())
                    .sorted(new FileDirectoryComparator())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Something is wrong at sorting");
        }
    }

    private void convertFileToString(PathWrapper path, StringBuilder stringBuilder) {
        if (path.isNested()) {
            addIndent(path, stringBuilder);
        }
        try {
            long size = Files.isDirectory(path.path()) ?
                    getDirSize(path.path()) : Files.size(path.path());
            stringBuilder.append(path.path().getFileName())
                    .append(SPACE)
                    .append(size)
                    .append(SPACE)
                    .append(BYTES)
                    .append(NEW_LINE);
        } catch (IOException e) {
            throw new RuntimeException("Something is wrong at converting");
        }
    }

    private void addIndent(PathWrapper path, StringBuilder stringBuilder) {
        String indent = makeIndent(path);
        refreshListOfOpenedBranches(indent);
        indent = addOpenedBranches(indent);
        stringBuilder.append(indent);
    }

    private String makeIndent(PathWrapper pathWrapper) {
        return INDENT.repeat(Math.max(0, pathWrapper.getNestingLevel() - 1)) +
                (pathWrapper.isLast() ? LAST : CROSS);
    }

    private void refreshListOfOpenedBranches(String indent) {
        char[] chars = indent.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == OPENING_BRANCH) {
                openedBranches.add(i);
            } else if (chars[i] == LAST_IN_BRANCH) {
                openedBranches.remove(i);
            }
        }
    }

    private String addOpenedBranches(String indent) {
        char[] chars = indent.toCharArray();
        IntStream.range(0, chars.length)
                .filter(i -> openedBranches.contains(i) && chars[i] == SPACE_CHAR)
                .forEach(i -> chars[i] = LINE);

        return String.valueOf(chars);
    }

    private long getDirSize(Path path) throws IOException {
        List<Path> files = Files.list(path)
                .collect(Collectors.toList());
        long size = 0;

        for (Path file : files) {
            if (Files.isRegularFile(file)) {
                size += Files.size(file);
            } else if (Files.isDirectory(file)) {
                size += getDirSize(file);
            }
        }

        return size;
    }
}
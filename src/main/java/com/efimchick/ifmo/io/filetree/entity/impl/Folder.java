package com.efimchick.ifmo.io.filetree.entity.impl;

import com.efimchick.ifmo.io.filetree.comparators.FileDirectoryComparator;
import com.efimchick.ifmo.io.filetree.comparators.FileNameComparator;
import com.efimchick.ifmo.io.filetree.entity.Location;
import com.efimchick.ifmo.io.filetree.entity.PathWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.efimchick.ifmo.io.filetree.constants.FileTreeConstants.BYTES;
import static com.efimchick.ifmo.io.filetree.constants.FileTreeConstants.SPACE;
import static com.efimchick.ifmo.io.filetree.utils.IndentUtils.addIndent;

public class Folder implements Location {
    @Override
    public void convertPathToString(PathWrapper pathWrapper, StringBuilder stringBuilder) {
        try {
            convertPath(pathWrapper, stringBuilder, getSize(pathWrapper.path()));
        } catch (IOException e) {
            throw new RuntimeException("Something is wrong at getSize", e);
        }

        List<Path> innerPaths = getFilesListSorted(pathWrapper.path());
        List<PathWrapper> pathWrappers = wrapList(innerPaths, pathWrapper.getNestingLevel() + 1);

        pathWrappers.forEach(pathWrapper1 ->
                checkPathAndConvert(pathWrapper1).convertPathToString(pathWrapper1, stringBuilder));
    }

    @Override
    public long getSize(Path path) throws IOException {
        List<Path> files = Files.list(path)
                .collect(Collectors.toList());
        Location location = null;
        long size = 0;

        for (Path file : files) {
            if (Files.isRegularFile(file)) {
                location = new File();
            } else if (Files.isDirectory(file)) {
                location = new Folder();
            }
            size += location.getSize(file);
        }

        return size;
    }

    private Location checkPathAndConvert(PathWrapper pathWrapper1) {
        Location location = null;
        if (Files.isRegularFile(pathWrapper1.path())) {
            location = new File();
        } else if (Files.isDirectory(pathWrapper1.path())) {
            location = new Folder();
        }

        return location;
    }

    private void convertPath(PathWrapper pathWrapper, StringBuilder stringBuilder, long size) {
        if (pathWrapper.isNested()) {
            addIndent(pathWrapper, stringBuilder);
        }

        stringBuilder.append(pathWrapper.path().getFileName())
                .append(SPACE)
                .append(size)
                .append(SPACE)
                .append(BYTES)
                .append("\n");
    }

    private List<Path> getFilesListSorted(Path path) {
        try {
            return Files.list(path)
                    .sorted(new FileNameComparator())
                    .sorted(new FileDirectoryComparator())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Something is wrong at sorting", e);
        }
    }

    private List<PathWrapper> wrapList(List<Path> paths, int nesting) {
        List<PathWrapper> pathWrappers = paths.stream()
                .map(path -> new PathWrapper(path, nesting))
                .collect(Collectors.toList());

        markLastElement(pathWrappers);

        return pathWrappers;
    }

    private void markLastElement(List<PathWrapper> pathWrappers) {
        pathWrappers.get(pathWrappers.size() - 1).makeLast();
    }
}
package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.efimchick.ifmo.io.filetree.FileTreeImpl.convertFileToString;
import static com.efimchick.ifmo.io.filetree.FileTreeImpl.makeTree;

public class Folder implements Location {
    @Override
    public void getTree(PathWrapper pathWrapper, StringBuilder stringBuilder) {
        convertFileToString(pathWrapper, stringBuilder);
        List<Path> innerPaths = getFilesListSorted(pathWrapper.path());
        List<PathWrapper> pathWrappers = wrapList(innerPaths, pathWrapper.getNestingLevel() + 1);
        pathWrappers.forEach(pathWrapper1 -> makeTree(pathWrapper1, stringBuilder));
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
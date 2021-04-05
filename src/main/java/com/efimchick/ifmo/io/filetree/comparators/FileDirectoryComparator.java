package com.efimchick.ifmo.io.filetree.comparators;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileDirectoryComparator implements Comparator<Path> {
    @Override
    public int compare(Path o1, Path o2) {
        return (Files.isDirectory(o2) ? 1 : 0) - (Files.isDirectory(o1) ? 1 : 0);
    }
}

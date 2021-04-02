package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.efimchick.ifmo.io.filetree.FileTreeImpl.convertFileToString;

public class File implements Location {
    @Override
    public void getTree(PathWrapper pathWrapper, StringBuilder stringBuilder) {
        convertFileToString(pathWrapper, stringBuilder);
    }

    @Override
    public long getSize(Path path) throws IOException {
        return Files.size(path);
    }
}

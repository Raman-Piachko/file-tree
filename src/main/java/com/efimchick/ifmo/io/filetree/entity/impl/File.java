package com.efimchick.ifmo.io.filetree.entity.impl;

import com.efimchick.ifmo.io.filetree.entity.Location;
import com.efimchick.ifmo.io.filetree.entity.PathWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.efimchick.ifmo.io.filetree.constants.FileTreeConstants.BYTES;
import static com.efimchick.ifmo.io.filetree.constants.FileTreeConstants.SPACE;
import static com.efimchick.ifmo.io.filetree.utils.IndentUtils.addIndent;

public class File implements Location {
    @Override
    public void convertPathToString(PathWrapper pathWrapper, StringBuilder stringBuilder) {
        if (pathWrapper.isNested()) {
            addIndent(pathWrapper, stringBuilder);
        }
        long size;
        try {
            size = getSize(pathWrapper.path());
        } catch (IOException e) {
            throw new RuntimeException("Something is wrong at getSize", e);
        }

        stringBuilder.append(pathWrapper.path().getFileName())
                .append(SPACE)
                .append(size)
                .append(SPACE)
                .append(BYTES)
                .append("\n");
    }

    @Override
    public long getSize(Path path) throws IOException {
        return Files.size(path);
    }
}
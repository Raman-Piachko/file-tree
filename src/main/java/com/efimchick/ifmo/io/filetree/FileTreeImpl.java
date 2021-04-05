package com.efimchick.ifmo.io.filetree;

import com.efimchick.ifmo.io.filetree.entity.impl.File;
import com.efimchick.ifmo.io.filetree.entity.impl.Folder;
import com.efimchick.ifmo.io.filetree.entity.PathWrapper;
import com.efimchick.ifmo.io.filetree.entity.Location;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileTreeImpl implements FileTree {
    private Location location;

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || Files.notExists(path)) {
            return Optional.empty();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            PathWrapper pathWrapper = new PathWrapper(path, 0);
            if (Files.isRegularFile(pathWrapper.path())) {
                location = new File();
            } else if (Files.isDirectory(pathWrapper.path())) {
                location = new Folder();
            }

            location.convertPathToString(pathWrapper, stringBuilder);
            return Optional.of(stringBuilder.toString());
        }
    }
}
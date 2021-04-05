package com.efimchick.ifmo.io.filetree.entity;

import java.io.IOException;
import java.nio.file.Path;

public interface Location {
    void convertPathToString(PathWrapper pathWrapper, StringBuilder stringBuilder);

    long getSize(Path path) throws IOException;
}
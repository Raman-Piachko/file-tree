package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.Path;

public interface Location {
    void getTree(PathWrapper pathWrapper, StringBuilder stringBuilder);
    long getSize(Path path) throws IOException;
}

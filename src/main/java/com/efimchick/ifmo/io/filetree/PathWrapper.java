package com.efimchick.ifmo.io.filetree;

import java.nio.file.Path;

public class PathWrapper {
    private final Path path;
    private final int nestingLevel;
    private boolean last;

    public PathWrapper(Path path, int nestingLevel) {
        this.path = path;
        this.nestingLevel = nestingLevel;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isNested() {
        return nestingLevel > 0;
    }

    public Path path() {
        return path;
    }

    public int getNestingLevel() {
        return nestingLevel;
    }

    public void makeLast() {
        last = true;
    }
}
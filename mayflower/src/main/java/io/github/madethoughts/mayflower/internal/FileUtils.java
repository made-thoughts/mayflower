/*
 * Copyright 2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.madethoughts.mayflower.internal;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 Common file utils
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     @param start the start
     @param depth the depth
     @param predicate the predicate
     @return The {@link Stream<Path>} produces by {@link Files#find}
     @see Files#find(Path, int, BiPredicate, FileVisitOption...)
     */
    public static Stream<Path> find(Path start, int depth, BiPredicate<Path, BasicFileAttributes> predicate) {
        try {
            return Files.find(start, depth, predicate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     @param path The file path
     @return the contents
     @see Files#readString(Path)
     */
    public static String readString(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     @param path The files path
     @param text the text to write
     @see Files#writeString(Path, CharSequence, OpenOption...)
     */
    public static void writeString(Path path, String text) {
        try {
            Files.writeString(path, text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

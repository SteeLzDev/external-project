package com.zetra.econsig.unittest.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;

public class FileHelperTest {

    @Test
    public void isFilenameSafeTest() throws ZetraException{
        assertTrue(FileHelper.isFilenameSafe("test")); // must be true
        assertTrue(FileHelper.isFilenameSafe("test.txt")); // must be true
        assertTrue(FileHelper.isFilenameSafe("my_file-name.doc")); // must be true

        assertFalse(FileHelper.isFilenameSafe(".hidden")); // false (leading dot)
        assertFalse(FileHelper.isFilenameSafe("file..name.txt")); // false (consecutive dots)
        assertFalse(FileHelper.isFilenameSafe("invalid/name")); // false (slash)
        assertFalse(FileHelper.isFilenameSafe("another name")); // false (space)
    }

    @Test
    public void isPathSafeTest() throws ZetraException{
        assertTrue(FileHelper.isPathSafe("/")); // must be true
        assertTrue(FileHelper.isPathSafe("/tmp")); // must be true
        assertTrue(FileHelper.isPathSafe("/tmp/tes")); // must be true
        assertTrue(FileHelper.isPathSafe("/tmp/test/file.ext")); // must be true
        assertTrue(FileHelper.isPathSafe("/tmp.dir/my.file")); // must be true

        assertFalse(FileHelper.isPathSafe("../tmp")); // false (consecutive dots)
        assertFalse(FileHelper.isPathSafe("/tmp/.")); // false (trailing dot)
        assertFalse(FileHelper.isPathSafe("./tmp")); // false (leading dot)
    }
}

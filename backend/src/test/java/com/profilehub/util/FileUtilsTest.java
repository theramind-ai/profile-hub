package com.profilehub.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    @Test
    void isValidFileSize_ReturnsTrueForSmallFile() {
        assertTrue(FileUtils.isValidFileSize(1024));
    }

    @Test
    void isValidFileSize_ReturnsFalseForLargeFile() {
        assertFalse(FileUtils.isValidFileSize(100 * 1024 * 1024));
    }

    @Test
    void getFileExtension_ReturnsExtension() {
        assertEquals("pdf", FileUtils.getFileExtension("doc.pdf"));
        assertEquals("", FileUtils.getFileExtension("noext"));
    }

    @Test
    void isAllowedFileType_ValidTypes() {
        assertTrue(FileUtils.isAllowedFileType("file.pdf"));
        assertFalse(FileUtils.isAllowedFileType("file.exe"));
    }
}

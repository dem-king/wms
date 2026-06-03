package com.wms.business.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Database initialization schema coverage tests.
 */
@DisplayName("Database initialization schema coverage")
class DatabaseInitSchemaCoverageTest {

    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("@TableName\\(\"([^\"]+)\"\\)");
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("CREATE TABLE(?: IF NOT EXISTS)? `([^`]+)`");

    @Test
    @DisplayName("initial SQL should create every entity table")
    void shouldCreateEveryEntityTable() throws IOException {
        Path script = Path.of("..", "..", "database", "wms_complete_init.sql");
        assertFalse(Files.notExists(script), "missing database initialization SQL script");

        Set<String> entityTables = collectEntityTables(Path.of(".."));
        Set<String> sqlTables = collectCreatedTables(script);
        Set<String> missingTables = new TreeSet<>(entityTables);
        missingTables.removeAll(sqlTables);

        assertEquals(Set.of(), missingTables, "database initialization SQL is missing entity tables");
    }

    private Set<String> collectEntityTables(Path root) throws IOException {
        Set<String> tables = new TreeSet<>();
        try (Stream<Path> files = Files.walk(root)) {
            for (Path file : files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().contains("src\\main\\java")
                            || path.toString().contains("src/main/java"))
                    .filter(path -> path.toString().endsWith(".java"))
                    .toList()) {
                String content = Files.readString(file);
                TABLE_NAME_PATTERN.matcher(content).results()
                        .map(match -> match.group(1))
                        .forEach(tables::add);
            }
        }
        return tables;
    }

    private Set<String> collectCreatedTables(Path script) throws IOException {
        Set<String> tables = new TreeSet<>();
        String content = Files.readString(script);
        CREATE_TABLE_PATTERN.matcher(content).results()
                .map(match -> match.group(1))
                .forEach(tables::add);
        return tables;
    }
}

package org.fogbowcloud.arrebol.execution.docker;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStream;

public class DockerExecutorHelperTest {

    String badFormattedSameLineContent;
    String emptyContent;
    String badFormattedContent;
    String wellFormattedContent;
    
    @Before
    public void setUp() throws IOException {
        final String TASK_SCRIPT_EXAMPLES_DIRECTORY = "task-script-examples/";
        final String EXIT_CODES_SAME_LINE_FILE_PATH =
                TASK_SCRIPT_EXAMPLES_DIRECTORY + "ec-in-same-line.ts.ec";
        final String EMPTY_EXAMPLE_FILE_PATH =
                TASK_SCRIPT_EXAMPLES_DIRECTORY + "empty-example.ts.ec";
        final String GAP_IN_EXIT_CODES_FILE_PATH =
                TASK_SCRIPT_EXAMPLES_DIRECTORY + "gap-in-ecs.ts.ec";
        final String WELL_FORMED_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "well-formed.ts.ec";

        this.badFormattedSameLineContent = getFileContent(new ClassPathResource(EXIT_CODES_SAME_LINE_FILE_PATH));
        this.emptyContent = getFileContent(new ClassPathResource(EMPTY_EXAMPLE_FILE_PATH));
        this.badFormattedContent = getFileContent(new ClassPathResource(GAP_IN_EXIT_CODES_FILE_PATH));
        this.wellFormattedContent = getFileContent(new ClassPathResource(WELL_FORMED_FILE_PATH));
    }
    
    private String getFileContent(Resource file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            String content = IOUtils.toString(is, "UTF-8");
            return content;
        }
    }
    
    @Test
    public void testNotEmptyContent() {
        Assert.assertFalse(this.wellFormattedContent.isEmpty());
        Assert.assertFalse(this.badFormattedContent.isEmpty());
        Assert.assertFalse(this.badFormattedSameLineContent.isEmpty());
    }
    
    @Test
    public void testEmptyContent() {
        Assert.assertTrue(this.emptyContent.isEmpty());
    }

}

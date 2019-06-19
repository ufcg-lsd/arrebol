package org.fogbowcloud.arrebol.execution.docker;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DockerExecutorHelperTest {

    @Before
    public void setUp() {
        final String TASK_SCRIPT_EXAMPLES_DIRECTORY = "task-script-examples/";
        final String EXIT_CODES_SAME_LINE_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "ec-in-same-line.ts.ec";
        final String EMPTY_EXAMPLE_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "empty-example.ts.ec";
        final String GAP_IN_EXIT_CODES_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "gap-in-ecs.ts.ec";
        final String WELL_FORMED_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "well-formed.ts.ec";

        final Resource exampleFileOne = new ClassPathResource(EXIT_CODES_SAME_LINE_FILE_PATH);
        final Resource exampleFileTwo = new ClassPathResource(EMPTY_EXAMPLE_FILE_PATH);
        final Resource exampleFileThree = new ClassPathResource(GAP_IN_EXIT_CODES_FILE_PATH);
        final Resource exampleFileFour = new ClassPathResource(WELL_FORMED_FILE_PATH);

        List<Resource> filesList = new ArrayList<>();

        filesList.add(exampleFileOne);
        filesList.add(exampleFileTwo);
        filesList.add(exampleFileThree);
        filesList.add(exampleFileFour);

        List<String> filesContent = new ArrayList<>();

        try {
            for (Resource file : filesList) {
                InputStream is = file.getInputStream();
                filesContent.add(IOUtils.toString(is, "UTF-8"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String fileContent : filesContent) {
            System.out.println(fileContent);
            break;
        }
    }

    @Test
    public void getEcFile() {



    }

//    @Test
//    public void parseEcContentToArray() {
//
//
//
//    }
}

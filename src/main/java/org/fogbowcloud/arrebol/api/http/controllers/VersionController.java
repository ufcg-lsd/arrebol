package org.fogbowcloud.arrebol.api.http.controllers;

import org.fogbowcloud.arrebol.api.constants.ApiDocumentation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Map.entry;

@RestController
@RequestMapping(value = ApiDocumentation.ApiEndpoints.VERSION_ENDPOINT)
public class VersionController {

    private static final Map<String,String> VERSION = Map.ofEntries(
            entry("Version", ApiDocumentation.ApiInfo.VERSION)
    );


    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> getVersion(){
        return ResponseEntity.ok(VERSION);
    }
}

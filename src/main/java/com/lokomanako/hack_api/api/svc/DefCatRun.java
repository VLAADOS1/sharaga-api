package com.lokomanako.hack_api.api.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefCatRun implements ApplicationRunner {

    @Autowired
    private DefCatSvc defCatSvc;

    @Override
    public void run(ApplicationArguments args) {
        defCatSvc.runAll();
    }
}

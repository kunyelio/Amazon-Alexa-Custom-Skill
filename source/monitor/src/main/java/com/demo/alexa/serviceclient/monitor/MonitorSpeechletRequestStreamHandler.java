package com.demo.alexa.serviceclient.monitor;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class MonitorSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds;

    static {
    	supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.ask.skill.953c1281-e884-4041-b705-7b4445659694");
    }

    public MonitorSpeechletRequestStreamHandler() {
        super(new MonitorSpeechlet(), supportedApplicationIds);
    }

}

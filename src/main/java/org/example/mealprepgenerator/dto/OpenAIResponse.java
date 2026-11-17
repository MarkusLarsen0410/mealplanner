package org.example.mealprepgenerator.dto;

import java.util.List;

public record OpenAIResponse(
        List<Output> output
) {
    public record Output(
            String type,
            List<Content> content
    ) {}

    public record Content(
            String type,
            String text
    ) {}


}
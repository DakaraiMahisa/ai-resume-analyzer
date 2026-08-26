package com.airesumeanalyzer.backend.processing.extraction;

import java.io.InputStream;

public interface TextExtractor {

    String extract(InputStream inputStream);
}
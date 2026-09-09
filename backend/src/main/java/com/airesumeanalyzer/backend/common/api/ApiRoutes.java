package com.airesumeanalyzer.backend.common.api;

public final class ApiRoutes {
    private ApiRoutes() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String API_V1 = "/v1";

    public static final String AUTH = API_V1 + "/auth";
    public static final String RESUMES = API_V1 + "/resumes";

    public static final String PROCESSING = API_V1 + "/processing";
    public static final String JOB_DESCRIPTIONS = API_V1 + "/job-descriptions";

    public static final String MATCHING = API_V1 + "/matching";
    public static final String ANALYSIS = API_V1 + "/analysis";

}

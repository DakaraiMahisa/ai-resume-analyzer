CREATE TABLE candidate_profiles (
                                    id BINARY(16) NOT NULL,
                                    created_at TIMESTAMP(6) NOT NULL,
                                    updated_at TIMESTAMP(6) NOT NULL,
                                    version BIGINT NOT NULL,

                                    user_id BINARY(16) NOT NULL,

                                    professional_headline VARCHAR(255),
                                    professional_summary TEXT,
                                    phone VARCHAR(30),
                                    location VARCHAR(255),
                                    linkedin_url VARCHAR(500),
                                    github_url VARCHAR(500),
                                    portfolio_url VARCHAR(500),
                                    career_objective TEXT,

                                    PRIMARY KEY (id),

                                    CONSTRAINT uk_candidate_profiles_user
                                        UNIQUE (user_id),

                                    CONSTRAINT fk_candidate_profiles_user
                                        FOREIGN KEY (user_id)
                                            REFERENCES users (id)
                                            ON DELETE CASCADE
);
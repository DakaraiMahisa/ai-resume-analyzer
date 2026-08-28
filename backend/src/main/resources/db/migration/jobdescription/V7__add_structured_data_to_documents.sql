ALTER TABLE resumes
    ADD COLUMN structured_data JSON;

ALTER TABLE job_descriptions
    ADD COLUMN structured_data JSON;
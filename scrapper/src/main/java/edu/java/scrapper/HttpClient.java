package edu.java.scrapper;

import edu.java.scrapper.dto.Response;

public interface HttpClient {
    Response getLastUpdate(Long id);
}

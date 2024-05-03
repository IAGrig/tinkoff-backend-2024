package edu.java.httpClients;

import edu.java.httpClients.dto.Response;

public interface HttpClient {
    Response getLastUpdate(Long id);
}

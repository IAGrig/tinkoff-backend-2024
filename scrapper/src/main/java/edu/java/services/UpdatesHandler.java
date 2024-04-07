package edu.java.services;

import edu.java.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface UpdatesHandler {
    String update(LinkUpdateRequest request);
}

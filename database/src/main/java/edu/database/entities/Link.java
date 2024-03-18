package edu.database.entities;

import java.time.OffsetDateTime;

public record Link(Long id, String domain, String url,
                   OffsetDateTime registered, OffsetDateTime lastUpdate, OffsetDateTime lastCheck) {
}

package edu.database.entities;

import java.time.OffsetDateTime;

public record User(Long tgId, OffsetDateTime registered) {
}

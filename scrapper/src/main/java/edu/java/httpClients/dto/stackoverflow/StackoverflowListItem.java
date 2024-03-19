package edu.java.httpClients.dto.stackoverflow;

import java.time.OffsetDateTime;
import lombok.Getter;

@SuppressWarnings("MemberName")
@Getter
public class StackoverflowListItem {
    private StackoverflowOwner owner;
    private Integer answer_id;
    private Integer comment_id;
    private OffsetDateTime last_activity_date;
    private OffsetDateTime creation_date;
}

package edu.database.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "tg_id")
    private Long tgId;
    private OffsetDateTime registered;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_links",
               joinColumns = @JoinColumn(name = "user_tg_id"),
               inverseJoinColumns = @JoinColumn(name = "link_id"))
    private List<Link> links;

    public User(Long tgId) {
        this.tgId = tgId;
        this.registered = OffsetDateTime.now();
    }

    public User(Long tgId, OffsetDateTime registered) {
        this.tgId = tgId;
        this.registered = registered;
    }

    public void addLink(Link link) {
        link.getUsers().add(this);
        links.add(link);
    }
}

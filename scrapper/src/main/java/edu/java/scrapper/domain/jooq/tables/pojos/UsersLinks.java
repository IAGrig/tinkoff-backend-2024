/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq.tables.pojos;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UsersLinks implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer linkId;
    private Long userTgId;

    public UsersLinks() {}

    public UsersLinks(UsersLinks value) {
        this.linkId = value.linkId;
        this.userTgId = value.userTgId;
    }

    @ConstructorProperties({ "linkId", "userTgId" })
    public UsersLinks(
        @NotNull Integer linkId,
        @NotNull Long userTgId
    ) {
        this.linkId = linkId;
        this.userTgId = userTgId;
    }

    /**
     * Getter for <code>USERS_LINKS.LINK_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Integer getLinkId() {
        return this.linkId;
    }

    /**
     * Setter for <code>USERS_LINKS.LINK_ID</code>.
     */
    public void setLinkId(@NotNull Integer linkId) {
        this.linkId = linkId;
    }

    /**
     * Getter for <code>USERS_LINKS.USER_TG_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getUserTgId() {
        return this.userTgId;
    }

    /**
     * Setter for <code>USERS_LINKS.USER_TG_ID</code>.
     */
    public void setUserTgId(@NotNull Long userTgId) {
        this.userTgId = userTgId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UsersLinks other = (UsersLinks) obj;
        if (this.linkId == null) {
            if (other.linkId != null)
                return false;
        }
        else if (!this.linkId.equals(other.linkId))
            return false;
        if (this.userTgId == null) {
            if (other.userTgId != null)
                return false;
        }
        else if (!this.userTgId.equals(other.userTgId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.linkId == null) ? 0 : this.linkId.hashCode());
        result = prime * result + ((this.userTgId == null) ? 0 : this.userTgId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UsersLinks (");

        sb.append(linkId);
        sb.append(", ").append(userTgId);

        sb.append(")");
        return sb.toString();
    }
}

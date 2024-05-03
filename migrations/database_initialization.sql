CREATE TABLE IF NOT EXISTS links (
    id          serial,
    url         text                                   NOT NULL,
    domain_name varchar(255)                           NOT NULL,
    registered timestamp with time zone  DEFAULT now() NOT NULL,
    last_update timestamp with time zone,
    last_check  timestamp with time zone DEFAULT now() NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (url)
);

CREATE TABLE IF NOT EXISTS users (
    tg_id      bigint,
    registered timestamp with time zone DEFAULT now(),

    PRIMARY KEY (tg_id)
);

CREATE TABLE IF NOT EXISTS users_links (
    link_id    integer references links ON DELETE CASCADE,
    user_tg_id bigint  references users ON DELETE CASCADE,

    PRIMARY KEY (link_id, user_tg_id)
);

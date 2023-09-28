create
database outbox;
\c outbox;
CREATE TABLE "public"."outbox"
(
    "id"            uuid NOT NULL,
    "aggregateid"   character varying(255),
    "aggregatetype" character varying(255),
    "payload"       jsonb,
    "type"          character varying(255),
    CONSTRAINT "outbox_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

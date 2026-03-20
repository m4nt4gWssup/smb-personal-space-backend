CREATE TABLE IF NOT EXISTS dis_personal_space_folders
(
	folder_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
	folder_name character varying(255) NOT NULL,
	parent_id bigint,
	folder_order integer NOT NULL,
	profile_id integer NOT NULL,
	CONSTRAINT dis_personal_space_folders_pk PRIMARY KEY (folder_id),
	CONSTRAINT dis_personal_space_folders_fk FOREIGN KEY (parent_id)
		REFERENCES dis_personal_space_folders (folder_id) MATCH SIMPLE
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	CONSTRAINT dis_personal_space_folders_profile_tab_fk FOREIGN KEY (profile_id)
		REFERENCES profile_tab (prof_id) MATCH SIMPLE
		ON UPDATE CASCADE
		ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS dis_personal_space
(
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
	profile_id integer NOT NULL,
	type character varying(255) NOT NULL,
	folder_id bigint NOT NULL,
	object_id character varying(255) NOT NULL,
	sort_order float4 NOT NULL,
	creation_timestamp timestamp without time zone,
	CONSTRAINT dis_personal_space_pk PRIMARY KEY (id),
	CONSTRAINT dis_personal_space_unique UNIQUE (profile_id, folder_id, type, object_id),
	CONSTRAINT dis_personal_space_dis_personal_space_folders_fk FOREIGN KEY (folder_id)
		REFERENCES dis_personal_space_folders (folder_id) MATCH SIMPLE
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	CONSTRAINT dis_personal_space_fk FOREIGN KEY (next_id)
		REFERENCES dis_personal_space (id) MATCH SIMPLE
		ON UPDATE NO ACTION
		ON DELETE NO ACTION,
	CONSTRAINT dis_personal_space_profile_tab_fk FOREIGN KEY (profile_id)
		REFERENCES profile_tab (prof_id) MATCH SIMPLE
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS dis_personal_space_objects_backup
(
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
	type character varying(255) NOT NULL,
	object_id character varying(255) NOT NULL,
	title character varying(1000) NOT NULL,
	additional_information character varying(1000),
	CONSTRAINT dis_personal_space_objects_backup_pk PRIMARY KEY (id)
);
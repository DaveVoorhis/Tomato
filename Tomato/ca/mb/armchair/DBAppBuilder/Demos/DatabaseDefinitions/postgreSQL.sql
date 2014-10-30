\connect template1

CREATE DATABASE dbappbuilder;

\connect dbappbuilder

CREATE SEQUENCE "test_testnumber_seq" start 1 increment 1 maxvalue 2147483647 minvalue 1 cache 1;

CREATE TABLE "test" (
	"testnumber" integer DEFAULT nextval('"test_testnumber_seq"'::text) NOT NULL,
	"dbcheckbox" boolean,
	"dbcombobox" character varying(20),
	"dblabel" character varying(20),
	"dbpasswordfield" character varying(10),
	"dbradiobutton" boolean,
	"dbscrollbar" integer,
	"dbtextfield" character varying(20),
	"dbtogglebutton" boolean,
	"dblist" character varying(20),
	"dbtextarea" text,
	"dbeditorpane" text,
	"dbtextpane" text,
	"dbslider" integer,
	Constraint "test_pkey" Primary Key ("testnumber")
);

COPY "test" FROM stdin;
147660	f	test	pg_class | t		f	0		f	pg_class | t				0
147636	t	pg_relcheck	pg_class | t		f	87		t	pg_class | t				71
147645	f	pg_relcheck	pg_group | t		t	47		f	pg_database | t				53
147662	f	pg_rewrite	pg_aggregate | t		f	49		f	pg_aggregate | t				33
147632	f	pg_relcheck	pg_class | t		f	35		f	pg_attribute | t				76
147664	f	pg_attrdef			f	49		f	pg_trigger | t			This is a test.  This is being changed.	57
147663	t	pg_relcheck	pg_xactlock | f		f	0		f	pg_xactlock | f	asdfasdf			0
147659	f	pg_rewrite	test | t		f	52		t	pg_description | t				45
\.

SELECT setval ('"test_testnumber_seq"', 147664, true);


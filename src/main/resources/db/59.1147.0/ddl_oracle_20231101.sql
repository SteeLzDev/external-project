/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     03/10/2023 15:18:09                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_dados_consignante                                  */
/*==============================================================*/
create table tb_dados_consignante  (
   cse_codigo           varchar2(32)                    not null,
   tda_codigo           varchar2(32)                    not null,
   dac_valor            varchar2(255)                   not null,
   constraint pk_tb_dados_consignante primary key (cse_codigo, tda_codigo)
);

/*==============================================================*/
/* Index: r_933_fk                                              */
/*==============================================================*/
create index r_933_fk on tb_dados_consignante (
   tda_codigo asc
);

/*==============================================================*/
/* Index: r_938_fk                                              */
/*==============================================================*/
create index r_938_fk on tb_dados_consignante (
   cse_codigo asc
);

/*==============================================================*/
/* Table: tb_dados_consignataria                                */
/*==============================================================*/
create table tb_dados_consignataria  (
   csa_codigo           varchar2(32)                    not null,
   tda_codigo           varchar2(32)                    not null,
   daa_valor            varchar2(255)                   not null,
   constraint pk_tb_dados_consignataria primary key (csa_codigo, tda_codigo)
);

/*==============================================================*/
/* Index: r_936_fk                                              */
/*==============================================================*/
create index r_936_fk on tb_dados_consignataria (
   tda_codigo asc
);

/*==============================================================*/
/* Index: r_941_fk                                              */
/*==============================================================*/
create index r_941_fk on tb_dados_consignataria (
   csa_codigo asc
);

/*==============================================================*/
/* Table: tb_dados_correspondente                               */
/*==============================================================*/
create table tb_dados_correspondente  (
   cor_codigo           varchar2(32)                    not null,
   tda_codigo           varchar2(32)                    not null,
   dar_valor            varchar2(255)                   not null,
   constraint pk_tb_dados_correspondente primary key (cor_codigo, tda_codigo)
);

/*==============================================================*/
/* Index: r_937_fk                                              */
/*==============================================================*/
create index r_937_fk on tb_dados_correspondente (
   tda_codigo asc
);

/*==============================================================*/
/* Index: r_942_fk                                              */
/*==============================================================*/
create index r_942_fk on tb_dados_correspondente (
   cor_codigo asc
);

/*==============================================================*/
/* Table: tb_dados_estabelecimento                              */
/*==============================================================*/
create table tb_dados_estabelecimento  (
   est_codigo           varchar2(32)                    not null,
   tda_codigo           varchar2(32)                    not null,
   dae_valor            varchar2(255)                   not null,
   constraint pk_tb_dados_estabelecimento primary key (est_codigo, tda_codigo)
);

/*==============================================================*/
/* Index: r_934_fk                                              */
/*==============================================================*/
create index r_934_fk on tb_dados_estabelecimento (
   tda_codigo asc
);

/*==============================================================*/
/* Index: r_939_fk                                              */
/*==============================================================*/
create index r_939_fk on tb_dados_estabelecimento (
   est_codigo asc
);

/*==============================================================*/
/* Table: tb_dados_orgao                                        */
/*==============================================================*/
create table tb_dados_orgao  (
   org_codigo           varchar2(32)                    not null,
   tda_codigo           varchar2(32)                    not null,
   dao_valor            varchar2(255)                   not null,
   constraint pk_tb_dados_orgao primary key (org_codigo, tda_codigo)
);

/*==============================================================*/
/* Index: r_935_fk                                              */
/*==============================================================*/
create index r_935_fk on tb_dados_orgao (
   tda_codigo asc
);

/*==============================================================*/
/* Index: r_940_fk                                              */
/*==============================================================*/
create index r_940_fk on tb_dados_orgao (
   org_codigo asc
);

alter table tb_dados_consignante
   add constraint fk_tb_dados_r_933_tb_tipo_ foreign key (tda_codigo)
      references tb_tipo_dado_adicional (tda_codigo);

alter table tb_dados_consignante
   add constraint fk_tb_dados_r_938_tb_consi foreign key (cse_codigo)
      references tb_consignante (cse_codigo);

alter table tb_dados_consignataria
   add constraint fk_tb_dados_r_936_tb_tipo_ foreign key (tda_codigo)
      references tb_tipo_dado_adicional (tda_codigo);

alter table tb_dados_consignataria
   add constraint fk_tb_dados_r_941_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_dados_correspondente
   add constraint fk_tb_dados_r_937_tb_tipo_ foreign key (tda_codigo)
      references tb_tipo_dado_adicional (tda_codigo);

alter table tb_dados_correspondente
   add constraint fk_tb_dados_r_942_tb_corre foreign key (cor_codigo)
      references tb_correspondente (cor_codigo);

alter table tb_dados_estabelecimento
   add constraint fk_tb_dados_r_934_tb_tipo_ foreign key (tda_codigo)
      references tb_tipo_dado_adicional (tda_codigo);

alter table tb_dados_estabelecimento
   add constraint fk_tb_dados_r_939_tb_estab foreign key (est_codigo)
      references tb_estabelecimento (est_codigo);

alter table tb_dados_orgao
   add constraint fk_tb_dados_r_935_tb_tipo_ foreign key (tda_codigo)
      references tb_tipo_dado_adicional (tda_codigo);

alter table tb_dados_orgao
   add constraint fk_tb_dados_r_940_tb_orgao foreign key (org_codigo)
      references tb_orgao (org_codigo);


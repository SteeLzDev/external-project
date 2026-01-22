/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     06/06/2024 09:27:00                          */
/*==============================================================*/


/*==============================================================*/
/* Table: ht_historico_ocorrencia_ade                           */
/*==============================================================*/
create table ht_historico_ocorrencia_ade  (
   hoa_codigo           varchar2(32)                    not null,
   oca_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   hoa_data             date                            not null,
   hoa_ip_acesso        varchar2(45)                    not null,
   hoa_obs              clob                            not null,
   constraint pk_ht_historico_ocorrencia_ade primary key (hoa_codigo)
);

/*==============================================================*/
/* Index: r_963_fk                                              */
/*==============================================================*/
create index r_963_fk on ht_historico_ocorrencia_ade (
   oca_codigo asc
);

/*==============================================================*/
/* Index: r_964_fk                                              */
/*==============================================================*/
create index r_964_fk on ht_historico_ocorrencia_ade (
   usu_codigo asc
);

/*==============================================================*/
/* Table: tb_historico_ocorrencia_ade                           */
/*==============================================================*/
create table tb_historico_ocorrencia_ade  (
   hoa_codigo           varchar2(32)                    not null,
   oca_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   hoa_data             date                            not null,
   hoa_ip_acesso        varchar2(45)                    not null,
   hoa_obs              clob                            not null,
   constraint pk_tb_historico_ocorrencia_ade primary key (hoa_codigo)
);

/*==============================================================*/
/* Index: r_961_fk                                              */
/*==============================================================*/
create index r_961_fk on tb_historico_ocorrencia_ade (
   oca_codigo asc
);

/*==============================================================*/
/* Index: r_962_fk                                              */
/*==============================================================*/
create index r_962_fk on tb_historico_ocorrencia_ade (
   usu_codigo asc
);

alter table ht_historico_ocorrencia_ade
   add constraint fk_ht_histo_r_963_ht_ocorr foreign key (oca_codigo)
      references ht_ocorrencia_autorizacao (oca_codigo);

alter table ht_historico_ocorrencia_ade
   add constraint fk_ht_histo_r_964_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_historico_ocorrencia_ade
   add constraint fk_tb_histo_r_961_tb_ocorr foreign key (oca_codigo)
      references tb_ocorrencia_autorizacao (oca_codigo);

alter table tb_historico_ocorrencia_ade
   add constraint fk_tb_histo_r_962_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);


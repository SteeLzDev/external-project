/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     17/06/2022 14:34:13                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_anexo_credenciamento                               */
/*==============================================================*/
create table tb_anexo_credenciamento  (
   anc_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   cre_codigo           varchar2(32)                    not null,
   tar_codigo           varchar2(32)                    not null,
   anc_nome             varchar2(255)                   not null,
   anc_ativo            smallint                        default 1 not null,
   anc_data             date                            not null,
   anc_ip_acesso        varchar2(45),
   constraint pk_tb_anexo_credenciamento primary key (anc_codigo)
);

/*==============================================================*/
/* Index: r_882_fk                                              */
/*==============================================================*/
create index r_882_fk on tb_anexo_credenciamento (
   cre_codigo asc
);

/*==============================================================*/
/* Index: r_883_fk                                              */
/*==============================================================*/
create index r_883_fk on tb_anexo_credenciamento (
   usu_codigo asc
);

/*==============================================================*/
/* Index: r_884_fk                                              */
/*==============================================================*/
create index r_884_fk on tb_anexo_credenciamento (
   tar_codigo asc
);

/*==============================================================*/
/* Table: tb_ocorrencia_credenciamento                          */
/*==============================================================*/
create table tb_ocorrencia_credenciamento  (
   ocd_codigo           varchar2(32)                    not null,
   cre_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   toc_codigo           varchar2(32)                    not null,
   tmo_codigo           varchar2(32),
   ocd_data             date                            not null,
   ocd_obs              clob                            not null,
   ocd_ip_acesso        varchar2(45),
   constraint pk_tb_ocorrencia_credenciament primary key (ocd_codigo)
);

/*==============================================================*/
/* Index: r_879_fk                                              */
/*==============================================================*/
create index r_879_fk on tb_ocorrencia_credenciamento (
   toc_codigo asc
);

/*==============================================================*/
/* Index: r_881_fk                                              */
/*==============================================================*/
create index r_881_fk on tb_ocorrencia_credenciamento (
   tmo_codigo asc
);

/*==============================================================*/
/* Index: r_878_fk                                              */
/*==============================================================*/
create index r_878_fk on tb_ocorrencia_credenciamento (
   usu_codigo asc
);

/*==============================================================*/
/* Index: r_880_fk                                              */
/*==============================================================*/
create index r_880_fk on tb_ocorrencia_credenciamento (
   cre_codigo asc
);

alter table tb_anexo_credenciamento
   add constraint fk_tb_anexo_r_882_tb_crede foreign key (cre_codigo)
      references tb_credenciamento_csa (cre_codigo);

alter table tb_anexo_credenciamento
   add constraint fk_tb_anexo_r_883_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_anexo_credenciamento
   add constraint fk_tb_anexo_r_884_tb_tipo_ foreign key (tar_codigo)
      references tb_tipo_arquivo (tar_codigo);

alter table tb_ocorrencia_credenciamento
   add constraint fk_tb_ocorr_r_878_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_ocorrencia_credenciamento
   add constraint fk_tb_ocorr_r_879_tb_tipo_ foreign key (toc_codigo)
      references tb_tipo_ocorrencia (toc_codigo);

alter table tb_ocorrencia_credenciamento
   add constraint fk_tb_ocorr_r_880_tb_crede foreign key (cre_codigo)
      references tb_credenciamento_csa (cre_codigo);

alter table tb_ocorrencia_credenciamento
   add constraint fk_tb_ocorr_r_881_tb_tipo_ foreign key (tmo_codigo)
      references tb_tipo_motivo_operacao (tmo_codigo);


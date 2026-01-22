/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     07/03/2022 12:04:33                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_ocorrencia_correspondente                          */
/*==============================================================*/
create table tb_ocorrencia_correspondente  (
   ocr_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   cor_codigo           varchar2(32)                    not null,
   toc_codigo           varchar2(32)                    not null,
   tmo_codigo           varchar2(32),
   ocr_obs              clob                            not null,
   ocr_data             date                            not null,
   ocr_ip_acesso        varchar2(45),
   constraint pk_tb_ocorrencia_correspondent primary key (ocr_codigo)
);

/*==============================================================*/
/* Index: r_860_fk                                              */
/*==============================================================*/
create index r_860_fk on tb_ocorrencia_correspondente (
   cor_codigo asc
);

/*==============================================================*/
/* Index: r_861_fk                                              */
/*==============================================================*/
create index r_861_fk on tb_ocorrencia_correspondente (
   usu_codigo asc
);

/*==============================================================*/
/* Index: r_862_fk                                              */
/*==============================================================*/
create index r_862_fk on tb_ocorrencia_correspondente (
   toc_codigo asc
);

/*==============================================================*/
/* Index: r_863_fk                                              */
/*==============================================================*/
create index r_863_fk on tb_ocorrencia_correspondente (
   tmo_codigo asc
);

alter table tb_ocorrencia_correspondente
   add constraint fk_tb_ocorr_r_860_tb_corre foreign key (cor_codigo)
      references tb_correspondente (cor_codigo);

alter table tb_ocorrencia_correspondente
   add constraint fk_tb_ocorr_r_861_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_ocorrencia_correspondente
   add constraint fk_tb_ocorr_r_862_tb_tipo_ foreign key (toc_codigo)
      references tb_tipo_ocorrencia (toc_codigo);

alter table tb_ocorrencia_correspondente
   add constraint fk_tb_ocorr_r_863_tb_tipo_ foreign key (tmo_codigo)
      references tb_tipo_motivo_operacao (tmo_codigo);


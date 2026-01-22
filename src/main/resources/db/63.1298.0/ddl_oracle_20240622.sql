/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     17/06/2024 10:05:16                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_bloco_processamento_lote                           */
/*==============================================================*/
create table tb_bloco_processamento_lote  (
   cpl_arquivo_econsig  varchar2(255)                   not null,
   bpl_num_linha        integer                         not null,
   csa_codigo           varchar2(32)                    not null,
   sbp_codigo           varchar2(32)                    not null,
   bpl_periodo          date                            not null,
   bpl_data_inclusao    date                            not null,
   bpl_data_processamento date,
   bpl_linha            clob                            not null,
   bpl_campos           clob                            not null,
   bpl_critica          clob,
   constraint pk_tb_bloco_processamento_lote primary key (cpl_arquivo_econsig, bpl_num_linha)
);

/*==============================================================*/
/* Index: r_966_fk                                              */
/*==============================================================*/
create index r_966_fk on tb_bloco_processamento_lote (
   cpl_arquivo_econsig asc
);

/*==============================================================*/
/* Index: r_968_fk                                              */
/*==============================================================*/
create index r_968_fk on tb_bloco_processamento_lote (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_967_fk                                              */
/*==============================================================*/
create index r_967_fk on tb_bloco_processamento_lote (
   sbp_codigo asc
);

alter table tb_controle_processamento_lote add cpl_canal char(1) default '2' not null;

alter table tb_controle_processamento_lote add cpl_data date;

alter table tb_controle_processamento_lote add cpl_arquivo_critica varchar2(255);

alter table tb_controle_processamento_lote add usu_codigo varchar2(32);

/*==============================================================*/
/* Index: r_965_fk                                              */
/*==============================================================*/
create index r_965_fk on tb_controle_processamento_lote (
   usu_codigo asc
);

alter table tb_bloco_processamento_lote
   add constraint fk_tb_bloco_r_966_tb_contr foreign key (cpl_arquivo_econsig)
      references tb_controle_processamento_lote (cpl_arquivo_econsig);

alter table tb_bloco_processamento_lote
   add constraint fk_tb_bloco_r_967_tb_statu foreign key (sbp_codigo)
      references tb_status_bloco_processamento (sbp_codigo);

alter table tb_bloco_processamento_lote
   add constraint fk_tb_bloco_r_968_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_controle_processamento_lote
   add constraint fk_tb_contr_r_965_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);


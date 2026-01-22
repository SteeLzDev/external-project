/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     31/01/2025 14:11:15                          */
/*==============================================================*/


alter table tb_arquivo_ope_nao_confirmadas
   drop constraint fk_tb_arqui_r_793_tb_opera;

alter table tb_operacao_nao_confirmada
   drop constraint fk_tb_opera_r_791_tb_usuar;

alter table tb_operacao_nao_confirmada
   drop constraint fk_tb_opera_r_792_tb_acess;

drop index r_792_fk;

drop index r_791_fk;

alter table tb_operacao_nao_confirmada
   drop primary key cascade;

CALL dropTableIfExists('tmp_tb_operacao_nao_confirmada');

rename tb_operacao_nao_confirmada to tmp_tb_operacao_nao_confirmada;

/*==============================================================*/
/* Table: tb_operacao_nao_confirmada                          */
/*==============================================================*/
create table tb_operacao_nao_confirmada  (
   onc_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   rse_codigo           varchar2(32),
   acr_codigo           varchar2(32)                    not null,
   onc_ip_acesso        varchar2(45)                    not null,
   onc_data             date                            not null,
   onc_detalhe          blob                            not null,
   onc_parametros       clob                            not null,
   constraint pk_tb_operacao_nao_confirmada primary key (onc_codigo)
);

insert into tb_operacao_nao_confirmada (onc_codigo, usu_codigo, acr_codigo, onc_ip_acesso, onc_data, onc_detalhe, onc_parametros)
select onc_codigo, usu_codigo, acr_codigo, onc_ip_acesso, onc_data, onc_detalhe, onc_parametros
from tmp_tb_operacao_nao_confirmada;

CALL dropTableIfExists('tmp_tb_operacao_nao_confirmada');

/*==============================================================*/
/* Index: r_791_fk                                              */
/*==============================================================*/
create index r_791_fk on tb_operacao_nao_confirmada (
   usu_codigo asc
);

/*==============================================================*/
/* Index: r_792_fk                                              */
/*==============================================================*/
create index r_792_fk on tb_operacao_nao_confirmada (
   acr_codigo asc
);

/*==============================================================*/
/* Index: r_977_fk                                              */
/*==============================================================*/
create index r_977_fk on tb_operacao_nao_confirmada (
   rse_codigo asc
);

alter table tb_arquivo_ope_nao_confirmadas
   add constraint fk_tb_arqui_r_793_tb_opera foreign key (onc_codigo)
      references tb_operacao_nao_confirmada (onc_codigo);

alter table tb_operacao_nao_confirmada
   add constraint fk_tb_opera_r_791_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_operacao_nao_confirmada
   add constraint fk_tb_opera_r_792_tb_acess foreign key (acr_codigo)
      references tb_acesso_recurso (acr_codigo);

alter table tb_operacao_nao_confirmada
   add constraint fk_tb_opera_r_977_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);


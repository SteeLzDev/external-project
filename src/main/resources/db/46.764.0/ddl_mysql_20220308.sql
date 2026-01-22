/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     08/03/2022 09:37:50                          */
/*==============================================================*/


alter table tb_operacao_libera_margem
   add OLM_CONFIRMADA char(1) not null default 'N'
;

alter table tb_operacao_libera_margem
   add ADE_CODIGO varchar(32)
;

alter table tb_operacao_libera_margem add constraint FK_R_864 foreign key (ADE_CODIGO)
      references tb_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;


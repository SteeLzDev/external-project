/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     16/02/2023 17:43:53                          */
/*==============================================================*/


alter table tb_operacao_libera_margem
   add ADE_CODIGO_HT varchar(32)
;

alter table tb_operacao_libera_margem add constraint FK_R_901 foreign key (ADE_CODIGO_HT)
      references ht_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     22/09/2022 14:32:17                          */
/*==============================================================*/


alter table tb_definicao_taxa_juros
   add FUN_CODIGO varchar(32);

alter table tb_definicao_taxa_juros add constraint FK_R_891 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;


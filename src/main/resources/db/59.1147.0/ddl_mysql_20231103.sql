/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/10/2023 09:33:01                          */
/*==============================================================*/


alter table tb_margem_registro_servidor
   add MAR_CODIGO_ADEQUACAO smallint
;

alter table tb_margem_registro_servidor add constraint FK_R_943 foreign key (MAR_CODIGO_ADEQUACAO)
      references tb_margem (MAR_CODIGO) on delete restrict on update restrict;


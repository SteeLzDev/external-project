/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     17/06/2022 14:49:03                          */
/*==============================================================*/


alter table tb_beneficiario
   add BFC_CLASSIFICACAO char(1)
;

alter table tb_beneficiario
   add RSE_CODIGO varchar(32)
;

alter table tb_beneficiario add constraint FK_R_885 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     27/06/2022 11:09:44                          */
/*==============================================================*/


alter table ta_beneficiario
   add BFC_CLASSIFICACAO char(1)
;

alter table ta_beneficiario
   add RSE_CODIGO varchar(32)
;

alter table ta_beneficiario add constraint FK_R_886 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;


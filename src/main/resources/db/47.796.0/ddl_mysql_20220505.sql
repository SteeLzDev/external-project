/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/04/2022 08:58:16                          */
/*==============================================================*/


alter table tb_consignante
  add BCO_CODIGO smallint
;

alter table tb_consignante
   add CSE_SISTEMA_FOLHA varchar(100)
;

alter table tb_consignante add constraint FK_R_866 foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;


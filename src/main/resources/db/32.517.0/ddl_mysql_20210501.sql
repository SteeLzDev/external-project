/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     31/03/2021 15:42:02                          */
/*==============================================================*/


alter table tb_calculo_beneficio
   add MDE_CODIGO varchar(32)
;

alter table tb_calculo_beneficio add constraint FK_R_829 foreign key (MDE_CODIGO)
      references tb_motivo_dependencia (MDE_CODIGO) on delete restrict on update restrict;


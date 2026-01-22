/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     25/10/2019 11:42:27                          */
/*==============================================================*/


alter table ta_beneficio
   add MBE_CODIGO varchar(32);

alter table ta_beneficio add constraint FK_R_782 foreign key (MBE_CODIGO)
      references tb_modalidade_beneficio (MBE_CODIGO) on delete restrict on update restrict;


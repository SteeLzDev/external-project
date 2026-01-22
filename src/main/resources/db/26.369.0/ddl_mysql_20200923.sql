/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     23/09/2020 12:59:48                          */
/*==============================================================*/


alter table tb_calculo_beneficio
   add GRP_CODIGO varchar(32)
;

alter table tb_calculo_beneficio add constraint FK_R_815 foreign key (GRP_CODIGO)
      references tb_grau_parentesco (GRP_CODIGO) on delete restrict on update restrict;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     24/03/2020 15:21:38                          */
/*==============================================================*/


alter table ta_beneficio
   add COR_CODIGO varchar(32)
;

alter table tb_beneficio
   add COR_CODIGO varchar(32)
;

alter table tb_provedor_beneficio
   add COR_CODIGO varchar(32)
;

alter table ta_beneficio add constraint FK_R_796 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;

alter table tb_beneficio add constraint FK_R_794 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;

alter table tb_provedor_beneficio add constraint FK_R_795 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     02/08/2019 09:13:40                          */
/*==============================================================*/


alter table tb_natureza_servico
   add NSE_CODIGO_PAI varchar(32)
;

alter table tb_natureza_servico add constraint FK_R_766 foreign key (NSE_CODIGO_PAI)
      references tb_natureza_servico (NSE_CODIGO) on delete restrict on update restrict;

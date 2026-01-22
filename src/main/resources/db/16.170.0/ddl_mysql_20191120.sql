/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/11/2019 17:11:49                          */
/*==============================================================*/


alter table tb_ocorrencia_servidor
   add TMO_CODIGO varchar(32);

alter table tb_ocorrencia_servidor add constraint FK_R_786 foreign key (TMO_CODIGO)
      references tb_tipo_motivo_operacao (TMO_CODIGO) on delete restrict on update restrict;


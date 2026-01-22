/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     02/07/2020 08:57:24                          */
/*==============================================================*/


alter table tb_ocorrencia_consignataria
   add TMO_CODIGO varchar(32)
;

alter table tb_ocorrencia_consignataria add constraint FK_R_807 foreign key (TMO_CODIGO)
      references tb_tipo_motivo_operacao (TMO_CODIGO) on delete restrict on update restrict;


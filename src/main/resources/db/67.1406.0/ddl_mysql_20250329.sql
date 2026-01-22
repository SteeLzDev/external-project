/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     05/03/2025 15:17:42                          */
/*==============================================================*/


alter table tb_assunto_comunicacao
   add ASC_CONSIGNACAO bool not null default 0;

alter table tb_comunicacao 
   add ADE_CODIGO varchar(32);

alter table tb_comunicacao add constraint FK_R_978 foreign key (ADE_CODIGO)
      references tb_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;

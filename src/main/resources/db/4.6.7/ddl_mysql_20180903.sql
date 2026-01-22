/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03/09/2018 10:53:28                          */
/*==============================================================*/


alter table ta_beneficio
   add BEN_CATEGORIA smallint;

alter table tb_beneficio
   add BEN_CATEGORIA smallint;

/*==============================================================*/
/* Table: tb_beneficio_servico                                  */
/*==============================================================*/
create table tb_beneficio_servico
(
   BEN_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) not null,
   TIB_CODIGO           varchar(32) not null,
   BSE_ORDEM            smallint not null default 0,
   primary key (BEN_CODIGO, SVC_CODIGO)
) ENGINE=InnoDB;

alter table tb_beneficio_servico add constraint FK_R_726 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

alter table tb_beneficio_servico add constraint FK_R_727 foreign key (BEN_CODIGO)
      references tb_beneficio (BEN_CODIGO) on delete restrict on update restrict;

alter table tb_beneficio_servico add constraint FK_R_728 foreign key (TIB_CODIGO)
      references tb_tipo_beneficiario (TIB_CODIGO) on delete restrict on update restrict;


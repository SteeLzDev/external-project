/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     11/07/2018 10:33:16                          */
/*==============================================================*/

/*==============================================================*/
/* Table: tb_status_beneficiario                                */
/*==============================================================*/
create table tb_status_beneficiario
(
   SBE_CODIGO           varchar(32) not null,
   SBE_DESCRICAO        varchar(100) not null,
   primary key (SBE_CODIGO)
) ENGINE=InnoDB;

insert into tb_status_beneficiario (SBE_CODIGO, SBE_DESCRICAO) values ('1', 'Ativo');

alter table ta_beneficiario
   add column SBE_CODIGO  varchar(32) not null default '1';

alter table tb_beneficiario
   add column SBE_CODIGO  varchar(32) not null default '1';


alter table ta_beneficiario add constraint FK_R_715 foreign key (SBE_CODIGO)
      references tb_status_beneficiario (SBE_CODIGO) on delete restrict on update restrict;

alter table tb_beneficiario add constraint FK_R_714 foreign key (SBE_CODIGO)
      references tb_status_beneficiario (SBE_CODIGO) on delete restrict on update restrict;

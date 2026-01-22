/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/09/2019 11:07:27                          */
/*==============================================================*/


alter table tb_acesso_usuario drop foreign key FK_R_735;

alter table tb_acesso_usuario drop foreign key FK_R_736;

drop table if exists tmp_tb_acesso_usuario;

rename table tb_acesso_usuario to tmp_tb_acesso_usuario;

/*==============================================================*/
/* Table: tb_acesso_usuario                                     */
/*==============================================================*/
create table tb_acesso_usuario
(
   USU_CODIGO           varchar(32) not null,
   ACR_CODIGO           varchar(32) not null,
   ACU_NRO_ACESSO       int not null,
   primary key (USU_CODIGO, ACR_CODIGO)
) ENGINE = InnoDB;

insert into tb_acesso_usuario (USU_CODIGO, ACR_CODIGO, ACU_NRO_ACESSO)
select USU_CODIGO, ACR_CODIGO, ACU_NRO_ACESSO
from tmp_tb_acesso_usuario;

drop table if exists tmp_tb_acesso_usuario;

alter table tb_acesso_usuario add constraint FK_R_735 foreign key (ACR_CODIGO)
      references tb_acesso_recurso (ACR_CODIGO) on delete restrict on update restrict;

alter table tb_acesso_usuario add constraint FK_R_736 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;


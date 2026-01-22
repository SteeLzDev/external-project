/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/01/2020 15:38:18                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_funcao_perfil_master_nca                           */
/*==============================================================*/
create table tb_funcao_perfil_master_nca
(
   FUN_CODIGO           varchar(32) not null,
   NCA_CODIGO           varchar(32) not null,
   primary key (FUN_CODIGO, NCA_CODIGO)
) ENGINE=InnoDB;

alter table tb_funcao_perfil_master_nca add constraint FK_R_789 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

alter table tb_funcao_perfil_master_nca add constraint FK_R_790 foreign key (NCA_CODIGO)
      references tb_natureza_consignataria (NCA_CODIGO) on delete restrict on update restrict;


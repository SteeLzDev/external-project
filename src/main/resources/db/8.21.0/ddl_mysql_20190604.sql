/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     04/06/2019 13:53:36                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_historico_consulta_margem                          */
/*==============================================================*/
create table tb_historico_consulta_margem
(
   HCM_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   RSE_CODIGO           varchar(32) not null,
   HCM_DATA             datetime not null,
   HCM_TEM_MARGEM       char(1) not null default '1',
   HCM_CANAL            char(1) not null default '1',
   primary key (HCM_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_historico_consulta_margem add constraint FK_R_753 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_historico_consulta_margem add constraint FK_R_754 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;


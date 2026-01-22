/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/07/2021 14:40:53                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_funcao_sensivel_csa                                */
/*==============================================================*/
create table tb_funcao_sensivel_csa
(
   FUN_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   FSC_VALOR            char(1) not null default 'N',
   primary key (FUN_CODIGO, CSA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_funcao_sensivel_csa add constraint FK_R_844 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

alter table tb_funcao_sensivel_csa add constraint FK_R_845 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;


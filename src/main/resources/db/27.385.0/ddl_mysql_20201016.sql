/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     16/10/2020 08:46:03                          */
/*==============================================================*/

/*==============================================================*/
/* Table: tb_tipo_motivo_bloqueio                               */
/*==============================================================*/
create table tb_tipo_motivo_bloqueio
(
   TMB_CODIGO           varchar(32) not null,
   TMB_DESCRICAO        varchar(100) not null,
   primary key (TMB_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_consignataria
   add TMB_CODIGO varchar(32);

alter table tb_consignataria add constraint FK_R_818 foreign key (TMB_CODIGO)
      references tb_tipo_motivo_bloqueio (TMB_CODIGO) on delete restrict on update restrict;

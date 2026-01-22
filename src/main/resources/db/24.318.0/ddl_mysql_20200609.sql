/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     08/06/2020 16:33:47                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_status_verba_rescisoria                            */
/*==============================================================*/
create table tb_status_verba_rescisoria
(
   SVR_CODIGO           varchar(32) not null,
   SVR_DESCRICAO        varchar(100) not null,
   primary key (SVR_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_verba_rescisoria_rse                               */
/*==============================================================*/
create table tb_verba_rescisoria_rse
(
   VRR_CODIGO           varchar(32) not null,
   RSE_CODIGO           varchar(32) not null,
   SVR_CODIGO           varchar(32) not null,
   VRR_DATA_INI         datetime not null,
   VRR_DATA_FIM         datetime,
   VRR_DATA_ULT_ATUALIZACAO datetime not null,
   VRR_VALOR            decimal(13,2),
   VRR_PROCESSADO       char(1) not null default 'N',
   primary key (VRR_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_verba_rescisoria_rse add constraint FK_R_805 foreign key (SVR_CODIGO)
      references tb_status_verba_rescisoria (SVR_CODIGO) on delete restrict on update restrict;

alter table tb_verba_rescisoria_rse add constraint FK_R_806 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;


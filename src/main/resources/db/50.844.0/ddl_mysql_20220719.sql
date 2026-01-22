/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/06/2022 10:26:44                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_credenciamento_csa                                 */
/*==============================================================*/
create table tb_credenciamento_csa
(
   CRE_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   SCR_CODIGO           varchar(32) not null,
   CRE_DATA_INI         datetime not null,
   CRE_DATA_FIM         datetime,
   primary key (CRE_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_status_credenciamento                              */
/*==============================================================*/
create table tb_status_credenciamento
(
   SCR_CODIGO           varchar(32) not null,
   SCR_DESCRICAO        varchar(40) not null,
   primary key (SCR_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_credenciamento_csa add constraint FK_R_871 foreign key (SCR_CODIGO)
      references tb_status_credenciamento (SCR_CODIGO) on delete restrict on update restrict;

alter table tb_credenciamento_csa add constraint FK_R_872 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;


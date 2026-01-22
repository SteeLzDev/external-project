/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     11/05/2020 10:47:50                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_orgao                                        */
/*==============================================================*/
create table tb_param_orgao
(
   ORG_CODIGO           varchar(32) not null,
   TAO_CODIGO           varchar(32) not null,
   PAO_VLR              varchar(255) not null,
   primary key (ORG_CODIGO, TAO_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_tipo_param_orgao                                   */
/*==============================================================*/
create table tb_tipo_param_orgao
(
   TAO_CODIGO           varchar(32) not null,
   TAO_DESCRICAO        varchar(100) not null,
   TAO_DOMINIO          varchar(100) not null,
   TAO_VLR_DEFAULT      varchar(255),
   TAO_SUP_ALTERA       char(1) not null default 'N',
   TAO_SUP_CONSULTA     char(1) not null default 'N',
   TAO_CSE_ALTERA       char(1) not null default 'N',
   TAO_CSE_CONSULTA     char(1) not null default 'N',
   TAO_ORG_ALTERA       char(1) not null default 'N',
   TAO_ORG_CONSULTA     char(1) not null default 'N',
   primary key (TAO_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_param_orgao add constraint FK_R_803 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

alter table tb_param_orgao add constraint FK_R_804 foreign key (TAO_CODIGO)
      references tb_tipo_param_orgao (TAO_CODIGO) on delete restrict on update restrict;


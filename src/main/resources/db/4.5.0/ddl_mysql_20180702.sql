/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     02/07/2018 17:25:25                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_calendario_beneficio_cse                           */
/*==============================================================*/
create table tb_calendario_beneficio_cse
(
   CSE_CODIGO           varchar(32) not null,
   CBC_PERIODO          date not null,
   CBC_DIA_CORTE        smallint not null default 0,
   CBC_DATA_INI         datetime not null,
   CBC_DATA_FIM         datetime not null,
   primary key (CSE_CODIGO, CBC_PERIODO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_periodo_beneficio                                  */
/*==============================================================*/
create table tb_periodo_beneficio
(
   ORG_CODIGO           varchar(32) not null,
   PBE_PERIODO          date not null,
   PBE_PERIODO_ANT      date not null,
   PBE_PERIODO_POS      date not null,
   PBE_DIA_CORTE        smallint not null default 0,
   PBE_DATA_INI         datetime not null,
   PBE_DATA_FIM         datetime not null,
   PBE_SEQUENCIA        smallint not null default 0,
   primary key (ORG_CODIGO, PBE_PERIODO)
) ENGINE=InnoDB;

alter table tb_calendario_beneficio_cse add constraint FK_R_710 foreign key (CSE_CODIGO)
      references tb_consignante (CSE_CODIGO) on delete restrict on update restrict;

alter table tb_periodo_beneficio add constraint FK_R_709 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;


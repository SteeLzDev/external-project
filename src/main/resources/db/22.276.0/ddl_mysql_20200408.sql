/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/04/2020 13:55:58                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_faturamento_beneficio_nf                           */
/*==============================================================*/
create table tb_faturamento_beneficio_nf
(
   FNF_CODIGO           varchar(32) not null,
   FAT_CODIGO           varchar(32) not null,
   TNF_CODIGO           varchar(32) not null,
   FNF_CODIGO_CONTRATO  varchar(40),
   FNF_NUMERO_NF        varchar(30),
   FNF_NUMERO_TITULO    varchar(30),
   FNF_VALOR_BRUTO      decimal(13,2) not null,
   FNF_VALOR_ISS        decimal(13,2),
   FNF_VALOR_IR         decimal(13,2),
   FNF_VALOR_PIS_COFINS decimal(13,2),
   FNF_VALOR_LIQUIDO    decimal(13,2),
   FNF_DATA_GERACAO     datetime not null,
   FNF_DATA_VENCIMENTO  date,
   primary key (FNF_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_tipo_nota_fiscal                                   */
/*==============================================================*/
create table tb_tipo_nota_fiscal
(
   TNF_CODIGO           varchar(32) not null,
   TNF_DESCRICAO        varchar(40) not null,
   primary key (TNF_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_faturamento_beneficio_nf add constraint FK_R_801 foreign key (FAT_CODIGO)
      references tb_faturamento_beneficio (FAT_CODIGO) on delete restrict on update restrict;

alter table tb_faturamento_beneficio_nf add constraint FK_R_802 foreign key (TNF_CODIGO)
      references tb_tipo_nota_fiscal (TNF_CODIGO) on delete restrict on update restrict;


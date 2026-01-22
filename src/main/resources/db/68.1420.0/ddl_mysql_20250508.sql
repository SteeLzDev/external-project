/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     31/03/2025 17:12:40                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_pontuacao_rse_csa                            */
/*==============================================================*/
create table tb_param_pontuacao_rse_csa
(
   PPR_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   TPO_CODIGO           varchar(32) not null,
   NSE_CODIGO           varchar(32),
   PPR_PONTUACAO        int not null,
   PPR_LIM_INFERIOR     int not null,
   PPR_LIM_SUPERIOR     int not null,
   primary key (PPR_CODIGO)
) ENGINE = InnoDB;

/*==============================================================*/
/* Table: tb_pontuacao_rse_csa                                  */
/*==============================================================*/
create table tb_pontuacao_rse_csa
(
   RSE_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   PON_VLR              int not null,
   PON_DATA             datetime not null,
   primary key (RSE_CODIGO, CSA_CODIGO)
) ENGINE = InnoDB;

alter table tb_param_pontuacao_rse_csa add constraint FK_R_979 foreign key (TPO_CODIGO)
      references tb_tipo_param_pontuacao (TPO_CODIGO) on delete restrict on update restrict;

alter table tb_param_pontuacao_rse_csa add constraint FK_R_980 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_param_pontuacao_rse_csa add constraint FK_R_981 foreign key (NSE_CODIGO)
      references tb_natureza_servico (NSE_CODIGO) on delete restrict on update restrict;

alter table tb_pontuacao_rse_csa add constraint FK_R_982 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_pontuacao_rse_csa add constraint FK_R_983 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;


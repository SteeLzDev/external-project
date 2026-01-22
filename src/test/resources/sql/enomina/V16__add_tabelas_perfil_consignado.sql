/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/06/2024 15:15:15                          */
/*==============================================================*/

-- EXECUTADO NO DESENV-20553
-- ALTER TABLE tb_historico_margem_rse MODIFY COLUMN HMR_CODIGO BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;
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

/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     01/04/2025 10:23:40                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_perfil_consignado_csa                              */
/*==============================================================*/
create table tb_perfil_consignado_csa
(
   PCC_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   PCC_PONTUACAO_INFERIOR int not null,
   PCC_PONTUACAO_SUPERIOR int not null,
   PCC_PERFIL           varchar(100) not null,
   primary key (PCC_CODIGO)
) ENGINE = InnoDB;

alter table tb_perfil_consignado_csa add constraint FK_R_984 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;


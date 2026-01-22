/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     17/06/2022 13:52:14                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_limite_margem_csa_org                              */
/*==============================================================*/
create table tb_limite_margem_csa_org
(
   MAR_CODIGO           smallint not null,
   CSA_CODIGO           varchar(32) not null,
   ORG_CODIGO           varchar(32) not null,
   LMC_VALOR            decimal(13,8) not null default 1,
   LMC_DATA             datetime not null,
   primary key (MAR_CODIGO, CSA_CODIGO, ORG_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_limite_margem_csa_org add constraint FK_R_875 foreign key (MAR_CODIGO)
      references tb_margem (MAR_CODIGO) on delete restrict on update restrict;

alter table tb_limite_margem_csa_org add constraint FK_R_876 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_limite_margem_csa_org add constraint FK_R_877 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;


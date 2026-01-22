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


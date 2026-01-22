/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     29/01/2019 16:26:31                          */
/*==============================================================*/

/*==============================================================*/
/* Table: tb_tipo_dado_adicional                                */
/*==============================================================*/
rename table tb_tipo_dados_autorizacao to tb_tipo_dado_adicional;

alter table tb_tipo_dado_adicional
   add TEN_CODIGO varchar(32) after TDA_CODIGO;

update tb_tipo_dado_adicional
   set TEN_CODIGO = '19';

alter table tb_tipo_dado_adicional
   modify TEN_CODIGO varchar(32) not null;

alter table tb_tipo_dado_adicional add constraint FK_R_737 foreign key (TEN_CODIGO)
      references tb_tipo_entidade (TEN_CODIGO) on delete restrict on update restrict;

/*==============================================================*/
/* Table: tb_dados_servidor                                     */
/*==============================================================*/
create table tb_dados_servidor
(
   SER_CODIGO           varchar(32) not null,
   TDA_CODIGO           varchar(32) not null,
   DAS_VALOR            varchar(255) not null,
   primary key (TDA_CODIGO, SER_CODIGO)
) ENGINE=InnoDB;

alter table tb_dados_servidor add constraint FK_R_738 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_dados_servidor add constraint FK_R_739 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

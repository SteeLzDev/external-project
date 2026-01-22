/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     04/07/2023 14:20:25                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_sub_relatorio                                      */
/*==============================================================*/
create table tb_sub_relatorio
(
   SRE_CODIGO           varchar(32) not null,
   REL_CODIGO           varchar(32) not null,
   SRE_TEMPLATE_JASPER  varchar(100) not null,
   SRE_NOME_PARAMETRO   varchar(100) not null,
   SRE_TEMPLATE_SQL     text not null,
   primary key (SRE_CODIGO)
) ENGINE=InnoDB;

alter table tb_sub_relatorio add constraint FK_R_911 foreign key (REL_CODIGO)
      references tb_relatorio (REL_CODIGO) on delete restrict on update restrict;


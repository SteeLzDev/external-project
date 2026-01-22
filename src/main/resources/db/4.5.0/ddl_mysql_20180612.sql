/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     12/06/2018 17:26:12                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_grau_parentesco                                    */
/*==============================================================*/
create table tb_grau_parentesco
(
   GRP_CODIGO           varchar(32) not null,
   GRP_DESCRICAO        varchar(40) not null,
   primary key (GRP_CODIGO)
) ENGINE=InnoDB;


# 📦 PurchaseRequestList.java - Requisições de Compra

Este componente representa a **ação de listagem de Requisições de Compra** no sistema da FIRSTi, implementada com base na classe `AbstractActionList` da plataforma. Ele organiza a exibição de dados em uma tabela com filtros e comportamentos personalizados, e realiza pré-carregamentos conforme o perfil do usuário.

---

## 🧩 Pacote

```java
package br.com.firsti.packages.purchase.modules.purchaseRequest.actions;
```

---

## ✅ Funcionalidades Implementadas

### 🗂️ Colunas da Tabela

| Campo            | Entidade/Origem     | Descrição                          | Formato     | Largura |
|------------------|---------------------|-------------------------------------|-------------|---------|
| Criação          | `PurchaseRequest`   | Data de criação                     | `DATETIME`  | 136     |
| Depósito         | `Warehouse`         | Nome do depósito                    | Texto       | -       |
| Tipo de Produto  | `ProductType`       | Nome do tipo de produto             | Texto       | -       |
| Quantidade       | `PurchaseRequest`   | Quantidade solicitada               | `DECIMAL`   | 105     |
| Unidade          | `ProductType`       | Unidade de medida                   | Texto (traduzido) | 105     |
| Status           | `PurchaseRequest`   | Status da requisição                | Enum (traduzido)  | 90      |

- Ordenação padrão descendente pelo campo `creation`.
- Suporte a seleção múltipla de registros.
- Clique em uma linha abre a view modal `PurchaseRequestView`.

---

### 🔎 Filtros Disponíveis

| Filtro           | Tipo         | CSS        | Comentário |
|------------------|--------------|-------------|------------|
| Empresa          | Select       | col-12      | Carregamento diferenciado por perfil (admin x colaborador) |
| Depósito         | Select       | col-12      | Carregado com base na empresa selecionada |
| Tipo de Produto  | Select       | col-12      | Lista completa pré-carregada |
| Status           | Select       | col-12      | Traduzido via `LRPurchase.class` |
| Data de Criação  | InputDate    | col-6       | Intervalo entre `creationStart` e `creationEnd` com botão "Limpar" |

---

## 🔄 Pré-Carregamentos Dinâmicos

Implementados no método `onWindowRequest`:

- **Empresas**:
  - Se administrador ou empresa principal: lista completa.
  - Se colaborador: apenas a própria empresa.
- **Pré-seleção da Empresa** no filtro, conforme perfil do usuário.
- **Tipos de Produto** carregados via `ProductType.FIND_ALL`.
- **Status** como enum `PurchaseRequestStatus`.
- (Ponto de expansão): depósitos podem ser carregados dinamicamente conforme a empresa.

---

## 🔧 Classes Utilizadas

- `PurchaseRequest` — entidade principal da listagem.
- `Warehouse`, `ProductType` — usadas para joins e colunas adicionais.
- `Company` — controle de visibilidade e filtro por empresa.
- `PurchaseRequestOnChangeCompany` — ação auxiliar para atualizar filtros dependentes da empresa.

---

## 📌 Requisitos Atendidos

✔️ Tabela com colunas corretamente formatadas  
✔️ Ordenação por criação (descendente)  
✔️ Filtros completos com formatação correta (`col-12`, `col-6`)  
✔️ Pré-seleção de empresa conforme perfil do usuário  
✔️ Carregamento dinâmico de depósitos e tipos de produto  
✔️ Tradução de enums (`LRCore`, `LRPurchase`)  

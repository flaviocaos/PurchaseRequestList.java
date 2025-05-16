package br.com.firsti.packages.purchase.modules.purchaseRequest.actions;

import br.com.firsti.languages.LRCore;
import br.com.firsti.languages.LRProduct;
import br.com.firsti.languages.LRPurchase;
import br.com.firsti.languages.LRStock;
import br.com.firsti.module.actions.AbstractActionList;
import br.com.firsti.module.exceptions.AccessDeniedException;
import br.com.firsti.module.exceptions.ResourceNotFoundException;
import br.com.firsti.module.requests.ActionRequest;
import br.com.firsti.packages.organization.entities.Company;
import br.com.firsti.packages.organization.modules.company.ModuleCompany;
import br.com.firsti.packages.product.entities.Manufacturer;
import br.com.firsti.packages.product.entities.ProductCategory;
import br.com.firsti.packages.product.entities.ProductType;
import br.com.firsti.packages.product.modules.product.actions.ProductInsert;
import br.com.firsti.packages.purchase.entities.PurchaseRequest;
import br.com.firsti.packages.purchase.entities.PurchaseRequest.PurchaseRequestStatus;
import br.com.firsti.packages.purchase.modules.purchaseRequest.ModulePurchaseRequest;
import br.com.firsti.packages.stock.common.StockReferences;
import br.com.firsti.packages.stock.entities.Warehouse;
import br.com.firsti.persistence.EntityManagerWrapper;
import br.com.firsti.services.websocket.messages.output.elements.DataFormat;
import br.com.firsti.services.websocket.messages.output.elements.ElementRequest;
import br.com.firsti.services.websocket.messages.output.elements.items.InputDate;
import br.com.firsti.services.websocket.messages.output.elements.items.Select;
import br.com.firsti.services.websocket.messages.output.elements.items.TableButton;
import br.com.firsti.services.websocket.messages.output.elements.items.TableButton.TableButtonType;

public class PurchaseRequestList extends AbstractActionList<ModulePurchaseRequest> {

    public PurchaseRequestList() {
        super(new Builder<ModulePurchaseRequest>(Access.COMPANY_PRIVATE, TableType.LIST, PurchaseRequest.class)
            .setIconClass("fa-solid fa-cart-arrow-down")
            .addLoadAction(PurchaseRequestOnChangeCompany.class)
            .addJoin(createJoin(Warehouse.class, PurchaseRequest.class).on(StockReferences.ID, StockReferences.WAREHOUSE_ID))
            .addJoin(createJoin(ProductType.class, PurchaseRequest.class).on(StockReferences.ID, StockReferences.PRODUCT_TYPE_ID))
            .addColumn(createColumn(PurchaseRequest.class, "creation", LRCore.CREATION, DataFormat.DATETIME).setMinWidth(136))
            .addColumn(createColumn(Warehouse.class, "name", LRCore.WAREHOUSE))
            .addColumn(createColumn(ProductType.class, "name", LRProduct.PRODUCT_TYPE))
            .addColumn(createColumn(PurchaseRequest.class, "quantity", LRCore.QUANTITY, DataFormat.DECIMAL).setMinWidth(105))
            .addColumn(createColumn(ProductType.class, "unit", LRCore.UNIT).setTranslate(LRCore.class).setMinWidth(105))
            .addColumn(createColumn(PurchaseRequest.class, "status", LRCore.STATUS).setTranslate(LRPurchase.class).setMinWidth(90))
            .setOrder(PurchaseRequest.class, "creation", TableOrderType.DESC)
            .setOnClick(ElementRequest.createModalRequest(PurchaseRequestView.class))
            .setSelection(TableSelection.MULTIPLE)
            .addFilter(createFilter(new Select("company")
                .setLabel(LRCore.COMPANY)
                .addClass("col-12")
                .setOnChange(PurchaseRequestOnChangeCompany.class))
                .setFilter(FilterOperator.EQUAL, Warehouse.class, "companyId"))
            .addFilter(createFilter(new Select("warehouse")
                .setLabel(LRStock.WAREHOUSE)
                .addClass("col-12"))
                .setFilter(FilterOperator.EQUAL, PurchaseRequest.class, "warehouseId"))
            .addFilter(createFilter(new Select("productType")
                .setLabel(LRProduct.PRODUCT_TYPE)
                .addClass("col-12"))
                .setFilter(FilterOperator.EQUAL, PurchaseRequest.class, "productTypeId"))
            .addFilter(createFilter(new Select("status")
                .setLabel(LRCore.STATUS)
                .addClass("col-12")
                .setTranslate(LRPurchase.class))
                .setFilter(FilterOperator.EQUAL, PurchaseRequest.class, "status"))
            .addFilter(createFilter(new InputDate("creationStart")
                .setLabel(LRCore.STARTING_FROM)
                .addClass("col-6")
                .showClear())
                .setFilter(FilterOperator.GRATER_THAN_OR_EQUAL_TO, PurchaseRequest.class, "creation"))
            .addFilter(createFilter(new InputDate("creationEnd")
                .setLabel(LRCore.UP_TO)
                .addClass("col-6")
                .showClear())
                .setFilter(FilterOperator.LESS_THAN_OR_EQUAL_TO, PurchaseRequest.class, "creation"))
            .addButton(new TableButton(TableButtonType.NO_ID, "insert", LRCore.INSERT)
                .setOnClick(ElementRequest.createPopupRequest(PurchaseRequestInsert.class)))
        );
    }

    @Override
    public void onWindowRequest(EntityManagerWrapper entityManager, ActionRequest request, WindowBuilder windowBuilder)
            throws AccessDeniedException, ResourceNotFoundException {

        Company userCompany = request.getUserProfile().getCompany();

        if (request.getUserProfile().isAdministrator() || (userCompany != null && userCompany.isMain())) {
            entityManager.createNamedQuery(Company.FIND_ALL_WITH_SOCIAL_NAME, Company.class)
                .getResultStream()
                .forEach(company -> windowBuilder.getPreloadBuilder().addTo("company", company.getId(), company.getName()));
        } else if (userCompany != null) {
            windowBuilder.getPreloadBuilder().addTo("company", userCompany.getId(), userCompany.getName());
        }

        if (userCompany == null) {
            userCompany = ModuleCompany.getMainCompany();
        }

        windowBuilder.getDataBuilder().addEntityId("company", userCompany.getId());

        entityManager.createNamedQuery(ProductType.FIND_ALL, ProductType.class)
            .getResultStream()
            .forEach(type -> windowBuilder.getPreloadBuilder().addTo("productType", type.getId(), type.getName()));

        windowBuilder.getPreloadBuilder().add("status", PurchaseRequestStatus.class);
    }
}
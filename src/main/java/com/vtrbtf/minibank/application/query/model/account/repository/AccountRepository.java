package com.vtrbtf.minibank.application.query.model.account.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.vtrbtf.minibank.application.query.infrastructure.QuerySideConfiguration;
import com.vtrbtf.minibank.application.query.model.account.repository.view.AccountView;
import com.vtrbtf.minibank.application.query.model.account.repository.view.TransactionView;
import com.vtrbtf.minibank.aggregate.client.account.events.AccountOpened;
import com.vtrbtf.minibank.aggregate.client.account.events.AccountTransactionEvent;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

@Repository
@Value @NonFinal
public class AccountRepository {
    MongoCollection<AccountView> collection;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public AccountRepository(@Qualifier("querySideMongoClient") MongoClient client) {
        collection = client.getDatabase(QuerySideConfiguration.DOMAIN_DB).getCollection("account", AccountView.class);
    }

    public void save(AccountOpened account){
        collection.insertOne(new AccountView(account));
    }

    public void pushTransaction(AccountTransactionEvent transaction, BigDecimal balance){
        collection.updateOne(eq("_id", transaction.getAccountId()), combine(push("transactions", new TransactionView(transaction)), set("balance", balance)));
    }

    public AccountView get(String id){
        return collection.find(eq("_id", id)).first();
    }
}

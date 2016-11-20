package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.PersistentAccountDAO;

/**
 * Created by hp on 20/11/2016.
 */
public class PersistentExpenseManager {
    private Context context = null;

    //Constructor
    public PersistentExpenseManager( Context context) {
        this.context = context;
        setup();
    }


    public void setup()  {

        //Setup AccountDAO object
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(context);
        setAccountsDAO(persistentAccountDAO);

        //Setup TransactionDAO object
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(context);
        setTransactionsDAO(persistentTransactionDAO);

    }
}
package lk.ac.mrt.cse.dbs.simpleexpensemanager.ui;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import java.util.ArrayList;
        import java.util.List;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.database.DBhandler;;


/**
 * Created by hp on 20/11/2016.
 */
public class PersistentAccountDAO implements AccountDAO {

    private Context context;

    //Constructor
    public PersistentAccountDAO(Context context) {
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {

        //Open the database connection
        DBhandler handler = DBhandler.getInstance(context);
        if( handler == null){
            System.out.print("Damn");
        }
        SQLiteDatabase db = handler.getReadableDatabase();

        //Query to select all account numbers from the account table
        String query = "SELECT "+ handler.ACCOUNT_NO +" FROM " + handler.ACCOUNTS_TABLE +" ORDER BY " + handler.ACCOUNT_NO + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> resultSet = new ArrayList<>();

        //Add account numbers to a list
        while (cursor.moveToNext())
        {
            resultSet.add(cursor.getString(cursor.getColumnIndex(handler.ACCOUNT_NO)));
        }

        cursor.close();

        //Return the list of account numbers
        return resultSet;

    }

    @Override
    public List<Account> getAccountsList() {

        DBhandler handler = DBhandler.getInstance(context);
        SQLiteDatabase db = handler.getReadableDatabase();

        //Query to select all the details about all the accounts in the account table
        String query = "SELECT * FROM " + handler.ACCOUNTS_TABLE +" ORDER BY "+handler.ACCOUNT_NO +" ASC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Account> resultSet = new ArrayList<>();

        //Add account details to a list
        while (cursor.moveToNext())
        {
            Account account = new Account(cursor.getString(cursor.getColumnIndex(handler.ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndex(handler.BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(handler.ACCOUNT_HOLDER_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(handler.BALANCE)));

            resultSet.add(account);
        }

        cursor.close();

        //Return list of account objects
        return resultSet;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        DBhandler handler = DBhandler.getInstance(context);
        SQLiteDatabase db = handler.getReadableDatabase();

        //Query to get details of the account specifiec by the account number
        String query = "SELECT * FROM " + handler.ACCOUNTS_TABLE + " WHERE " + handler.ACCOUNT_NO + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //add the details to an account object
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(handler.ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndex(handler.BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(handler.ACCOUNT_HOLDER_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(handler.BALANCE)));
        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("You have selected an invalid account number...!");
        }

        cursor.close();

        //Return the account object
        return account;
    }

    @Override
    public void addAccount(Account account) {

        DBhandler handler = DBhandler.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();

        //Save account details to the account table
        ContentValues values = new ContentValues();
        values.put(handler.ACCOUNT_NO, account.getAccountNo());
        values.put(handler.BANK_NAME, account.getBankName());
        values.put(handler.ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(handler.BALANCE, account.getBalance());

        db.insert(handler.ACCOUNTS_TABLE, null, values);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        DBhandler handler = DBhandler.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();
        //Query to delete a particular account from the account table
        String query = "SELECT * FROM " + handler.ACCOUNTS_TABLE + " WHERE " + handler.ACCOUNT_NO + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //Delete the account if found in the table
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(handler.ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndex(handler.BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(handler.ACCOUNT_HOLDER_NAME)),
                    cursor.getFloat(cursor.getColumnIndex(handler.BALANCE)));
            db.delete(handler.ACCOUNTS_TABLE, handler.ACCOUNT_NO + " = ?", new String[] { accountNo });
            cursor.close();

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("No such account found...!");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        DBhandler handler = DBhandler.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();

        //Retrieve the account details of the selected account
        Account account = getAccount(accountNo);

        //Update the BALANCE if the account is found in the table
        if (account!=null) {

            double new_amount=0;

            //Deduct the AMOUNT is it is an expense
            if (expenseType.equals(ExpenseType.EXPENSE)) {
                new_amount = account.getBalance() - amount;
            }
            //Add the AMOUNT if it is an income
            else if (expenseType.equals(ExpenseType.INCOME)) {
                new_amount = account.getBalance() + amount;
            }

            //Query to update BALANCE in the account table
            String strSQL = "UPDATE "+handler.ACCOUNTS_TABLE +" SET "+handler.BALANCE +" = "+new_amount+" WHERE "+handler.ACCOUNT_NO +" = '"+ accountNo+"'";

            db.execSQL(strSQL);

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("No such account found...!");
        }

    }
}
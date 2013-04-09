package il.ac.huji.todolist;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.parse.ParseException;
import com.parse.ParseObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDAL extends SQLiteOpenHelper {
	  static final String TODO_ITEM_ID_FIELD = "_id";
	  static final String TODO_ITEM_TITLE_FIELD = "title";
	  static final String TODO_ITEM_DUE_FIELD = "due";
	  static final String TODO_ITEMS_TABLE = "todo";
	  
	  public TodoDAL(Context context) {
	    super(context, "todo_db", null, 1);
	    Parse.initialize(context, "xglxRlDpfJtqpvcCvuaHRJmcUDMK36OCSXoqUr5n", "G9tAnMnl8iRosCHECRcCZdtm99lnDH64mzGyIQDT");
	  }
	  
	  public void onCreate(SQLiteDatabase db) {
	    db.execSQL("create table todo ( _id integer primary key autoincrement, title text, due long);");
	  }
	  
	  public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
		  
	  }
	  
	  public boolean insert(ITodoItem item)
	  {
		  SQLiteDatabase db = this.getWritableDatabase();
		  
		  ContentValues todoItemDB = new ContentValues();
		  todoItemDB.put(TODO_ITEM_TITLE_FIELD, item.getTitle());
		  todoItemDB.put(TODO_ITEM_DUE_FIELD, item.getDueDate().getTime());
		  
		  long r = db.insert(TODO_ITEMS_TABLE, null, todoItemDB);
		  if (r == -1){
			  System.out.println("Error inserting to db\n");
		  }
		  
		  ParseObject itemParseObject = new ParseObject("todo");
		  itemParseObject.put(TODO_ITEM_TITLE_FIELD, item.getTitle());
		  itemParseObject.put(TODO_ITEM_DUE_FIELD, item.getDueDate().getTime());
		  try {
			itemParseObject.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return false;
		}
		  
		  return true;
	  }

	  public boolean delete(ITodoItem todoItem) {
		  SQLiteDatabase db = this.getWritableDatabase();
		  
		  long r = db.delete(TODO_ITEMS_TABLE, "title = ?", new String[] {todoItem.getTitle()});
		  if (r == -1){
			  System.out.println("Error inserting to db\n");
		  }
		  
		  ParseQuery query = new ParseQuery("todo");
		  query.whereEqualTo(TODO_ITEM_TITLE_FIELD, todoItem.getTitle());
		  List<ParseObject> items;
		try {
			items = query.find();
			items.get(0).delete();			
		} catch (ParseException e) {
			return false;
		}
		
		return true;
	}
	
	  public boolean update(ITodoItem todoItem) { 
		  ParseQuery query = new ParseQuery("todo");
		  query.whereEqualTo(TODO_ITEM_TITLE_FIELD, todoItem.getTitle());
		  List<ParseObject> items;

		  try {
			items = query.find();
			ParseObject todoParseObj = items.get(0);
			todoParseObj.put(TODO_ITEM_TITLE_FIELD, todoItem.getTitle());			 
			todoParseObj.put(TODO_ITEM_DUE_FIELD, todoItem.getDueDate().getTime());
			todoParseObj.save();
		} catch (ParseException e) {
			return false;
		}
		  return true;
	  }
	  
	  public List<ITodoItem> all() { 
		  ParseQuery query = new ParseQuery("todo");
		  List<ParseObject> items;
		  
			List<ITodoItem> todoList = new ArrayList<ITodoItem>();		  
		  try {
			  
			items = query.find();
			
			for (ParseObject itemObj : items){
				TodoItem todoItem = new TodoItem(itemObj.getString(TODO_ITEM_TITLE_FIELD), new Date(itemObj.getLong(TODO_ITEM_DUE_FIELD)));
				todoList.add(todoItem);
			}
			
		} catch (ParseException e) {
			// pass
		}
		  return todoList;
	  }	
}

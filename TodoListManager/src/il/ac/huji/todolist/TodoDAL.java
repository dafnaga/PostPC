package il.ac.huji.todolist;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
	    Parse.initialize(context, context.getString(R.string.parseApplication), context.getString(R.string.clientKey));
		ParseUser.enableAutomaticUser();
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
		  if (item.getDueDate() == null){
			  todoItemDB.putNull(TODO_ITEM_DUE_FIELD);
		  } else {
			  todoItemDB.put(TODO_ITEM_DUE_FIELD, item.getDueDate().getTime());
		  }
		  

		  
		  long r = db.insert(TODO_ITEMS_TABLE, null, todoItemDB);
		  if (r == -1){
			  System.out.println("Error inserting to db\n");
			  return false;
		  }
		  
		  ParseObject itemParseObject = new ParseObject("todo");
		  itemParseObject.put(TODO_ITEM_TITLE_FIELD, item.getTitle());
		  if (item.getDueDate() != null){
			  itemParseObject.put(TODO_ITEM_DUE_FIELD, item.getDueDate().getTime());			 
		  }
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
			  return false;
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
			//bhtodoParseObj.put(TODO_ITEM_TITLE_FIELD, todoItem.getTitle());
			if (todoItem.getDueDate() == null){
				todoParseObj.remove(TODO_ITEM_DUE_FIELD);
			} else { 
				todoParseObj.put(TODO_ITEM_DUE_FIELD, todoItem.getDueDate().getTime());
			}
			todoParseObj.save();
		} catch (ParseException e) {
			return false;
		}
		  
		  SQLiteDatabase db = this.getWritableDatabase();
		  ContentValues todoItemDB = new ContentValues();
		  todoItemDB.put(TODO_ITEM_TITLE_FIELD, todoItem.getTitle());
		  if (todoItem.getDueDate() == null){
			  todoItemDB.putNull(TODO_ITEM_DUE_FIELD);
		  } else {
			  todoItemDB.put(TODO_ITEM_DUE_FIELD, todoItem.getDueDate().getTime());
		  }
		  
		  int r = db.update("todo", todoItemDB, TODO_ITEM_TITLE_FIELD + "= ?" , new String[] {todoItem.getTitle()});
		  if (r == -1){
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
				TodoItem todoItem;
				if(itemObj.has(TODO_ITEM_DUE_FIELD)){
					todoItem = new TodoItem(itemObj.getString(TODO_ITEM_TITLE_FIELD), new Date(itemObj.getLong(TODO_ITEM_DUE_FIELD)));					
				} else {
					todoItem = new TodoItem(itemObj.getString(TODO_ITEM_TITLE_FIELD), null);
				}
				todoList.add(todoItem);
			}
			
		} catch (ParseException e) {
			// pass
		}
		  return todoList;
	  }	
}

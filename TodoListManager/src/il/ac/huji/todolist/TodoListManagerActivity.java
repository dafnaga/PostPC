package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class TodoListManagerActivity extends Activity {
	
	class TodoListCursorAdapter extends SimpleCursorAdapter{
		
		
		public TodoListCursorAdapter(Context context, int resource,
				Cursor cursor, String[] from, int[] to) {
			super(context, resource, cursor, from, to); 
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			boolean b;
			
			Cursor todoListCursor = getCursor();
			
			b = todoListCursor.moveToFirst();
			b = todoListCursor.move(position);
			
			int titleIndex = todoListCursor.getColumnIndex(TodoDAL.TODO_ITEM_TITLE_FIELD);
			int dueIndex = todoListCursor.getColumnIndex(TodoDAL.TODO_ITEM_DUE_FIELD);

			String itemTitle = todoListCursor.getString(titleIndex);
			Date itemDate = new Date(todoListCursor.getLong(dueIndex));
			
			Date currentDate = new Date();

			LayoutInflater inflater = LayoutInflater.from(getBaseContext());
			View row = inflater.inflate(R.layout.activity_todo_list_row, null);
			((TextView)row.findViewById(R.id.txtTodoTitle)).setText(itemTitle);
			
			if (itemDate == null){
				((TextView)row.findViewById(R.id.txtTodoDueDate)).setText("No due date");
				return row;
			} 

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");			
			String dateString = df.format(itemDate);
			
			((TextView)row.findViewById(R.id.txtTodoDueDate)).setText(dateString);
			if (itemDate.before(currentDate)){
				((TextView)row.findViewById(R.id.txtTodoDueDate)).setTextColor(Color.RED);
				((TextView)row.findViewById(R.id.txtTodoTitle)).setTextColor(Color.RED);
			}
			return row;
		}
	}
		
	private TodoListCursorAdapter adapter; 
	final int NEW_TODO_ITEM_REQUEST = 1;
	
	public static final String NEW_ITEM_DUE_DATE = "dueDate";
	public static final String NEW_ITEM_TITLE = "title";
	private TodoDAL todoDal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
						
		ListView itemsList = (ListView)findViewById(R.id.lstTodoItems); 

		todoDal = new TodoDAL(this);
		Cursor cursor = getFreshCursor();
		
		String[] from = new String[] {TodoDAL.TODO_ITEM_TITLE_FIELD, TodoDAL.TODO_ITEM_DUE_FIELD};
		int[] to = new int[] { R.id.txtTodoTitle, R.id.txtTodoDueDate };
		adapter = new TodoListCursorAdapter(this, R.layout.activity_todo_list_row, cursor, from, to);
		itemsList.setAdapter(adapter);
		registerForContextMenu(itemsList);		
	}

	private Cursor getFreshCursor() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = todoDal.getWritableDatabase();
		Cursor cursor = db.query("todo", new String[] {TodoDAL.TODO_ITEM_TITLE_FIELD, TodoDAL.TODO_ITEM_DUE_FIELD, TodoDAL.TODO_ITEM_ID_FIELD}, null, null, null, null, null);
		return cursor;
	}

	private void refreshList(){
		adapter.changeCursor(getFreshCursor());
		adapter.notifyDataSetChanged();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_todo_list_manager, menu);
		return true;
	}
	
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
	    super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.todo_list_context_menu, menu);
	    
	    // Get the list
	    ListView list = (ListView)v;

	    // Get the list item position    
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	    int position = info.position;
	    
	    String itemTitle = ((Cursor)(list.getItemAtPosition(position))).getString(0);
	    menu.setHeaderTitle(itemTitle);
	    
	    if (!itemTitle.startsWith("Call ")){
	    	MenuItem call = menu.findItem(R.id.menuItemCall);
	        call.setVisible(false);
	        call.setEnabled(false);
	    }
	    else{
	    	MenuItem call = menu.findItem(R.id.menuItemCall);
	    	call.setTitle(itemTitle);
	    }
	} 
    

    @Override
    public boolean onContextItemSelected(MenuItem item){
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
    	int position = info.position;
    	if(item.getItemId() == R.id.menuItemDelete){
    		deleteClick(position);
    	}
    	else if(item.getItemId() == R.id.menuItemCall){
    		callClick(position);
    	}
    	else {
    		return false;
    	}
    	
    	return true;
    }
    
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menuItemAdd:
			addClick();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}
	
	public void addClick(){
		Intent addTodo = new Intent(this, AddNewTodoItemActivity.class);
		startActivityForResult(addTodo, this.NEW_TODO_ITEM_REQUEST);		
	}
	

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1){
			TodoItem newItem = new TodoItem((String)data.getSerializableExtra(NEW_ITEM_TITLE),(Date)data.getSerializableExtra(NEW_ITEM_DUE_DATE));
			todoDal.insert(newItem);
			refreshList();
		}
	}

	
	public void deleteClick(int position){
			Cursor item = (Cursor)((ListView)findViewById(R.id.lstTodoItems)).getItemAtPosition(position);
			String title = item.getString(0);
			Date date = new Date(item.getLong(1));
			
			TodoItem todoItem = new TodoItem(title, date);
			todoDal.delete(todoItem);
			refreshList();
	}
	
	public void callClick(int position){
		String dialStr = "tel:" + ((TodoItem)((ListView)findViewById(R.id.lstTodoItems)).getItemAtPosition(position)).getTitle().substring(5);
		Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse(dialStr));
		startActivity(dial); 
	}

}

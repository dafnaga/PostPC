package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;


public class TodoListManagerActivity extends Activity {
	
	class TodoListArrayAdapter extends ArrayAdapter<TodoItem>{

		List<TodoItem> todoListItems;
		
		public TodoListArrayAdapter(Context context, int resource,
				List<TodoItem> todoListItems) {
			super(context, resource, todoListItems);
			this.todoListItems = todoListItems;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			String itemTitle = todoListItems.get(position).getTitle();
			Date itemDate = todoListItems.get(position).getDueDate();
			Date currentDate = new Date();

			LayoutInflater inflater = LayoutInflater.from(getContext());
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
		
	List<TodoItem> todoList = new ArrayList<TodoItem>();
	private TodoListArrayAdapter adapter; 
	final int NEW_TODO_ITEM_REQUEST = 1;
	
	public static final String NEW_ITEM_DUE_DATE = "dueDate";
	public static final String NEW_ITEM_TITLE = "title";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
				
		adapter = new TodoListArrayAdapter(this, R.layout.activity_todo_list_row, todoList);
		ListView itemsList = (ListView)findViewById(R.id.lstTodoItems); 
		itemsList.setAdapter(adapter);
		registerForContextMenu(itemsList);
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
	    
	    String itemTitle = ((TodoItem)(list.getItemAtPosition(position))).getTitle();
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
			todoList.add(newItem);
			adapter.notifyDataSetChanged();
		}
	}

	
	public void deleteClick(int position){
		try{
			TodoItem item = (TodoItem)((ListView)findViewById(R.id.lstTodoItems)).getItemAtPosition(position);
			todoList.remove(item);
			adapter.notifyDataSetChanged();
		}
		catch(Exception e){
			// do nothing
		}
	}
	
	public void callClick(int position){
		String dialStr = "tel:" + ((TodoItem)((ListView)findViewById(R.id.lstTodoItems)).getItemAtPosition(position)).getTitle().substring(5);
		Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse(dialStr));
		startActivity(dial); 
	}

}

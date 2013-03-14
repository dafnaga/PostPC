package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {
	
	class TodoListArrayAdapter extends ArrayAdapter<String>{

		List<String> todoListItems;
		
		public TodoListArrayAdapter(Context context, int resource,
				List<String> todoListItems) {
			super(context, resource, todoListItems);
			this.todoListItems = todoListItems;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			String item = todoListItems.get(position);
			
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View row = inflater.inflate(R.layout.activity_todo_list_row, null);
			
			((TextView)row.findViewById(R.id.txtTodoItem)).setText(item);
			if (position % 2 == 0){
				((TextView)row.findViewById(R.id.txtTodoItem)).setTextColor(Color.RED);
			} else {
				((TextView)row.findViewById(R.id.txtTodoItem)).setTextColor(Color.BLUE);
			}
			return row;
		}
	}
	
	List<String> todoList = new ArrayList();
	private TodoListArrayAdapter adapter; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
				
		adapter = new TodoListArrayAdapter(this, R.layout.activity_todo_list_row, todoList);
		((ListView)findViewById(R.id.lstTodoItems)).setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_todo_list_manager, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menuItemAdd:
			addClick();
			break;
		case R.id.menuItemDelete:
			deleteClick();
			break;
		default:
			sdfgsdfg
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}
	
	public void addClick(){
		try{
			String newTodo = ((EditText)findViewById(R.id.edtNewItem)).getText().toString();
			todoList.add(newTodo);
			adapter.notifyDataSetChanged();
		}
		catch(Exception e){
			// do nothing
		}
	}
	
	public void deleteClick(){
		try{
			String item = (String)((ListView)findViewById(R.id.lstTodoItems)).getSelectedItem();
			todoList.remove(item);
			adapter.notifyDataSetChanged();
		}
		catch(Exception e){
			// do nothing
		}
	}

}
